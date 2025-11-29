const BASE_URL = 'http://localhost:8888/api/v1/hotels';
const FILE_UPLOAD_BASE_URL = 'http://localhost:8888/api/v1/files';
const ROOM_TYPE_API_URL = 'http://localhost:8888/api/v1/roomTypes'; 

function getToken() {
    const token = localStorage.getItem('Bearer');
    console.log("Retrieved token from localStorage:", token);
    if (!token) {
        throw new Error("UNAUTHORIZED: Token not found.");
    }
    return token;
}

export async function getAllHotelsAdmin() {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/getAll`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 401 || response.status === 403) {
            throw new Error("Session expired or unauthorized. (HTTP 40x)");
        }
        
        const data = await response.json(); // Safely parse JSON here
        
        if (data.code !== 200 || !response.ok) {
            throw new Error(data.message || "Failed to fetch hotels (Server reported error).");
        }
        
        return data.data; 
    } catch (error) {
        console.error("Error fetching hotels:", error);
        throw error;
    }
}

export async function getHotelById(hotelId) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/${hotelId}`, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.status === 401 || response.status === 403) {
            throw new Error("Session expired or unauthorized.");
        }
        
        const data = await response.json();
        if (data.code !== 200 || !response.ok) {
            throw new Error(data.message || "Failed to fetch hotel details.");
        }
        return data.data;
    } catch (error) {
        console.error(`Error fetching hotel ${hotelId}:`, error);
        throw error;
    }
}

export async function createNewHotel(hotelData) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/createHotel`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(hotelData)
        });
        
        const data = await response.json();
        if (!response.ok || data.code !== 200) {
            throw new Error(data.message || "Failed to create hotel.");
        }
        console.log("Hotel Id", data.data);
        return data.data; 
    } catch (error) {
        console.error("Error creating hotel:", error);
        throw error;
    }
}

export async function uploadHotelImage(file, hotelId) {
    console.log("Hotel ID when uploading image:", hotelId);
    try {
        const token = getToken();
        const formData = new FormData();
        formData.append('file', file);
        formData.append('hotelId', hotelId);

        const response = await fetch(`${FILE_UPLOAD_BASE_URL}/hotel-images/upload`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`, 
            },
            body: formData,
        });

        const data = await response.json();

        if (!response.ok || response.status !== 200 || data.error) {
            throw new Error(data.body || "Upload failed.");
        }

        return data;

    } catch (error) {
        console.error("Error uploading hotel image:", error);
        throw error;
    }
}


export async function updateHotelInfo(hotelId, hotelData) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/updateHotel/${hotelId}`, {
            method: 'PUT', 
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(hotelData)
        });
        
        const data = await response.json();
        if (!response.ok || data.code !== 200) {
            throw new Error(data.message || "Failed to update hotel.");
        }
        return data.data;
    } catch (error) {
        console.error("Error updating hotel:", error);
        throw error;
    }
}

export async function deleteHotelById(hotelId) {
    try {
        const token = getToken();
        const response = await fetch(`${BASE_URL}/${hotelId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.status === 200) {
            const data = await response.json();
            if (data.code !== 200) {
                 throw new Error(data.message || "Deletion failed on server side.");
            }
            return true;
        } else {
            throw new Error(`Deletion failed with status: ${response.status}`);
        }
    } catch (error) {
        console.error("Error deleting hotel:", error);
        throw error;
    }
}

export async function getRoomTypeById(roomTypeId) {
    // GET /api/v1/roomTypes/{roomTypeId}
    console.log("Fetching room type with ID:", roomTypeId);
    try {
        const token = getToken();
        const response = await fetch(`${ROOM_TYPE_API_URL}/${roomTypeId}`, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.status === 401 || response.status === 403) {
            throw new Error("Session expired or unauthorized.");
        }
        
        const data = await response.json(); 
        
        if (data.code !== 200 || !response.ok) {
            throw new Error(data.message || "Failed to fetch room type details.");
        }
        
        return data.data;

    } catch (error) {
        console.error(`Error fetching room type ${roomTypeId}:`, error);
        throw error;
    }
}

export async function getRoomTypesByHotelId(hotelId) {
    try {
        const token = getToken();
        const response = await fetch(`${ROOM_TYPE_API_URL}/hotel/${hotelId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 401 || response.status === 403) {
            throw new Error("Session expired or unauthorized. (Room Type API)");
        }
        
        const data = await response.json(); 
        
        if (data.code !== 200 || !response.ok) {
            throw new Error(data.message || "Failed to fetch room types.");
        }
        
        return data.data; 

    } catch (error) {
        console.error(`Error fetching room types for ${hotelId}:`, error);
        throw error;
    }
}

export async function createNewRoomType(roomTypeData, hotelId) {
    try {
        const token = getToken();
        const response = await fetch(`${ROOM_TYPE_API_URL}/hotel/${hotelId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(roomTypeData)
        });
        
        const data = await response.json();
        if (!response.ok || data.code !== 200) {
            throw new Error(data.message || "Failed to create room type.");
        }
        return data.data;
    } catch (error) {
        console.error("Error creating room type:", error);
        throw error;
    }
}

export async function uploadRoomTypeImage(file, roomTypeId) {
    try {
        const token = getToken();
        const formData = new FormData();
        formData.append('file', file);
        formData.append('roomTypeId', roomTypeId);

        const response = await fetch(`${FILE_UPLOAD_BASE_URL}/room-images/upload`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`, 
            },
            body: formData,
        });

        const data = await response.json();

        if (!response.ok || response.status !== 200 || data.error) {
            throw new Error(data.body || "Room image upload failed.");
        }
        return data; 
    } catch (error) {
        console.error("Error uploading room image:", error);
        throw error;
    }
}

export async function updateRoomTypeInfo(roomTypeId, roomTypeData) {
    // API: PUT /api/v1/roomTypes/{roomTypeId}
    try {
        const token = getToken();
        const response = await fetch(`${ROOM_TYPE_API_URL}/${roomTypeId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(roomTypeData)
        });
        
        const data = await response.json();
        if (!response.ok || data.code !== 200) {
            throw new Error(data.message || "Failed to update room type.");
        }
        return data.data;
    } catch (error) {
        console.error("Error updating room type:", error);
        throw error;
    }
}

