const BOOKING_API_BASE_URL = 'http://localhost:8888/api/v1/bookings';
import { parseJwt } from '../utils/jwtUtils.js';

function getToken() {
    const token = localStorage.getItem('Bearer');
    if (!token) {
        throw new Error("UNAUTHORIZED: Token not found.");
    }
    return token;
}

export async function getAllBookingsAdmin() {
    try {
        const token = getToken();
        const email = parseJwt(token).sub;

        const response = await fetch(`${BOOKING_API_BASE_URL}/owner?email=${email}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 401 || response.status === 403) {
            throw new Error("Session expired or unauthorized. (Booking API)");
        }
        
        const data = await response.json(); 
        if (data.code !== 200 || !response.ok) {
            throw new Error(data.message || "Failed to fetch bookings.");
        }
        
        return data.data;

    } catch (error) {
        console.error("Error fetching bookings:", error);
        throw error;
    }
}

export async function cancelBookingAdmin(bookingId) {
    // PUT /api/v1/bookings/admin/{id}/cancel
    try {
        const token = getToken();
        const response = await fetch(`${BOOKING_API_BASE_URL}/admin/${bookingId}/cancel`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        const data = await response.json();
        if (!response.ok || data.code !== 200) {
            throw new Error(data.message || "Failed to cancel booking.");
        }
        return true;
    } catch (error) {
        console.error(`Error cancelling booking ${bookingId}:`, error);
        throw error;
    }
}

export async function deleteBookingAdmin(bookingId) {
    // DELETE /api/v1/bookings/admin/{booking_id}
    try {
        const token = getToken();
        const response = await fetch(`${BOOKING_API_BASE_URL}/admin/${bookingId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const data = await response.json();
        if (!response.ok || data.code !== 200) {
            throw new Error(data.message || "Failed to delete booking.");
        }
        return true;
    } catch (error) {
        console.error(`Error deleting booking ${bookingId}:`, error);
        throw error;
    }
}