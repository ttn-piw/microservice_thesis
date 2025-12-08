const REVIEW_API_BASE_URL = 'http://localhost:8888/api/v1/reviews';
import { parseJwt } from '../utils/jwtUtils.js';

function getToken() {
    const token = localStorage.getItem('Bearer');
    if (!token) {
        throw new Error("UNAUTHORIZED: Token not found.");
    }
    return token;
}

export async function getOwnerReviews() {
    try {
        const token = getToken();
        const email = parseJwt(token).sub;
        
        const response = await fetch(`${REVIEW_API_BASE_URL}/owner?email=${email}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 401 || response.status === 403) {
            throw new Error("Session expired or unauthorized.");
        }
        
        const data = await response.json(); 
        
        if (data.code !== 200 || !response.ok) {
            throw new Error(data.message || "Failed to fetch reviews.");
        }
        
        return data.data;

    } catch (error) {
        console.error("Error fetching reviews:", error);
        throw error;
    }
}