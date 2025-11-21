
import { loginUser } from '../api/authService.js';
import { parseJwt } from '../utils/jwtUtils.js';


document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const responseMessageEl = document.getElementById("responseMessage");

    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const loginButton = event.target.querySelector('button[type="submit"]');

        loginButton.disabled = true;
        loginButton.textContent = 'Logging in...';
        responseMessageEl.textContent = '';
        responseMessageEl.className = 'mt-3 text-center';

        try {
            const apiResponse = await loginUser(email, password);
            console.log("Login success:", apiResponse);

            if (apiResponse.data && apiResponse.data.token) {
                localStorage.setItem('Bearer', apiResponse.data.token);
                
                const token = localStorage.getItem('Bearer'); 

            if (token) {
                const payload = parseJwt(token);

                if (payload && payload.scope) {
                    const userScope = payload.scope.toUpperCase();
                    console.log("User scope:", userScope);

                    if (userScope.includes('ADMIN')) {
                        window.location.href = 'admin-page.html';
                        return; 
                    } 
                }
            }

                responseMessageEl.textContent = 'Login successful! Redirecting...';
                responseMessageEl.classList.add('text-success');

                window.location.href = 'main-page.html'; 
            } else {
                throw new Error(apiResponse.message || 'Login response did not contain a token.');
            }

        } catch (error) {
            console.error("Login error:", error.message);
            responseMessageEl.textContent = error.message;
            responseMessageEl.classList.add('text-danger');
        
        } finally {
            loginButton.disabled = false;
            loginButton.textContent = 'Login';
        }
    });
});