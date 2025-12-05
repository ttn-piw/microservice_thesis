const API_BASE_URL = 'http://localhost:8888/api/v1/chats'; 

export async function sendMessage(message) {
    const token = localStorage.getItem('Bearer');

    if (!token) {
        throw new Error("UNAUTHORIZED: Token not found.");
    }

    try {
        const response = await fetch(`${API_BASE_URL}/hotels`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` 
            },
            body: JSON.stringify({ message: message }) 
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const textResponse = await response.text(); 
        
        try {
            const data = JSON.parse(textResponse);
            return data;
        } catch (e) {
            return { response: textResponse };
        }

    } catch (error) {
        console.error("Chat Service Error:", error);
        throw error;
    }
}