import { registerUser } from '../api/authService.js';

document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("registerForm");
    const responseMessageEl = document.getElementById("responseMessage");

    registerForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const name = document.getElementById("name").value;
        const username = document.getElementById("username").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const rePassword = document.getElementById("rePassword").value;
        const phone = document.getElementById("phone").value;
        const gender = document.getElementById("gender").value;
        const birthday = document.getElementById("birthday").value;
        const avatar = document.getElementById("avatar").value || null;

        const registerButton = event.target.querySelector('button[type="submit"]');

        //Delete previous messages
        responseMessageEl.textContent = '';
        responseMessageEl.className = 'mt-3 text-center';

        if (password !== rePassword) {
            responseMessageEl.textContent = 'Password is not matched';
            responseMessageEl.classList.add('text-danger');
            return;
        }

        registerButton.disabled = true;
        registerButton.textContent = 'Creating...';

        //Create register data object
        const registerData = {
            name,
            username,
            email,
            password,
            rePassword, 
            phone,
            gender,
            birthday,
            avatar
        };
    
        try {
            const apiResponse = await registerUser(registerData);
            console.log("Registration success:", apiResponse);

            responseMessageEl.textContent = apiResponse.message + " Redirecting to login...";
            responseMessageEl.classList.add('text-success');

            setTimeout(() => {
                window.location.href = '/pages/login.html';
            }, 2000);

        } catch (error) {
            console.error("Registration error:", error.message);
            responseMessageEl.textContent = error.message;
            responseMessageEl.classList.add('text-danger');
            
            registerButton.disabled = false;
            registerButton.textContent = 'Create Account';
        }
    });
});