const REVIEW_API_URL = 'http://localhost:8888/api/v1/reviews'; 

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