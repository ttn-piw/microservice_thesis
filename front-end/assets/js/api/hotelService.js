const HOTEL_API_URL = 'http://localhost:8888/api/v1/hotels';
const HOTEL_API_URL_TEST = 'http://localhost:8082/api/v1/hotels';

//Return promise containing ApiResponse object
export const getHotelMainPage = async () => {
    const response = await fetch(`${HOTEL_API_URL}/`);
    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Failed to fetch main page hotels');
    }
    return apiResponse;
};

export const getAllHotels = async () => {
    const response = await fetch(`${HOTEL_API_URL}/getAll`);
    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Failed to fetch all hotels');
    }
    return apiResponse;
};

export const getHotelById = async (uuid) => {
    const response = await fetch(`${HOTEL_API_URL}/${uuid}`);
    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Failed to fetch hotel by ID');
    }
    return apiResponse;
};

export const searchHomePage = async (searchData) => {
    const params = new URLSearchParams();

    if (!searchData.checkIn || !searchData.bookedRoom) {
        throw new Error("Check-in date and room count are required.");
    }

    if (searchData.city) {
        params.append('city', searchData.city);
    }
    params.append('checkIn', searchData.checkIn); 
    
    if (searchData.checkOut) {
        params.append('checkOut', searchData.checkOut);
    }
    params.append('bookedRoom', searchData.bookedRoom); 

    const response = await fetch(`${HOTEL_API_URL}/homeSearch`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    });

    const apiResponse = await response.json();
    if (!response.ok) {
        throw new Error(apiResponse.message || 'Search failed');
    }
    return apiResponse;
};

/**
 * @param {object} searchParams - { city, country, rating_star, minPrice, maxPrice, keyword }
 * @returns {Promise<object>} 
 */
export const searchHotels = async (searchParams) => {
    // GET /search?city=...&country=...
    const params = new URLSearchParams();
    
    Object.keys(searchParams).forEach(key => {
        if (searchParams[key] !== null && searchParams[key] !== undefined && searchParams[key] !== '') {
            params.append(key, searchParams[key]);
        }
    });

    const response = await fetch(`${HOTEL_API_URL}/search?${params.toString()}`);
    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Hotel search failed');
    }
    return apiResponse;
};

export async function getRoomAvailability(hotelId, checkIn, checkOut) {
    const params = new URLSearchParams({ hotelId, checkIn, checkOut });
    const url = `${HOTEL_API_URL}/availability?${params.toString()}`;
    
    console.log("Fetching availability from:", url);

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error("Failed to fetch room availability:", error);
        return {
            code: 500,
            message: error.message,
            data: []
        };
    }
}

export const createNewHotel = async (newHotelRequest) => {
    const response = await fetch(`${HOTEL_API_URL}/createHotel`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newHotelRequest)
    });

    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Failed to create hotel');
    }
    return apiResponse;
};

/**
 * @param {string} id 
 * @param {object} hotelUpdateRequest 
 * @returns {Promise<object>} 
 */
export const updateHotelInfo = async (id, hotelUpdateRequest) => {
    // PUT /updateHotel/{id}
    const response = await fetch(`${HOTEL_API_URL}/updateHotel/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(hotelUpdateRequest)
    });

    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Failed to update hotel');
    }
    return apiResponse;
};


export const deleteHotelById = async (hotelId) => {
    // DELETE /{id}
    const response = await fetch(`${HOTEL_API_URL}/${hotelId}`, {
        method: 'DELETE',
        headers: {
        }
    });

    const apiResponse = await response.json();

    if (!response.ok) {
        throw new Error(apiResponse.message || 'Failed to delete hotel');
    }
    return apiResponse;
};


// --- ENDPOINT RETURN TEXT ---
export const getHotelNameSnapshot = async (hotelId) => {
    // GET /{hotelId}/getHotelNameSnapshot
    const response = await fetch(`${HOTEL_API_URL}/${hotelId}/getHotelNameSnapshot`);

    if (!response.ok) {
        try {
            const apiResponse = await response.json();
            throw new Error(apiResponse.message || 'Failed to get hotel snapshot');
        } catch (e) {
            const textError = await response.text();
            throw new Error(textError || 'Failed to get hotel snapshot');
        }
    }

    return await response.text();
};