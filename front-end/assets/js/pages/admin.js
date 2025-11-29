import { parseJwt } from '../utils/jwtUtils.js';
import {    getAllHotelsAdmin, deleteHotelById, createNewHotel, uploadHotelImage, 
            getRoomTypesByHotelId, createNewRoomType, uploadRoomTypeImage } 
from '../api/hotelServiceAdmin.js';

const HOTEL_TABLE_BODY_ID = 'hotelTableBody';
const ADMIN_EMAIL_DISPLAY_ID = 'adminEmailDisplay';
const MODAL_CONTAINER_ID = 'createHotelModalContainer'; 
const ROOM_TYPE_MODAL_CONTAINER_ID = 'roomTypeModalContainer'; 
const IMAGE_BASE_URL = 'http://localhost:8888/uploads'

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', { year: 'numeric', month: 'numeric', day: 'numeric' });
    } catch (e) {
        return 'Invalid Date';
    }
}

// --- Render Logic ---
function renderHotelTable(hotels) {
    const tbody = document.getElementById(HOTEL_TABLE_BODY_ID);
    if (!tbody) return;
    tbody.innerHTML = '';

    if (!hotels || hotels.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="p-4 text-center text-gray-500">Không tìm thấy khách sạn nào.</td></tr>';
        return;
    }

    hotels.forEach(hotel => {
        const row = tbody.insertRow();
        row.className = 'hover:bg-gray-50';

        row.insertCell().textContent = hotel.name;
        row.insertCell().textContent = `${hotel.city}, ${hotel.country}`;
        row.insertCell().textContent = hotel.star_rating;
        row.insertCell().textContent = formatDate(hotel.created_at);
        
        //Limited Hotel ID display
        const idCell = row.insertCell();
        idCell.innerHTML = `<code class="bg-gray-100 p-1 rounded text-xs">${hotel.id.substring(0, 8)}...</code>`;
        
        // Action buttons
        const actionCell = row.insertCell();
        actionCell.className = 'px-4 py-3 whitespace-nowrap space-x-2';
        
        const manageRoomsBtn = document.createElement('button');
        manageRoomsBtn.innerHTML = '<i class="ri-door-open-line"></i> Rooms';
        manageRoomsBtn.className = 'text-xs text-indigo-600 hover:text-indigo-900 font-semibold p-1 rounded border border-indigo-200 hover:bg-indigo-50';
        manageRoomsBtn.onclick = () => showRoomTypeModal(hotel.id, hotel.name);
        
        const editBtn = document.createElement('button');
        editBtn.innerHTML = '<i class="ri-edit-line"></i> Edit';
        editBtn.className = 'text-xs text-yellow-600 hover:text-yellow-900 font-semibold p-1 rounded border border-yellow-200 hover:bg-yellow-50';

        const deleteBtn = document.createElement('button');
        deleteBtn.innerHTML = '<i class="ri-delete-bin-line"></i> Delete';
        deleteBtn.className = 'text-xs text-red-600 hover:text-red-900 font-semibold p-1 rounded border border-red-200 hover:bg-red-50 ml-1';
        deleteBtn.onclick = () => handleDeleteClick(hotel.id, hotel.name);

        actionCell.appendChild(manageRoomsBtn);
        actionCell.appendChild(editBtn);
        actionCell.appendChild(deleteBtn);
    });
}

// --- Event Handlers ---
async function handleDeleteClick(event) {
    const hotelId = event.currentTarget.dataset.hotelId;
    const hotelName = event.currentTarget.dataset.hotelName;

    if (confirm(`Bạn có chắc chắn muốn xóa khách sạn "${hotelName}" (ID: ${hotelId.substring(0, 8)}...)?`)) {
        try {
            event.currentTarget.disabled = true;
            event.currentTarget.textContent = 'Deleting...';

            await deleteHotelById(hotelId);
            alert(`Hotel: ${hotelName} deleted successfully.`);
            
            loadAdminDashboard(); 

        } catch (error) {
            alert(`Error deleting hotel: ${error.message}`);
            event.currentTarget.disabled = false;
            event.currentTarget.textContent = ' Delete';
        }
    }
}

