import { getMyBookings } from '../api/bookingService.js';

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString.split('T')[0].replace(/-/g, '/'));
        return date.toLocaleDateString('vi-VN', { year: 'numeric', month: 'short', day: 'numeric' });
    } catch (e) {
        return dateString;
    }
}

function calculateNights(checkIn, checkOut) {
    try {
        const start = new Date(checkIn);
        const end = new Date(checkOut);
        const timeDiff = end.getTime() - start.getTime();
        const days = Math.ceil(timeDiff / (1000 * 3600 * 24));
        return days > 0 ? days : 1;
    } catch (e) {
        return 'N/A';
    }
}

function renderBookings(bookings) {
    const container = document.getElementById('bookingsList');
    const messageBox = document.getElementById('messageContainer');
    container.innerHTML = '';
    messageBox.classList.add('hidden');

    if (!bookings || bookings.length === 0) {
        messageBox.textContent = "You don't have any current or past bookings.";
        messageBox.classList.remove('hidden');
        messageBox.className = 'p-4 bg-yellow-100 text-yellow-700 rounded-lg font-medium';
        return;
    }

    bookings.forEach(booking => {
        const isConfirmed = booking.status === 'CONFIRMED';
        const statusClass = isConfirmed ? 'bg-green-100 text-green-700 border-green-500' : 'bg-yellow-100 text-yellow-700 border-yellow-500';
        const nights = calculateNights(booking.check_in_date, booking.check_out_date);
        
        const card = document.createElement('div');
        card.className = 'booking-card bg-white p-6 rounded-xl shadow-lg hover:shadow-xl transition duration-300 space-y-4';

        const roomDetailsHtml = booking.bookedRoomTypes.map(room => `
            <div class="flex justify-between text-sm text-gray-600">
                <span class="font-medium">${room.quantity}x ${room.room_type_name_snapshot}</span>
                <span>${room.price_per_night_snapshot.toLocaleString()} VND/night</span>
            </div>
        `).join('');

        const guestInfo = booking.bookingGuests.find(g => g.is_primary) || booking.bookingGuests[0];
        const guestName = guestInfo ? guestInfo.full_name : 'N/A';

        card.innerHTML = `
            <!-- Header -->
            <div class="flex justify-between items-center pb-3 border-b border-gray-200">
                <h2 class="text-2xl font-bold text-gray-800">${booking.hotel_name_snapshot}</h2>
                <span class="px-3 py-1 font-semibold rounded-full ${statusClass}">
                    ${booking.status}
                </span>
            </div>

            <!-- Booking Details -->
            <div class="grid grid-cols-2 gap-4 text-gray-700">
                <!-- Left Column -->
                <div>
                    <span class="font-medium text-lg text-agoda">Reference ID:</span>
                    <span class="text-xl p-1 rounded font-mono">${booking.id}</span>
                </div>
                <!-- Right Column -->
                <div class="text-right">
                    <p class="font-medium text-lg text-agoda">Total Price:</p>
                    <span class="text-xl font-extrabold text-red-600">${booking.total_price.toLocaleString()} VND</span>
                </div>
            </div>

            <!-- Dates & Nights -->
            <div class="flex justify-between items-center bg-blue-50 p-3 rounded-lg border-l-4 border-agoda">
                <div class="text-center w-1/3">
                    <p class="text-xs text-gray-600 uppercase">Check-in</p>
                    <span class="font-bold text-lg">${formatDate(booking.check_in_date)}</span>
                </div>
                <div class="text-center w-1/3 text-sm text-gray-500">
                    <i class="ri-moon-line"></i> ${nights} night${nights > 1 ? 's' : ''}
                </div>
                <div class="text-center w-1/3">
                    <p class="text-xs text-gray-600 uppercase">Check-out</p>
                    <span class="font-bold text-lg">${formatDate(booking.check_out_date)}</span>
                </div>
            </div>

            <!-- Room Details -->
            <div class="pt-2 border-t border-gray-200">
                <p class="font-semibold text-gray-800 mb-2 flex items-center"><i class="ri-bed-line mr-2 text-agoda"></i> Rooms Booked</p>
                <div class="space-y-1 pl-4">
                    ${roomDetailsHtml}
                </div>
            </div>
            
            <!-- Guest & Requests -->
            <div class="text-sm space-y-1 pt-2 border-t border-gray-200">
                <p><span class="font-semibold">Primary Guest:</span> ${guestName} (<a href="mailto:${booking.userEmail}" class="text-agoda hover:underline">${booking.userEmail}</a>)</p>
                <p><span class="font-semibold">Requests:</span> <span class="text-gray-500">${booking.special_requests || 'None'}</span></p>
            </div>
        `;
        container.appendChild(card);
    });
}


// Main Logic 
async function loadMyBookings() {
    const container = document.getElementById('bookingsList');
    const messageBox = document.getElementById('messageContainer');

    const token = localStorage.getItem('Bearer');
    if (!token) {
        messageBox.textContent = "You must be logged in to view your bookings. Redirecting to home...";
        messageBox.classList.remove('hidden');
        messageBox.className = 'p-4 bg-red-100 text-red-700 rounded-lg font-medium';
        container.innerHTML = '';
        setTimeout(() => { window.location.href = '/pages/login.html'; }, 3000); 
        return;
    }

    messageBox.textContent = "Loading your bookings...";
    messageBox.classList.remove('hidden');
    messageBox.className = 'p-4 bg-blue-100 text-blue-700 rounded-lg font-medium';
    container.innerHTML = '';


    try {
        const response = await getMyBookings();

        if (response.code === 200 && Array.isArray(response.data)) {
            renderBookings(response.data);
        } else if (response.message && response.message.includes("Session expired")) {
             messageBox.textContent = "Your session has expired. Please log in again.";
             messageBox.className = 'p-4 bg-red-100 text-red-700 rounded-lg font-medium';
                setTimeout(() => { window.location.href = '/pages/login.html'; }, 3000);
        }
         else {
            messageBox.textContent = `Error loading data: ${response.message || 'Unknown error'}.`;
            messageBox.className = 'p-4 bg-red-100 text-red-700 rounded-lg font-medium';
        }

    } catch (error) {
        messageBox.textContent = `A critical error occurred: ${error.message}`;
        messageBox.className = 'p-4 bg-red-100 text-red-700 rounded-lg font-medium';
    }
}


document.addEventListener('DOMContentLoaded', loadMyBookings);