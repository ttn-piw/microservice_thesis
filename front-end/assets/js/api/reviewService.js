const REVIEW_API_URL = 'http://localhost:8888/api/v1/reviews'; 

export const getUserReviews = async (userEmail) => {
    const response = await fetch(`${REVIEW_API_URL}/user/${userEmail}`);
    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Failed to fetch user reviews');
    }

    return apiResponse;
};

export const createReview = async (reviewData) => {
    const token = localStorage.getItem('Bearer'); 
    
    if (!token) {
        throw new Error("Unauthorized: No token found");
    }

    const response = await fetch(`${REVIEW_API_URL}/review`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(reviewData)
    });

    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Failed to post review');
    }

    return apiResponse;
};

export const getMyBookingsWithReviews = async (email) => {
    const token = localStorage.getItem('Bearer');
    if (!token) throw new Error("Unauthorized");

    const response = await fetch(`${REVIEW_API_URL}/me?email=${email}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    const apiResponse = await response.json();
    if (!response.ok) {
        throw new Error(apiResponse.message || 'Failed to fetch bookings');
    }

    return apiResponse;
};

export const getReviewsByHotelId = async (hotelId) => {
    try {
        const response = await fetch(`${REVIEW_API_URL}/hotel/${hotelId}`);
        const apiResponse = await response.json();

        if (!response.ok) {
            console.warn("Endpoint /hotel/{id} not found, falling back to /all filtering");
            return await getReviewsByHotelIdFallback(hotelId);
        }

        return apiResponse;
    } catch (error) {
        console.error("Error fetching hotel reviews:", error);
        throw error;
    }
};

async function getReviewsByHotelIdFallback(hotelId) {
    const response = await fetch(`${REVIEW_API_URL}/all`);
    const data = await response.json(); 
   
    if (Array.isArray(data)) {
        return {
            code: 200,
            data: data.filter(r => r.hotelId === hotelId)
        };
    } 
    return { code: 404, data: [] };
}