async function loadAdminDashboard() {
    const tbody = document.getElementById(HOTEL_TABLE_BODY_ID);
    const messageBox = document.getElementById('messageContainer');
    
    if (tbody) {
        tbody.innerHTML = '<tr><td colspan="6" class="p-4 text-center text-blue-500"><i class="ri-loader-4-line animate-spin mr-2"></i> Loading hotels...</td></tr>';
    }

    try {
        const hotels = await getAllHotelsAdmin();
        console.log("Fetched hotels for admin dashboard:", hotels);
        renderHotelTable(hotels);
        if (messageBox) messageBox.classList.add('hidden');
    } catch (error) {
        const errorMessage = error.message.includes("Session expired") 
                                ? "Session expired. Please log in again." 
                                : `Error loading data: ${error.message}`;

        if (tbody) tbody.innerHTML = `<tr><td colspan="6" class="p-4 text-center text-red-600 font-semibold">${errorMessage}</td></tr>`;
        if (messageBox) {
            messageBox.textContent = errorMessage;
            messageBox.classList.remove('hidden');
            messageBox.className = 'p-4 bg-red-100 text-red-700 rounded-lg font-medium';
        }
        if (error.message.includes("Session expired")) {
             localStorage.removeItem('Bearer');
             setTimeout(() => { window.location.href = 'main-page.html'; }, 1500);
        }
    }
}

function showCreateHotelModal() {
    const modalContainer = document.getElementById(MODAL_CONTAINER_ID);
    if (!modalContainer) return;

    modalContainer.innerHTML = `
        <div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-40 flex items-center justify-center p-4" id="modalOverlay">
            <div class="bg-white rounded-xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-y-auto transform transition-all duration-300">
                <form id="createHotelForm" class="p-6">
                    <div class="flex justify-between items-center border-b pb-3 mb-4">
                        <h3 class="text-2xl font-bold text-gray-800">Add new hotel</h3>
                        <button type="button" class="text-gray-400 hover:text-gray-600 text-3xl" id="closeModalButton">&times;</button>
                    </div>

                    <p class="text-sm text-gray-500 mb-4">Please fill in the basic information of the hotel.</p>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <!-- Column 1 -->
                        <div class="space-y-4">
                            <label class="block">
                                <span class="text-gray-700 font-medium">Hotel Name (*)</span>
                                <input type="text" name="name" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                            </label>
                            <label class="block">
                                <span class="text-gray-700 font-medium">Star Rating (1-5) (*)</span>
                                <input type="number" name="star_rating" min="1" max="5" value="3" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                            </label>
                            <label class="block">
                                <span class="text-gray-700 font-medium">Contact Email (*)</span>
                                <input type="email" name="email" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                            </label>
                            <label class="block">
                                <span class="text-gray-700 font-medium">Check-in Time (HH:MM:SS)</span>
                                <input type="text" name="check_in_time" value="14:00:00" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                            </label>
                            <label class="block">
                                <span class="text-gray-700 font-medium">Check-out Time (HH:MM:SS)</span>
                                <input type="text" name="check_out_time" value="12:00:00" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                            </label>
                            <label class="block">
                                <span class="text-gray-700 font-medium">Main Thumbnail Image (*)</span>
                                <input type="file" name="hotel_image" id="hotelImageInput" accept="image/*" required class="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100">
                            </label>
                        </div>
                        
                        <!-- Column 2 -->
                        <div class="space-y-4">
                            <label class="block">
                                <span class="text-gray-700 font-medium">Address Line (*)</span>
                                <input type="text" name="address_line" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                            </label>
                            <label class="block">
                                <span class="text-gray-700 font-medium">City (*)</span>
                                <input type="text" name="city" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                            </label>
                            <label class="block">
                                <span class="text-gray-700 font-medium">Province (*)</span>
                                <input type="text" name="state_province" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                            </label>
                            <label class="block">
                                <span class="text-gray-700 font-medium">Country (*)</span>
                                <input type="text" name="country" required value="Vietnam" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                            </label>
                            <label class="block">
                                <span class="text-gray-700 font-medium">Hotel Description</span>
                                <textarea name="description" rows="4" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></textarea>
                            </label>
                        </div>
                    </div>
        
                    <div class="mt-6 flex justify-end space-x-3 border-t pt-4">
                        <button type="button" id="cancelModalButton" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition">Hủy</button>
                        <button type="submit" id="submitCreateButton" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition flex items-center">
                            <i class="ri-save-line mr-1"></i> Tạo Khách sạn
                        </button>
                    </div>
                    <div id="formMessage" class="mt-3 text-sm text-red-500 hidden"></div>
                </form>
            </div>
        </div>
    `;

    const closeModal = () => modalContainer.innerHTML = '';
    document.getElementById('closeModalButton').addEventListener('click', closeModal);
    document.getElementById('cancelModalButton').addEventListener('click', closeModal);
    
    document.getElementById('modalOverlay').addEventListener('click', (e) => {
        if (e.target.id === 'modalOverlay') { closeModal(); }
    });
    
    document.getElementById('createHotelForm').addEventListener('submit', handleCreateHotelSubmit);
}

