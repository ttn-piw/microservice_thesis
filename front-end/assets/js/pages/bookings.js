const BOOKING_API_URL = "http://localhost:8888/api/v1/bookings/bookings";
import { sendConfirmationEmail } from '../api/emailService.js';

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        return JSON.parse(decodeURIComponent(atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')));
    } catch (e) { return null; }
}

document.addEventListener('DOMContentLoaded', () => {
    const sessionData = JSON.parse(sessionStorage.getItem('bookingSession'));

    if (!sessionData) {
        alert("No booking data found. Redirecting to home.");
        return;
    }

    renderBookingSummary(sessionData);
    prefillUserInfo();

    //Handle button
    document.getElementById('btnConfirmBooking').addEventListener('click', () => {
        handleBookingSubmit(sessionData);
    });

    document.getElementById('btnBackToHotel').addEventListener('click', () => {
        const params = new URLSearchParams();
        params.append('hotel_id', sessionData.hotelId);
        params.append('checkIn', sessionData.checkInDate);
        params.append('checkOut', sessionData.checkOutDate);
        
        window.location.href = `hotel-details.html?${params.toString()}`;
    })
});

function renderBookingSummary(data) {
    document.getElementById('summaryHotelName').textContent = data.hotelName;
    document.getElementById('summaryAddress').textContent = data.hotelAddress;
    document.getElementById('summaryImage').src = data.hotelImage || "../assets/images/default-placeholder.jpg";
    
    document.getElementById('summaryCheckIn').textContent = data.checkInDate;
    document.getElementById('summaryCheckOut').textContent = data.checkOutDate;

    const start = new Date(data.checkInDate);
    const end = new Date(data.checkOutDate);
    const nights = Math.ceil(Math.abs(end - start) / (1000 * 60 * 60 * 24));
    document.getElementById('summaryNights').textContent = nights;

    const roomListEl = document.getElementById('summaryRoomList');
    roomListEl.innerHTML = '';
    
    data.roomTypes.forEach(room => {
        const div = document.createElement('div');
        div.className = 'room-item';
        div.innerHTML = `
            <span>${room.quantity}x ${room.roomTypeName}</span>
            <span>${(room.price * room.quantity * nights).toLocaleString()} VND</span>
        `;
        roomListEl.appendChild(div);
    });

    document.getElementById('summaryTotalPrice').textContent = parseInt(data.totalPrice).toLocaleString() + " VND";
}

function prefillUserInfo() {
    const token = localStorage.getItem('bearerToken');
    if (token) {
        const payload = parseJwt(token);
        if (payload && payload.sub) {
            document.getElementById('guestEmail').value = payload.sub;
        }
    }
}

async function handleBookingSubmit(sessionData) {
    const btn = document.getElementById('btnConfirmBooking');
    const fullName = document.getElementById('guestName').value.trim();
    const email = document.getElementById('guestEmail').value.trim();
    const otherRequest = document.getElementById('otherRequest').value.trim();
    
    const selectedCheckboxes = Array.from(document.querySelectorAll('.special-req-cb:checked'))
                                    .map(cb => cb.value);
    
    let finalSpecialRequests = selectedCheckboxes.join(", ");
    if (otherRequest) {
        finalSpecialRequests += (finalSpecialRequests ? ", " : "") + otherRequest;
    }

    if (!fullName || !email) {
        alert("Please fill in the required Guest Information.");
        return;
    }

    //Payload
    const bookingPayload = {
        hotelId: sessionData.hotelId,
        checkInDate: sessionData.checkInDate,
        checkOutDate: sessionData.checkOutDate,
        specialRequests: finalSpecialRequests || "None", 
        guests: [
            {
                full_name: fullName,
                email: email,
                is_primary: true
            }
        ],
        roomTypes: sessionData.roomTypes.map(rt => ({
            roomTypeId: rt.roomTypeId,
            quantity: parseInt(rt.quantity)
        }))
    };

     const fullBookingData = {
        ...sessionData,
        guestName: fullName,
        email: email,
        specialRequests: finalSpecialRequests,
        roomTypes: sessionData.roomTypes.map(rt => ({
            roomTypeId: rt.roomTypeId,
            roomTypeName: rt.roomTypeName,
            quantity: parseInt(rt.quantity),
            price: rt.price
        }))
    };

    console.log("Sending Booking Payload:", bookingPayload);

    btn.textContent = "Processing...";
    btn.disabled = true;

    const token = localStorage.getItem('Bearer');
    console.log("Using token:", token);
    
    try {
        const response = await fetch(BOOKING_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` 
            },
            body: JSON.stringify(bookingPayload)
        });

        if (response.ok) {
            const result = await response.json();
            alert("Booking Successful! Booking ID: " + (result.data || "Confimed"));
            console.log("Booking successful:", result.data);

            const bookingId = result.data?.id || result.id || result.data;
            sendConfirmationEmail(fullBookingData, bookingId);

            sessionStorage.removeItem('bookingSession');
            
            window.location.href = `/pages/booking-success.html?bookingId=${bookingId}`;
        } else {
            const errorText = await response.text();
            alert("Booking Failed: " + errorText);
        }
    } catch (error) {
        console.error(error);
        alert("An error occurred while connecting to the server.");
    } finally {
        btn.textContent = "COMPLETE BOOKING";
        btn.disabled = false;
    }
}