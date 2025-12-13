import { getUserByEmail, updateUserProfile, deactivateUserAccount } from '../api/userService.js';
import { deactivateAccount } from '../api/authService.js';  
import { parseJwt } from '../utils/jwtUtils.js';

async function handleLoadProfile(email) {
    try {
        const resJson = await getUserByEmail(email);

        if (resJson.code === 200 && resJson.data && resJson.data.data) {
            const profile = resJson.data.data;
            console.log("Populating UI with profile:", profile);
        
            showUI(profile, email); 
        } else {
            showToast("Error", "Could not find user profile.", "error");
        }
    } catch (error) {
        console.error("Error:", error); 
        
        showToast("Error", "Something went wrong: " + error.message, "error");
    }
}

async function handleUpdateProfile(email) {
    if (!email) return;

    const updateData = {
        name: document.getElementById("fullName").value,
        phone: document.getElementById("phone").value,
        gender: document.getElementById("gender").value,
        dob: document.getElementById("birthday").value, 
        avatar: document.getElementById("avatarUrl").value
    };

    try {
        const resJson = await updateUserProfile(email, updateData);

        if (resJson.code === 200 || resJson.status === 200) {
            showToast("Success", "Profile updated successfully!", "success");
            
            document.getElementById("displayUsername").textContent = updateData.name;
            if(updateData.avatar) document.getElementById("profileAvatar").src = updateData.avatar;
        } else {
            showToast("Error", resJson.message || "Update failed", "error");
        }
    } catch (error) {
        showToast("Error", "Failed to update profile.", "error");
    }
}

async function handleDeactivate(email) {
    try {
        const resJson = await deactivateAccount(email);

        if (resJson && (resJson.code === 200 || resJson.status === 200)) {
            closeDeactivateModal();
            showToast("Account Deactivated", "Redirecting...", "success");
            setTimeout(() => {
                localStorage.clear();
                window.location.href = "login.html";
            }, 2000);
        } else {
            showToast("Error", "Could not deactivate account.", "error");
        }
    } catch (error) {
        showToast("Error", "Failed to deactivate account.", "error");
    }
}

function showUI(data, email) {

    const setElementValue = (id, value) => {
        const element = document.getElementById(id);
        if (element) {
            element.value = value || "";
        } else {
            console.warn(`Warning: Element with id '${id}' not found in HTML.`);
        }
    };

    setElementValue("fullName", data.name);
    setElementValue("email", email);
    setElementValue("phone", data.phone);
    setElementValue("gender", data.gender || "Male");

    if (data.birthday) {
        setElementValue("birthday", data.birthday.split('T')[0]);
    }
    if (data.avatar) {
        setElementValue("avatarUrl", data.avatar);
        const imgEl = document.getElementById("profileAvatar");
        if (imgEl) imgEl.src = data.avatar;
    }

    const displayUser = document.getElementById("displayUsername");
    if (displayUser) displayUser.textContent = data.name || "User";
    
    const displayEmail = document.getElementById("displayEmail");
    if (displayEmail) displayEmail.textContent = email || "No email provided";
}

// Global functions for HTML onclick
window.openDeactivateModal = () => document.getElementById("deactivateModal").classList.remove("hidden");
window.closeDeactivateModal = () => document.getElementById("deactivateModal").classList.add("hidden");

function showToast(title, message, type = "success") {
    const toast = document.getElementById("toast");
    const toastContent = document.getElementById("toastContent");
    const toastIcon = document.getElementById("toastIcon");

    if (type === "success") {
        toastContent.className = "bg-white border-l-4 border-green-500 shadow-lg rounded p-4 flex items-center gap-3";
        toastIcon.className = "ri-checkbox-circle-fill text-green-500 text-xl";
    } else {
        toastContent.className = "bg-white border-l-4 border-red-500 shadow-lg rounded p-4 flex items-center gap-3";
        toastIcon.className = "ri-error-warning-fill text-red-500 text-xl";
    }

    document.getElementById("toastTitle").textContent = title;
    document.getElementById("toastMessage").textContent = message;

    toast.classList.remove("translate-x-full");
    setTimeout(() => toast.classList.add("translate-x-full"), 3000);
}

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("Bearer");
    const email = parseJwt(token)?.sub;

    if (!token) {
        alert("Please log in to view this page.");
        window.location.href = "login.html";
        return;
    }

    await handleLoadProfile(email);
    
    document.getElementById("btnSave").addEventListener("click", async () => {
        await handleUpdateProfile(email);
    });

    document.getElementById("btnReset").addEventListener("click", async () => {
        await handleLoadProfile(email);
    });

    document.getElementById("confirmDeactivateBtn").addEventListener("click", async () => {
        await handleDeactivate(email);
    });

    document.getElementById("avatarUrl").addEventListener("input", function() {
        if(this.value) document.getElementById("profileAvatar").src = this.value;
    });
});