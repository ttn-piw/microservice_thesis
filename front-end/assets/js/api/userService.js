const USER_API_URL = 'http://localhost:8888/api/v1/users';

export async function getUserByEmail(email) {
    try {
        const response = await fetch(`${USER_API_URL}/email?email=${email}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return await response.json();
    } catch (error) {
        console.error("Error fetching user by email:", error);
        throw error;
    }
}

export async function updateUserProfile(email, data) {
    console.log("Updating profile for email:", email, "with data:", data);
    // debugger;
    try {
        const response = await fetch(`${USER_API_URL}/updateInfo/email?email=${email}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        return await response.json();
    } catch (error) {
        console.error("Error updating profile:", error);
        throw error;
    }
}
