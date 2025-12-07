import { getAllHotelsAdminByOwner, deleteHotelById } from '../../api/hotelServiceAdmin.js';
import { showCreateHotelModal, showEditHotelModal, showRoomTypeModal } from './admin_modal_utils.js';
import { parseJwt } from '../../utils/jwtUtils.js';

const HOTEL_TABLE_BODY_ID = 'hotelTableBody';
const ownerEmail = parseJwt(localStorage.getItem('Bearer')).sub;

let hotelDataCache = [];
let currentSort = { field: 'created_at', direction: 'desc' };


function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        const datePart = dateString.split('T')[0]; 
        const date = new Date(datePart.replace(/-/g, '/')); 
        return date.toLocaleDateString('vi-VN', { year: 'numeric', month: 'numeric', day: 'numeric' });
    } catch (e) {
        return 'Invalid Date';
    }
}

// --- Hotel Render Logic ---
export function renderHotelView(container) {
    container.innerHTML = `
        <div id="hotelViewContainer">
            <div class="bg-white p-6 rounded-xl shadow-lg">
                <h3 class="text-xl font-semibold mb-4 border-b pb-2 flex justify-between items-center">
                    Hotels Management
                    <button id="addHotelButton" class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-blue-700 transition flex items-center">
                        <i class="ri-add-line mr-1"></i> Add New Hotel
                    </button>
                </h3>
                <div id="messageContainer" class="hidden"></div>
                <div class="overflow-x-auto">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead>
                            <tr class="bg-gray-50">
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider sortable-header" data-sort-field="name">
                                    Hotel Name <span class="sort-icon" id="sort-icon-name"></span>
                                </th>
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider sortable-header" data-sort-field="city">
                                    Location <span class="sort-icon" id="sort-icon-city"></span>
                                </th>
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider sortable-header" data-sort-field="star_rating">
                                    Star Rating <span class="sort-icon" id="sort-icon-star_rating"></span>
                                </th>
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider sortable-header" data-sort-field="created_at">
                                    Created At <span class="sort-icon" id="sort-icon-created_at"></span>
                                </th>
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="${HOTEL_TABLE_BODY_ID}" class="bg-white divide-y divide-gray-200">
                            <tr><td colspan="6" class="p-4 text-center text-gray-500">Loading data...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;

    document.getElementById('addHotelButton').addEventListener('click', showCreateHotelModal);
    setupSorting();
}

function renderHotelData(hotels) {
    const tbody = document.getElementById(HOTEL_TABLE_BODY_ID);
    if (!tbody) return;
    tbody.innerHTML = '';

    if (!hotels || hotels.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="p-4 text-center text-gray-500">No hotels found.</td></tr>';
        return;
    }

    hotels.forEach(hotel => {
        const row = tbody.insertRow();
        row.className = 'hover:bg-gray-50';

        row.insertCell().textContent = hotel.name;
        row.insertCell().textContent = `${hotel.city}, ${hotel.country}`; 
        row.insertCell().textContent = hotel.star_rating;
        row.insertCell().textContent = formatDate(hotel.created_at); 
        
        const idCell = row.insertCell();
        idCell.innerHTML = `<code class="bg-gray-100 p-1 rounded text-xs">${hotel.id.substring(0, 8)}...</code>`;

        const actionCell = row.insertCell();
        actionCell.className = 'px-4 py-3 whitespace-nowrap space-x-2';
        
        const manageRoomsBtn = document.createElement('button');
        manageRoomsBtn.innerHTML = '<i class="ri-door-open-line"></i> Rooms';
        manageRoomsBtn.className = 'text-xs text-indigo-600 hover:text-indigo-900 font-semibold p-1 rounded border border-indigo-200 hover:bg-indigo-50';
        manageRoomsBtn.onclick = () => showRoomTypeModal(hotel.id, hotel.name);
        
        const editBtn = document.createElement('button');
        editBtn.innerHTML = '<i class="ri-edit-line"></i> Edit';
        editBtn.className = 'text-xs text-yellow-600 hover:text-yellow-900 font-semibold p-1 rounded border border-yellow-200 hover:bg-yellow-50';
        editBtn.onclick = () => showEditHotelModal(hotel.id);
        
        const deleteBtn = document.createElement('button');
        deleteBtn.innerHTML = '<i class="ri-delete-bin-line"></i> Delete';
        deleteBtn.className = 'text-xs text-red-600 hover:text-red-900 font-semibold p-1 rounded border border-red-200 hover:bg-red-50 ml-1';
        deleteBtn.onclick = () => handleDeleteClick(hotel.id, hotel.name);

        actionCell.appendChild(manageRoomsBtn);
        actionCell.appendChild(editBtn);
        actionCell.appendChild(deleteBtn);
    });
}

//sorting logic
function sortHotels(field, direction) {
    if (!hotelDataCache || hotelDataCache.length === 0) return;

    const sortedHotels = [...hotelDataCache].sort((a, b) => {
        const valA = a[field];
        const valB = b[field];

        if (typeof valA === 'number' && typeof valB === 'number') {
            return direction === 'asc' ? valA - valB : valB - valA;
        }
        
        const strA = String(valA || '').toLowerCase();
        const strB = String(valB || '').toLowerCase();

        if (strA < strB) return direction === 'asc' ? -1 : 1;
        if (strA > strB) return direction === 'asc' ? 1 : -1;
        return 0;
    });

    currentSort = { field, direction };
    renderHotelData(sortedHotels);
    updateSortIcons();
}

function updateSortIcons() {
    document.querySelectorAll('.sort-icon').forEach(icon => {
        icon.innerHTML = '';
    });

    const iconElement = document.getElementById(`sort-icon-${currentSort.field}`);
    if (iconElement) {
        iconElement.innerHTML = currentSort.direction === 'asc' ? '<i class="ri-arrow-up-s-line"></i>' : '<i class="ri-arrow-down-s-line"></i>';
    }
}

function setupSorting() {
    document.querySelectorAll('.sortable-header').forEach(header => {
        // Use header.onclick to assign/override event, ensuring correct logic execution
        header.onclick = function() {
            const field = this.getAttribute('data-sort-field');
            let direction = 'asc';

            if (currentSort.field === field) {
                direction = currentSort.direction === 'asc' ? 'desc' : 'asc';
            }
            sortHotels(field, direction);
        };
    });
    sortHotels(currentSort.field, currentSort.direction);
}

export async function loadHotelDataAndRender() {
    const tbody = document.getElementById(HOTEL_TABLE_BODY_ID);
    const messageBox = document.getElementById('messageContainer');
    
    if (tbody) tbody.innerHTML = '<tr><td colspan="6" class="p-4 text-center text-gray-500">Loading data...</td></tr>';
    
    try {
        const hotels = await getAllHotelsAdminByOwner(ownerEmail);
        hotelDataCache = hotels;
        if (messageBox) messageBox.classList.add('hidden');
        setupSorting(); 

    } catch (error) {
        const errorMessage = error.message.includes("Session expired") ? "Session expired. Please log in again." : `Error loading data: ${error.message}`;
        if (tbody) tbody.innerHTML = `<tr><td colspan="6" class="p-4 text-center text-red-600 font-semibold">${errorMessage}</td></tr>`;
        if (messageBox) {
            messageBox.textContent = errorMessage;
            messageBox.classList.remove('hidden');
            messageBox.className = 'p-4 bg-red-100 text-red-700 rounded-lg font-medium';
        }
    }
}

export async function handleDeleteClick(hotelId, hotelName) {
    if (!confirm(`Are you sure you want to delete the hotel "${hotelName}" (ID: ${hotelId.substring(0, 8)}...)? This action cannot be undone!`)) return;

    try {
        await deleteHotelById(hotelId);
        alert(`Hotel "${hotelName}" has been successfully deleted.`);
        loadHotelDataAndRender(); 
    } catch (error) {
        alert(`Error deleting hotel: ${error.message}`);
    }
}