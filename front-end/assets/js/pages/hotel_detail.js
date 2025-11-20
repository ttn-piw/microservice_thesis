import { getHotelById, getRoomAvailability } from '../api/hotelService.js';

const API_GATEWAY_URL = 'http://localhost:8888';
const IMAGE_BASE_URL = `${API_GATEWAY_URL}/uploads/`;
const DEFAULT_IMAGE_URL = "../assets/images/default-placeholder.jpg";

const urlParams = new URLSearchParams(window.location.search);
const hotelId = urlParams.get('hotel_id');
const checkIn = urlParams.get('checkIn') == null ? '' : urlParams.get('checkIn'); 
const checkOut = urlParams.get('checkOut') == null ? '' : urlParams.get('checkOut'); 
const rooms = urlParams.get('rooms') || "1"

console.log(`HotelID: ${hotelId}, CheckIn: ${checkIn}, CheckOut: ${checkOut}`);

async function getUserId(userEmail) {
    try {
        const response = await fetch(`http://localhost:8080/persons/personEmail/getPID?personEmail=${userEmail}`);
        const data = await response.json();
        return data[0][0];
    } catch (error) {
        console.error('Error fetching userId:', error);
        alert('There was an issue fetching your user details. Please try again.');
        throw error; 
    }
}

// async function fetchReviews(hotelId) {
//     try {
//         const response = await fetch(`http://localhost:8080/api/reviews/hotelId?hotelId=${hotelId}`);
//         const reviews = await response.json();
        
//         console.log('Reviews Data:', reviews);
//         const reviewsContainer = document.getElementById('reviewsContainer');
//         reviewsContainer.innerHTML = '';

//         if (reviews && Array.isArray(reviews) && reviews.length > 0) {
//             reviews.forEach(review => {
//                 const [rating, comment, category, customerName] = review;
//                 const reviewCard = document.createElement('div');
//                 reviewCard.className = 'review-card';
//                 reviewCard.innerHTML = `
//                     <div class="review-info">
//                         <h3>${customerName || "Anonymous"}</h3>
//                         <p class="rating">Rating: ${rating || "N/A"} ★</p>
//                         <p class="category">Category: ${category || "Unknown"}</p>
//                         <p>${comment || "No comment"}</p>
//                     </div>
//                 `;
//                 reviewsContainer.appendChild(reviewCard);
//             });
//         } else {
//             reviewsContainer.innerHTML = '<p>No reviews available for this hotel yet.</p>';
//         }
//     } catch (error) {
//         console.error('Error fetching reviews:', error);
//         reviewsContainer.innerHTML = '<p>Error loading reviews.</p>';
//     }
// }

async function loadHotelDetails(hotelId) {
    try {
        const apiResponse = await getHotelById(hotelId);

        if (apiResponse.code !== 200 || !apiResponse.data) {
            console.error('Error fetching hotel data:', apiResponse.message);
            alert('Could not load hotel details. ' + apiResponse.message);
            return;
        }

        const hotel = apiResponse.data;
        console.log('Hotel Data:', hotel);

        document.getElementById('hotelName').textContent = hotel.name;
        const fullAddress = [hotel.city, hotel.state_province].filter(Boolean).join(', ');
        document.getElementById('hotelAddress').innerHTML = `<i class="ri-map-pin-line"></i> ${fullAddress}`;
        document.getElementById('cityInput').value = hotel.city;
        document.getElementById('hotelName').textContent = hotel.name;
        document.getElementById('hotelStars').textContent = `${hotel.star_rating} Stars`;
        document.getElementById('hotelDescription').textContent = hotel.description || "No description available.";

        const starsContainer = document.getElementById('hotelStars');
        starsContainer.innerHTML = ''; // Xóa
        for (let i = 0; i < hotel.star_rating; i++) {
            const starIcon = document.createElement('i');
            starIcon.className = 'fas fa-star';
            starsContainer.appendChild(starIcon);
        }

        document.getElementById('hotelDescription').textContent = hotel.description || "No description available.";

        const thumbnailImage = hotel.hotelImages.find(img => img.isThumbnail === true);
        let imageFileName = ""; 

        if (thumbnailImage) {
            imageFileName = thumbnailImage.imageUrl;
        } else if (hotel.hotelImages.length > 0) {
            imageFileName = hotel.hotelImages[0].imageUrl;
        }

        if (imageFileName) {
            document.getElementById('hotelImage').src = `${IMAGE_BASE_URL}${imageFileName}`;
        } else {
            document.getElementById('hotelImage').src = DEFAULT_IMAGE_URL; 
        }

    } catch (error) {
        console.error('Critical error loading hotel details:', error);
        alert('Failed to load hotel details: ' + error.message);
    }
}

