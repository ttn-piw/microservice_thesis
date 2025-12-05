import { getAllBookingsAdmin, cancelBookingAdmin, deleteBookingAdmin } from '../../api/bookingServiceAdmin.js';

const BOOKING_TABLE_BODY_ID = 'bookingTableBody';

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString.split('T')[0].replace(/-/g, '/'));
        return date.toLocaleDateString('vi-VN', { year: 'numeric', month: 'short', day: 'numeric' });
    } catch (e) {
        return dateString.substring(0, 10);
    }
}

// --- Booking Management Logic ---
async function handleCancelBooking(bookingId) {
    if (!confirm(`Are you sure you want to CANCEL booking ${bookingId.substring(0, 8)}...?`)) return;
    try {
        await cancelBookingAdmin(bookingId);
        alert('Booking has been successfully cancelled.');
        loadBookingsDashboard();
    } catch (error) {
        alert(`Error cancelling booking: ${error.message}`);
    }
}

async function handleDeleteBooking(bookingId) {
    if (!confirm(`Are you sure you want to PERMANENTLY DELETE booking ${bookingId.substring(0, 8)}...?`)) return;
    try {
        await deleteBookingAdmin(bookingId);
        alert('Booking has been successfully deleted.');
        loadBookingsDashboard();
    } catch (error) {
        alert(`Error deleting booking: ${error.message}`);
    }
}

function renderBookingTable(bookings) {
    const tbody = document.getElementById(BOOKING_TABLE_BODY_ID);
    if (!tbody) return;
    tbody.innerHTML = '';

    if (!bookings || bookings.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="p-4 text-center text-gray-500">No bookings found.</td></tr>';
        return;
    }
    
    bookings.forEach(booking => {
        const row = tbody.insertRow();
        row.className = 'hover:bg-gray-50';

        const statusColor = {
            'CONFIRMED': 'bg-green-100 text-green-700',
            'PENDING': 'bg-yellow-100 text-yellow-700',
            'CANCELLED': 'bg-red-100 text-red-700',
        }[booking.status] || 'bg-gray-100 text-gray-700';

        row.insertCell().innerHTML = `
            <code class="p-1 rounded text-xs block mb-1">${booking.id}</code>
            <strong>${booking.hotel_name_snapshot}</strong>
        `;
        row.insertCell().innerHTML = `
            <span class="block">${formatDate(booking.check_in_date)}</span>
            <span class="block">${formatDate(booking.check_out_date)}</span>
        `;
        row.insertCell().innerHTML = `<strong>${booking.total_price.toLocaleString('vi-VN')} VND</strong>`;
        
        row.insertCell().innerHTML = `<span class="px-2 py-1 text-xs font-semibold rounded-full ${statusColor}">${booking.status}</span>`;

        const actionCell = row.insertCell();
        actionCell.className = 'px-4 py-3 whitespace-nowrap space-x-2';
        
        if (booking.status === 'CONFIRMED' || booking.status === 'PENDING') {
            const cancelBtn = document.createElement('button');
            cancelBtn.innerHTML = '<i class="ri-close-circle-line"></i> Cancel';
            cancelBtn.className = 'text-xs text-red-600 hover:text-red-900 font-semibold p-1 rounded border border-red-200 hover:bg-red-50';
            cancelBtn.onclick = () => handleCancelBooking(booking.id);
            actionCell.appendChild(cancelBtn);
        }

        const deleteBtn = document.createElement('button');
        deleteBtn.innerHTML = '<i class="ri-delete-bin-line"></i> Delete';
        deleteBtn.className = 'text-xs text-gray-600 hover:text-gray-900 font-semibold p-1 rounded border border-gray-200 hover:bg-gray-50 ml-1';
        deleteBtn.onclick = () => handleDeleteBooking(booking.id);
        actionCell.appendChild(deleteBtn);
    });
}

export async function loadBookingsDashboard(container) {
    container.innerHTML = `
        <div class="bg-white p-6 rounded-xl shadow-lg">
            <h3 class="text-xl font-semibold mb-4 border-b pb-2">Booking Management</h3>
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead>
                        <tr class="bg-gray-50">
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID / Hotel</th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Check-in/out Date</th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Price</th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="${BOOKING_TABLE_BODY_ID}" class="bg-white divide-y divide-gray-200">
                        <tr><td colspan="5" class="p-4 text-center text-gray-500">Loading booking data...</td></tr>
                    </tbody>
                </table>
            </div>
            <div id="bookingListMessage" class="mt-4"></div>
        </div>
    `;

    try {
        const bookings = await getAllBookingsAdmin();
        renderBookingTable(bookings);

    } catch (error) {
        const messageBox = document.getElementById('bookingListMessage');
        if (messageBox) {
            messageBox.innerHTML = `<div class="p-4 bg-red-100 text-red-700 rounded-lg font-medium">Error loading booking data: ${error.message}</div>`;
        }
    }
}