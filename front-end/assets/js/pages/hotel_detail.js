import { getHotelById } from '../api/hotelService.js';


const API_GATEWAY_URL = 'http://localhost:8888';
const IMAGE_BASE_URL = `${API_GATEWAY_URL}/uploads/`;
const DEFAULT_IMAGE_URL = "../assets/images/default-placeholder.jpg";

const urlParams = new URLSearchParams(window.location.search);
const hotelId = urlParams.get('hotel_id');
console.log("hotelURL", hotelId);

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
        const fullAddress = [hotel.address_line, hotel.city, hotel.state_province, hotel.country].filter(Boolean).join(', ');
        document.getElementById('hotelAddress').textContent = fullAddress;
        document.getElementById('hotelStars').textContent = `${hotel.star_rating} Stars`;
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

        const roomTypes = hotel.roomTypes || [];
        const roomContainer = document.getElementById('roomContainer');
        roomContainer.innerHTML = '';

        let defaultRoomImgObj = hotel.hotelImages.find(img => !img.isThumbnail);
        if (!defaultRoomImgObj && thumbnailImage) { 
            defaultRoomImgObj = thumbnailImage;
        }
        const roomImgUrl = defaultRoomImgObj ? defaultRoomImgObj.imageUrl : '';

        roomTypes.forEach(roomType => {
            const availableRooms = roomType.rooms ? roomType.rooms.filter(room => room.status === 'available').length : 0;
            const totalRooms = roomType.total_rooms;

            const roomCard = document.createElement('div');
            roomCard.className = 'room-card';

            const displayRoomImg = roomImgUrl ? `${IMAGE_BASE_URL}${roomImgUrl}` : DEFAULT_IMAGE_URL;

            roomCard.innerHTML = `
                <img src="${displayRoomImg}" alt="${roomType.name}" class="room-image" />
                <div class="room-info">
                    <h3>${roomType.name}</h3>
                    <p>${roomType.description}</p>
                    <p><strong>Price:</strong> $${roomType.price_per_night}/night</p>
                    <p><strong>Available:</strong> ${availableRooms}/${totalRooms}</p>
                    ${availableRooms > 0
                        ? `<button class="btn" onclick="showBookingForm('${roomType.id}', '${roomType.name}', '${displayRoomImg}', ${roomType.price_per_night}, '${hotel.id}')">Book Now</button>`
                        : '<p><strong>Sold Out</strong></p>'
                    }
                </div>
            `;
            roomContainer.appendChild(roomCard);
        });

    } catch (error) {
        console.error('Critical error loading hotel details:', error);
        alert('Failed to load hotel details: ' + error.message);
    }
}

// window.addToWishlist = async (roomId) => { 
//     const userEmail = document.getElementById('UEmail').textContent.trim();
//     if (userEmail === 'Account') {
//         alert('Please log in to add items to your wishlist.');
//         return;
//     }

//     try {
//         const userId = await getUserId(userEmail);
//         console.log("UserId:", userId);

//         const params = new URLSearchParams();
//         params.append('pid', userId);
//         params.append('ctgid', roomId); 

//         const response = await fetch(`http://localhost:8080/wishlist/addToWishlist`, {
//             method: 'POST',
//             body: params
//         });
//         const data = await response.text();
//         console.log("Response:", data);
//         alert('Room added to wishlist!');
//     } catch (error) {
//         console.error('Error adding to wishlist:', error);
//         alert('Failed to add room to wishlist.');
//     }
// };

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
            totalPriceElement.textContent = days * roomPrice;
        } else {
            totalPriceElement.textContent = '0';
        }
    }

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
        const totalPrice = totalPriceElement.textContent;

        if (totalPrice === '0' || !checkinDate || !checkoutDate) {
            alert('Please select valid check-in and check-out dates.');
            return;
        }

        const userEmail = document.getElementById('UEmail').textContent.trim();
        if (userEmail === 'Account') {
            alert('Please log in to book a room.');
            return;
        }

        try {
            const userId = await getUserId(userEmail);
            console.log("Booking with UserId:", userId);

            const response = await fetch('http://localhost:8080/bookings/booked', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    pid: userId,
                    ctgid: roomId,
                    hid: hotelId,
                    money: totalPrice,
                    checkInDate: checkinDate,
                    checkOutDate: checkoutDate
                })
            });
            
            const data = await response.json(); 
            console.log("booking_data:", data);
            alert('Booking successful!');
            
        } catch (error) {
            console.error('Error booking room:', error);
            alert('Booking request sent!'); 
        } finally {
            document.body.removeChild(overlay);
            document.body.removeChild(formContainer);
            location.reload(); 
        }
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

    loadHotelDetails(hotelId);
    fetchReviews(hotelId);
});