async function loadAvailableRooms(hotelId, checkIn, checkOut) {
    const roomContainer = document.getElementById('roomContainer');
    
    if (!checkIn || !checkOut) {
        roomContainer.innerHTML = '<p>Please go back and select check-in and check-out dates to see available rooms.</p>';
        return;
    }

    roomContainer.innerHTML = '<p>Searching for available rooms...</p>';

    try {
        const apiResponse = await getRoomAvailability(hotelId, checkIn, checkOut);

        if (apiResponse.code !== 82200 || !Array.isArray(apiResponse.data)) {
            throw new Error(apiResponse.message || 'Could not load available rooms.');
        }

        renderAvailableRooms(apiResponse.data);

    } catch (error) {
        console.error('Error fetching availability:', error);
        roomContainer.innerHTML = `<p>Lỗi khi tải danh sách phòng: ${error.message}</p>`;
    }
}

function renderAvailableRooms(rooms) {
    const roomContainer = document.getElementById('roomContainer');
    roomContainer.innerHTML = '';

    if (rooms.length === 0) {
        roomContainer.innerHTML = '<p>No available rooms found for the dates you selected.</p>';
        return;
    }

    rooms.forEach(roomType => {
        const roomCard = document.createElement('div');
        roomCard.className = 'room-card';
        
        const displayRoomImg = DEFAULT_IMAGE_URL;

        roomCard.innerHTML = `
            <img src="${displayRoomImg}" alt="${roomType.name}" class="room-image" />
            <div class="room-info">
                <h3>${roomType.name}</h3>
                <p>${roomType.description}</p>
                <p><strong>Price:</strong> ${roomType.price_per_night.toLocaleString()} VND/night</p>
                <p><strong>Available:</strong> ${roomType.availableRooms}/${roomType.totalRooms}</p>
                ${roomType.availableRooms > 0
                    ? `<button class="btn" onclick="showBookingForm('${roomType.id}', '${roomType.name}', '${displayRoomImg}', ${roomType.price_per_night}, '${hotelId}')">Book Now</button>`
                    : '<p><strong>Sold Out</strong></p>'
                }
            </div>
        `;
        roomContainer.appendChild(roomCard);
    });
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1]; 
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Error when parsing token: ", e);
        return null;
    }
}

