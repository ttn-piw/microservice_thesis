const AUTH_API_URL = 'http://localhost:8888/api/v1/auth';

/**
 * @param {string} email
 * @param {string} password
 * @returns {Promise<object>} 
 */
export const loginUser = async (email, password) => {
    const loginRequest = {
        email: email,
        password: password
    };

    const response = await fetch(`${AUTH_API_URL}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginRequest)
    });

    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Login failed');
    }

    return apiResponse;
};

/**
 * @param {object} registerData 
 * @returns {Promise<object>}
 */
export const registerUser = async (registerData) => {
    // registerData object 
    // { name, username, email, password, rePassword, gender, phone, birthday, avatar }
    const response = await fetch(`${AUTH_API_URL}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(registerData)
    });

    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Registration failed');
    }

    return apiResponse;
};