async function handleCreateHotelSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const submitButton = document.getElementById('submitCreateButton');
    const formMessage = document.getElementById('formMessage');
    const imageFile = form.hotel_image.files[0];

    submitButton.disabled = true;
    submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> Handling...';
    formMessage.classList.add('hidden');
    
    //Post to create hotel API
    const newHotel = {
        name: form.name.value,
        description: form.description.value,
        star_rating: parseInt(form.star_rating.value),
        address_line: form.address_line.value,
        city: form.city.value,
        country: form.country.value,
        postal_code: form.postal_code ? form.postal_code.value : null,
        state_province: form.state_province ? form.state_province.value : null,
        phone_number: form.phone_number ? form.phone_number.value : null,
        email: form.email.value,
        check_in_time: form.check_in_time.value,
        check_out_time: form.check_out_time.value
    };

    console.log("Creating new hotel with data:", newHotel);

    try {
        const createdHotel = await createNewHotel(newHotel);
        const hotelId = createdHotel;

        if (imageFile) {
            submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (2/2) Upload image...';
            await uploadHotelImage(imageFile, hotelId);
        }

        document.getElementById(MODAL_CONTAINER_ID).innerHTML = ''; 
        alert(`Created hotel "${createdHotel.name}" successfully! (ID: ${hotelId.substring(0, 8)}...)`);
        
        loadAdminDashboard();

    } catch (error) {
        formMessage.textContent = `Error: ${error.message}`;
        formMessage.classList.remove('hidden');

    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = '<i class="ri-save-line mr-1"></i> Create Hotel';
    }
}

//Room Type Modal Logic
function renderRoomTypeTable(roomTypes) {
    const tbody = document.getElementById('roomTypeTableBody');
    if (!tbody) return;
    tbody.innerHTML = '';
    
    if (!roomTypes || roomTypes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="p-4 text-center text-gray-500">This hotel has no room types yet.</td></tr>';
        return;
    }

    roomTypes.forEach(roomType => {
        const row = tbody.insertRow();
        const imageUrl = roomType.roomTypeImages && roomType.roomTypeImages.length > 0 ? roomType.roomTypeImages[0].imageUrl : 'N/A';
        const price = roomType.price_per_night.toLocaleString('vi-VN') + ' VND';

        row.insertCell().textContent = roomType.name;
        row.insertCell().textContent = `${roomType.capacity_adults} A / ${roomType.capacity_children} C`;
        row.insertCell().textContent = roomType.total_rooms;
        row.insertCell().textContent = price;
        row.insertCell().innerHTML = imageUrl !== 'N/A' ? `<a href="${IMAGE_BASE_URL + imageUrl}" target="_blank" class="text-blue-500 hover:underline">View</a>` : 'N/A';
    });
}