window.showBookingForm = (roomId, roomName, roomImg, roomPrice, hotelId) => {
    console.log("HotelId form:", hotelId);

    const overlay = document.createElement('div');
    overlay.className = 'overlay';

    const formContainer = document.createElement('div');
    formContainer.className = 'booking-form';
    formContainer.innerHTML = `
    <form id="bookingSubmitForm">
        <h2>Booking Room</h2>
        <img src="${roomImg}" alt="${roomName}" />
        <p>Type: ${roomName}</p>
        <label for="checkin">Check-in:</label>
        <input type="date" id="checkin" required>
        <label for="checkout">Check-out:</label>
        <input type="date" id="checkout" required>
        <p><strong>Total Price:</strong> $<span id="totalPrice">0</span></p>
        <button type="submit">Book now !</button>
        <button type="button" id="closeForm">Back</button>
    </form>
    `;

    document.body.appendChild(overlay);
    document.body.appendChild(formContainer);

    const checkinInput = formContainer.querySelector('#checkin');
    const checkoutInput = formContainer.querySelector('#checkout');
    const totalPriceElement = formContainer.querySelector('#totalPrice');

    if (checkIn) {
        checkinInput.value = checkIn;
    }
    if (checkOut) {
        checkoutInput.value = checkOut;
    }

    const today = new Date().toISOString().split('T')[0];
    checkinInput.setAttribute('min', today);

    function calculateTotalPrice() {
        const checkinDate = new Date(checkinInput.value);
        const checkoutDate = new Date(checkoutInput.value);

        if (checkinInput.value) {
            let nextDay = new Date(checkinDate);
            nextDay.setDate(nextDay.getDate() + 1);
            checkoutInput.setAttribute('min', nextDay.toISOString().split('T')[0]);
        }

        if (checkinDate && checkoutDate && checkoutDate > checkinDate) {
            const time = Math.abs(checkoutDate - checkinDate);
            const days = Math.ceil(time / (1000 * 60 * 60 * 24));
            totalPriceElement.textContent = (days * roomPrice).toLocaleString();
        } else {
            totalPriceElement.textContent = '0';
        }
    }

    calculateTotalPrice();

    checkinInput.addEventListener('change', calculateTotalPrice);
    checkoutInput.addEventListener('change', calculateTotalPrice);

    formContainer.querySelector('#closeForm').addEventListener('click', () => {
        document.body.removeChild(overlay);
        document.body.removeChild(formContainer);
    });

   formContainer.querySelector('#bookingSubmitForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        const checkinDate = checkinInput.value;
        const checkoutDate = checkoutInput.value;
        const totalPrice = totalPriceElement.textContent.replace(/,/g, ''); 

        if (totalPrice === '0' || !checkinDate || !checkoutDate) {
            alert('Please select valid check-in and check-out dates.');
            return;
        }

        const token = localStorage.getItem('Bearer'); 
        console.log("Booking with token:", token);

        if (!token) {
            alert('Please log in to book a room.');
            window.location.href = 'login.html'; 
            return;
        }

        let userEmail;
        try {
            const payload = parseJwt(token);
            if (!payload || !payload.sub) {
                throw new Error("Invalid token payload.");
            }
            userEmail = payload.sub;
            console.log("Booking with email (from token):", userEmail);

        } catch (error) {
            console.error("Token error:", error);
            alert('Your session is invalid or expired. Please log in again.');
            localStorage.removeItem('bearerToken'); 
        }

        const bookingData = {
        hotelId: hotelId, 
        hotelName: document.getElementById('hotelName').textContent,
        hotelAddress: document.getElementById('hotelAddress').textContent,
        hotelImage: document.getElementById('hotelImage').src,
        checkInDate: checkinInput.value,
        checkOutDate: checkoutInput.value,
        totalPrice: totalPriceElement.textContent.replace(/,/g, ''),
        roomTypes: [
            {
                roomTypeId: roomId, 
                roomTypeName: roomName, 
                price: roomPrice,
                quantity: 1
            }
        ]};
        
        sessionStorage.setItem('bookingSession', JSON.stringify(bookingData));
        window.location.href = "bookings.html";
    });
};

document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    const hotelId = urlParams.get('hotel_id'); 

    if (!hotelId) {
        console.error('Hotel ID is missing from the URL.');
        alert('Could not find hotel ID. Returning to home page.');
        window.location.href = '/hotels/home'; 
        return;
    }


    const checkInInput = document.getElementById('checkInInput');
    const checkOutInput = document.getElementById('checkOutInput');
    const roomInput = document.getElementById('roomInput');
    
    if (checkIn) checkInInput.value = checkIn;
    if (checkOut) checkOutInput.value = checkOut;
    if (rooms) roomInput.value = rooms;

    const newSearchForm = document.getElementById('searchFormNew');
    newSearchForm.addEventListener('submit', (e) => {
        e.preventDefault(); 

        const newLocation = document.getElementById('cityInput').value;
        const newCheckIn = checkInInput.value;
        const newCheckOut = checkOutInput.value;
        const newRooms = roomInput.value;

        const queryParams = new URLSearchParams();
        if (hotelId) queryParams.set('hotel_id', hotelId);
        if (newLocation) queryParams.set('location', newLocation);
        if (newCheckIn) queryParams.set('checkIn', newCheckIn);
        if (newCheckOut) queryParams.set('checkOut', newCheckOut);
        if (newRooms) queryParams.set('rooms', newRooms);

        window.location.href = `hotel-details.html?${queryParams.toString()}`;
    });

    loadHotelDetails(hotelId);
    loadAvailableRooms(hotelId, checkIn, checkOut);
    // fetchReviews(hotelId);
});