const HOTEL_API_BASE = 'http://localhost:8888/api/v1/hotels';
const BOOKING_API_BASE = 'http://localhost:8888/api/v1/bookings';

export async function getTotalHotels(email) {
    const token = localStorage.getItem('Bearer');
        try {
            const response = await fetch(`${HOTEL_API_BASE}/owner/admin?email=${email}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) throw new Error('Failed to fetch hotel count');
            
            const text = await response.text();
            return parseInt(text) || 0;
        } catch (error) {
            console.error('Error fetching hotel count:', error);
            return 0;
        }
}

export async function getBookingStats(email) {
    const token = localStorage.getItem('Bearer');
    try {
        const response = await fetch(`${BOOKING_API_BASE}/owner/admin?email=${email}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) throw new Error('Failed to fetch booking stats');

        const result = await response.json();
        if (result.code === 200) {
            return result.data;
        }
        return { total_bookings: 0, total_revenue: 0 };
    } catch (error) {
        console.error('Error fetching booking stats:', error);
        return { total_bookings: 0, total_revenue: 0 };
    }
};