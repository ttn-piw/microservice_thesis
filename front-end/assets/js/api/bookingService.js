const BASE_URL = 'http://localhost:8888/api/v1/bookings';

export async function getMyBookings() {
    const token = localStorage.getItem('Bearer');
    
    if (!token) {
        return { code: 401, message: "Authentication required.", data: [] };
    }

    try {
        const response = await fetch(`${BASE_URL}/me/bookings`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        const data = await response.json();
        
        if (response.status === 401 || data.code === 401) {
            localStorage.removeItem('Bearer');
            throw new Error("Session expired. Please log in again.");
        }
        
        return data;
        
    } catch (error) {
        console.error("Error fetching my bookings:", error);
        return { code: 500, message: error.message, data: [] };
    }
}