async function showRoomTypeModal(hotelId, hotelName) {
    const modalContainer = document.getElementById(ROOM_TYPE_MODAL_CONTAINER_ID);
    if (!modalContainer) return;

    modalContainer.innerHTML = `
        <div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-50 flex items-center justify-center p-4" id="roomTypeModalOverlay">
            <div class="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-y-auto transform transition-all duration-300">
                <div class="p-6">
                    <div class="flex justify-between items-center border-b pb-3 mb-4">
                        <h3 class="text-2xl font-bold text-gray-800">Manage Room Types: ${hotelName}</h3>
                        <button type="button" class="text-gray-400 hover:text-gray-600 text-3xl" id="closeRoomTypeModalButton">&times;</button>
                    </div>

                    <!-- Tabs for List and Add New -->
                    <div class="space-y-6">
                        <!-- List of room types -->
                        <div class="bg-gray-50 p-4 rounded-lg">
                            <h4 class="text-xl font-semibold mb-3">List of Registered Room Types</h4>
                            <div class="overflow-x-auto">
                                <table class="min-w-full divide-y divide-gray-200">
                                    <thead>
                                        <tr class="bg-gray-200">
                                            <th class="px-4 py-2 text-left text-xs font-medium text-gray-600 uppercase">Room Name</th>
                                            <th class="px-4 py-2 text-left text-xs font-medium text-gray-600 uppercase">Capacity</th>
                                            <th class="px-4 py-2 text-left text-xs font-medium text-gray-600 uppercase">Total Rooms</th>
                                            <th class="px-4 py-2 text-left text-xs font-medium text-gray-600 uppercase">Price/Night</th>
                                            <th class="px-4 py-2 text-left text-xs font-medium text-gray-600 uppercase">Image</th>
                                        </tr>
                                    </thead>
                                    <tbody id="roomTypeTableBody" class="bg-white divide-y divide-gray-200">
                                        <tr><td colspan="5" class="p-4 text-center text-gray-500">Loading room list...</td></tr>
                                    </tbody>
                                </table>
                            </div>
                            <div id="roomListMessage" class="mt-2 text-sm text-center text-gray-500"></div>
                        </div>

                        <!-- Add new room type -->
                        <div class="border-t pt-6">
                            <h4 class="text-xl font-semibold mb-3">Add New Room Type</h4>
                            <form id="createRoomTypeForm" data-hotel-id="${hotelId}" class="space-y-3">
                                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <label class="block">
                                        <span class="text-gray-700">Room Name (*)</span>
                                        <input type="text" name="name" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                                    </label>
                                    <label class="block">
                                        <span class="text-gray-700">Price per Night (VND) (*)</span>
                                        <input type="number" name="price_per_night" min="1000" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                                    </label>
                                    <label class="block">
                                        <span class="text-gray-700">Total Rooms (*)</span>
                                        <input type="number" name="total_rooms" min="1" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                                    </label>
                                </div>
                                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <label class="block">
                                        <span class="text-gray-700">Capacity (Adults) (*)</span>
                                        <input type="number" name="capacity_adults" min="1" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                                    </label>
                                    <label class="block">
                                        <span class="text-gray-700">Capacity (Children)</span>
                                        <input type="number" name="capacity_children" value="0" min="0" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">
                                    </label>
                                    <label class="block">
                                        <span class="text-gray-700">Room Thumbnail Image (*)</span>
                                        <input type="file" name="room_image" id="roomImageInput" accept="image/*" required class="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-1 file:px-2 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700">
                                    </label>
                                </div>
                                <label class="block">
                                    <span class="text-gray-700">Description (*)</span>
                                    <textarea name="description" rows="2" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></textarea>
                                </label>
                                <div class="flex justify-end pt-2">
                                    <button type="submit" id="submitRoomTypeButton" class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition flex items-center">
                                        <i class="ri-add-line mr-1"></i> Add Room Type
                                    </button>
                                </div>
                                <div id="roomTypeFormMessage" class="mt-3 p-2 text-sm text-red-700 bg-red-100 rounded hidden"></div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    const closeModal = () => modalContainer.innerHTML = '';
    document.getElementById('closeRoomTypeModalButton').addEventListener('click', closeModal);
    document.getElementById('roomTypeModalOverlay').addEventListener('click', (e) => {
        if (e.target.id === 'roomTypeModalOverlay') { closeModal(); }
    });

    document.getElementById('createRoomTypeForm').addEventListener('submit', (e) => handleCreateRoomTypeSubmit(e, hotelId, hotelName));
    
    // Load existing room types
    try {
        const roomTypes = await getRoomTypesByHotelId(hotelId);
        renderRoomTypeTable(roomTypes);
    } catch (error) {
        document.getElementById('roomListMessage').textContent = `Error when loading room list: ${error.message}`;
        document.getElementById('roomListMessage').classList.remove('text-gray-500');
        document.getElementById('roomListMessage').classList.add('text-red-600');
    }
}

async function handleCreateRoomTypeSubmit(event, hotelId, hotelName) {
    event.preventDefault();
    const form = event.target;
    const submitButton = document.getElementById('submitRoomTypeButton');
    const formMessage = document.getElementById('roomTypeFormMessage');
    const imageFile = form.room_image.files[0];

    submitButton.disabled = true;
    submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> Processing...';
    formMessage.classList.add('hidden');

    const newRoomType = {
        name: form.name.value,
        description: form.description.value,
        price_per_night: parseFloat(form.price_per_night.value),
        capacity_adults: parseInt(form.capacity_adults.value),
        capacity_children: parseInt(form.capacity_children.value),
        total_rooms: parseInt(form.total_rooms.value)
    };

    try {
        submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (1/2) Creating record...';
        const createdRoomType = await createNewRoomType(newRoomType, hotelId);
        console.log("Created room type:", createdRoomType);
        // Back-end only return room type ID
        const roomTypeId = createdRoomType;

        if (!roomTypeId) {
            throw new Error("Server did not return room type ID. Creating room type failed.");
        }

        if (imageFile) {
            submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (2/2) Uploading image...';
            await uploadRoomTypeImage(imageFile, roomTypeId);
        }

        // Success
        alert(`Created new room type for hotel ${hotelName} successfully!`);

        const roomTypes = await getRoomTypesByHotelId(hotelId);
        renderRoomTypeTable(roomTypes);
        form.reset(); 

    } catch (error) {
        formMessage.textContent = `Lỗi Xử Lý: ${error.message}`;
        formMessage.classList.remove('hidden');

    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = '<i class="ri-add-line mr-1"></i> Tạo Loại phòng';
    }
}


// --- Initialization ---

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('Bearer');
    
    const tbody = document.getElementById(HOTEL_TABLE_BODY_ID);

    if (token) {
        const payload = parseJwt(token);

        if (payload && payload.scope && payload.scope.toUpperCase().includes('ADMIN')) {
            document.getElementById(ADMIN_EMAIL_DISPLAY_ID).textContent = payload.sub || 'Admin';
            loadAdminDashboard(); 
        } else {
            // Case 1: Token exists but is not ADMIN scope
            if (tbody) tbody.innerHTML = '<tr><td colspan="6" class="p-4 text-center text-red-600 font-bold">Truy cập bị từ chối: Bạn không có quyền quản trị.</td></tr>';
            alert("Truy cập bị từ chối: Bạn không có quyền quản trị.");
            localStorage.removeItem('Bearer');
            setTimeout(() => { window.location.href = 'main-page.html'; }, 1000);
        }
    } else {
        // no Token found
        if (tbody) tbody.innerHTML = '<tr><td colspan="6" class="p-4 text-center text-red-600 font-bold">Vui lòng đăng nhập để truy cập trang quản trị.</td></tr>';
        alert("Vui lòng đăng nhập để truy cập trang quản trị.");
        setTimeout(() => { window.location.href = 'main-page.html'; }, 1000);
    }

    document.getElementById('addHotelButton').addEventListener('click', showCreateHotelModal);

    document.getElementById('logoutButton').addEventListener('click', () => {
        localStorage.removeItem('Bearer');
        sessionStorage.clear();
        alert('Logging out...');
        window.location.href = 'main-page.html';
    });
});