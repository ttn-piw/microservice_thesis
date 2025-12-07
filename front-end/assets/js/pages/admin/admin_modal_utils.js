import { getHotelById, updateHotelInfo, createNewHotel, uploadHotelImage, getRoomTypesByHotelId, 
        getRoomTypeById, createNewRoomType, uploadRoomTypeImage , updateRoomTypeInfo, deleteHotelById } 
from '../../api/hotelServiceAdmin.js';

import { loadHotelDataAndRender, handleDeleteClick as reloadHotelList } from '../admin/admin_hotel_view.js'; 
import { parseJwt } from '../../utils/jwtUtils.js'; 

const ownerEmail = parseJwt(localStorage.getItem('Bearer')).sub;
const MODAL_CONTAINER_ID = 'createHotelModalContainer';
const ROOM_TYPE_MODAL_CONTAINER_ID = 'roomTypeModalContainer'; 
const IMAGE_BASE_URL = 'http://localhost:8888/uploads';


function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', { year: 'numeric', month: 'numeric', day: 'numeric' });
    } catch (e) {
        return 'Invalid Date';
    }
}

// --- Hotel Modal Logic ---
export function showCreateHotelModal() {
    const modalContainer = document.getElementById(MODAL_CONTAINER_ID);
    if (!modalContainer) return;

    const closeModal = () => modalContainer.innerHTML = '';
    modalContainer.innerHTML = `
        <div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-40 flex items-center justify-center p-4" id="modalOverlay">
            <div class="bg-white rounded-xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-y-auto transform transition-all duration-300">
                <form id="createHotelForm" class="p-6">
                    <input type="hidden" name="owner_email" value="${ownerEmail}">
                    <div class="flex justify-between items-center border-b pb-3 mb-4">
                        <h3 class="text-2xl font-bold text-gray-800">Add new hotel</h3>
                        <button type="button" class="text-gray-400 hover:text-gray-600 text-3xl" id="closeModalButton">&times;</button>
                    </div>
                    
                    <p class="text-sm text-gray-500 mb-4">Please fill in the basic information of the hotel.</p>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div class="space-y-4">
                            <label class="block"><span class="text-gray-700 font-medium">Hotel Name (*)</span><input type="text" name="name" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700 font-medium">Star Rating (1-5) (*)</span><input type="number" name="star_rating" min="1" max="5" value="3" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700 font-medium">Contact Email (*)</span><input type="email" name="email" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700 font-medium">Check-in Time (HH:MM:SS)</span><input type="text" name="check_in_time" value="14:00:00" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700 font-medium">Check-out Time (HH:MM:SS)</span><input type="text" name="check_out_time" value="12:00:00" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700 font-medium">Main Thumbnail Image (*)</span><input type="file" name="hotel_image" id="hotelImageInput" accept="image/*" required class="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"></label>
                        </div>
                        <div class="space-y-4">
                            <label class="block"><span class="text-gray-700 font-medium">Detailed Address (*)</span><input type="text" name="address_line" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700 font-medium">City (*)</span><input type="text" name="city" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700 font-medium">Province (*)</span><input type="text" name="state_province" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700 font-medium">Country (*)</span><input type="text" name="country" required value="Vietnam" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700 font-medium">Hotel Description</span><textarea name="description" rows="4" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></textarea></label>
                        </div>
                    </div>
                    <div class="mt-6 flex justify-end space-x-3 border-t pt-4">
                        <button type="button" id="cancelModalButton" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition">Cancel</button>
                        <button type="submit" id="submitCreateButton" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition flex items-center">
                            <i class="ri-save-line mr-1"></i> Create Hotel
                        </button>
                    </div>
                    <div id="formMessage" class="mt-3 text-sm text-red-500 hidden"></div>
                </form>
            </div>
        </div>`;

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
    submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> Processing...';
    formMessage.classList.add('hidden');
    
    const newHotel = {
        name: form.name.value, description: form.description.value, star_rating: parseInt(form.star_rating.value),
        address_line: form.address_line.value, city: form.city.value, country: form.country.value,
        postal_code: form.postal_code ? form.postal_code.value : null, state_province: form.state_province ? form.state_province.value : null,
        phone_number: form.phone_number ? form.phone_number.value : null, email: form.email.value,
        check_in_time: form.check_in_time.value, check_out_time: form.check_out_time.value,
        owner_email: ownerEmail
    };

    try {
        submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (1/2) Creating record...';
        const createdHotel = await createNewHotel(newHotel);
        const hotelId = createdHotel;

        if (!hotelId) throw new Error("Server did not return Hotel ID.");

        if (imageFile) {
            submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (2/2) Upload image...';
            await uploadHotelImage(imageFile, hotelId);
        }

        document.getElementById(MODAL_CONTAINER_ID).innerHTML = ''; 
        alert(`Created hotel "${newHotel.name}" successfully! (ID: ${hotelId.substring(0, 8)}...)`);
        loadHotelDataAndRender(); 

    } catch (error) {
        formMessage.textContent = `Error: ${error.message}`;
        formMessage.classList.remove('hidden');
    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = '<i class="ri-save-line mr-1"></i> Create Hotel';
    }
}

export async function showEditHotelModal(hotelId) {
    const modalContainer = document.getElementById(MODAL_CONTAINER_ID);
    if (!modalContainer) return;

    modalContainer.innerHTML = `<div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-40 flex items-center justify-center p-4" id="modalOverlay"><div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-3xl text-center"><i class="ri-loader-4-line animate-spin text-3xl text-blue-600"></i><p class="mt-2 text-gray-600">Loading hotel data...</p></div></div>`;

    try {
        const hotel = await getHotelById(hotelId);
        const closeModal = () => modalContainer.innerHTML = '';
        
        modalContainer.innerHTML = `
            <div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-40 flex items-center justify-center p-4" id="modalOverlay">
                <div class="bg-white rounded-xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-y-auto transform transition-all duration-300">
                    <form id="editHotelForm" data-hotel-id="${hotelId}" class="p-6">
                        <div class="flex justify-between items-center border-b pb-3 mb-4">
                            <h3 class="text-2xl font-bold text-gray-800">Edit hotel: ${hotel.name}</h3>
                            <button type="button" class="text-gray-400 hover:text-gray-600 text-3xl" id="closeModalButton">&times;</button>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div class="space-y-4">
                                <label class="block"><span class="text-gray-700 font-medium">Hotel Name (*)</span><input type="text" name="name" value="${hotel.name}" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                <label class="block"><span class="text-gray-700 font-medium">Star Rating (1-5) (*)</span><input type="number" name="star_rating" min="1" max="5" value="${hotel.star_rating}" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                <label class="block"><span class="text-gray-700 font-medium">Contact Email (*)</span><input type="email" name="email" value="${hotel.email}" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                <label class="block"><span class="text-gray-700 font-medium">Check-in Time (HH:MM:SS)</span><input type="text" name="check_in_time" value="${hotel.check_in_time}" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                <label class="block"><span class="text-gray-700 font-medium">Check-out Time (HH:MM:SS)</span><input type="text" name="check_out_time" value="${hotel.check_out_time}" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                <label class="block"><span class="text-gray-700 font-medium">Update Thumbnail Image (Choose new file)</span><input type="file" name="hotel_image" id="hotelImageInput" accept="image/*" class="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"></label>
                            </div>
                            <div class="space-y-4">
                                <label class="block"><span class="text-gray-700 font-medium">Detailed Address (*)</span><input type="text" name="address_line" value="${hotel.address_line}" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                <label class="block"><span class="text-gray-700 font-medium">City (*)</span><input type="text" name="city" value="${hotel.city}" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                <label class="block"><span class="text-gray-700 font-medium">Country (*)</span><input type="text" name="country" value="${hotel.country}" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                <label class="block"><span class="text-gray-700 font-medium">Hotel Description</span><textarea name="description" rows="2" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">${hotel.description}</textarea></label>
                                <label class="block"><span class="text-gray-700 font-medium">Province</span><input type="text" name="state_province" value="${hotel.state_province || ''}" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                <label class="block"><span class="text-gray-700 font-medium">Postal Code</span><input type="text" name="postal_code" value="${hotel.postal_code || ''}" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            </div>
                        </div>

                        <div class="mt-6 flex justify-end space-x-3 border-t pt-4">
                            <button type="button" id="cancelModalButton" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition">Cancel</button>
                            <button type="submit" id="submitEditButton" class="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 transition flex items-center">
                                <i class="ri-save-line mr-1"></i> Update Hotel
                            </button>
                        </div>
                        <div id="formMessage" class="mt-3 p-2 text-sm text-red-700 bg-red-100 rounded hidden"></div>
                    </form>
                </div>
            </div>`;

        document.getElementById('closeModalButton').addEventListener('click', closeModal);
        document.getElementById('cancelModalButton').addEventListener('click', closeModal);
        document.getElementById('modalOverlay').addEventListener('click', (e) => {
            if (e.target.id === 'modalOverlay') { closeModal(); }
        });
        
        document.getElementById('editHotelForm').addEventListener('submit', handleUpdateHotelSubmit);

    } catch (error) {
        modalContainer.innerHTML = `<div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-40 flex items-center justify-center p-4" id="modalOverlay"><div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-3xl"><p class="text-red-600 font-semibold">Error: Cannot load hotel details. ${error.message}</p><button onclick="document.getElementById('${MODAL_CONTAINER_ID}').innerHTML='';" class="mt-4 px-4 py-2 bg-gray-200 rounded">Close</button></div></div>`;
    }
}

async function handleUpdateHotelSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const hotelId = form.getAttribute('data-hotel-id');
    const submitButton = document.getElementById('submitEditButton');
    const formMessage = document.getElementById('formMessage');
    const imageFile = form.hotel_image.files[0];

    submitButton.disabled = true;
    submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> Processing...';
    formMessage.classList.add('hidden');
    
    const hotelUpdate = {
        name: form.name.value, description: form.description.value, star_rating: parseInt(form.star_rating.value),
        address_line: form.address_line.value, city: form.city.value, country: form.country.value,
        email: form.email.value, check_in_time: form.check_in_time.value, check_out_time: form.check_out_time.value,
        state_province: form.state_province ? form.state_province.value : null, postal_code: form.postal_code ? form.postal_code.value : null,
    };

    try {
        submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (1/2) Updating information...';
        await updateHotelInfo(hotelId, hotelUpdate);
        
        if (imageFile) {
            submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (2/2) Uploading new image...';
            await uploadHotelImage(imageFile, hotelId); 
        }

        document.getElementById(MODAL_CONTAINER_ID).innerHTML = ''; 
        alert(`Hotel "${hotelUpdate.name}" has been successfully updated!`);
        
        loadHotelDataAndRender(); 

    } catch (error) {
        formMessage.textContent = `Error: ${error.message}`;
        formMessage.classList.remove('hidden');

    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = '<i class="ri-save-line mr-1"></i> Update Hotel';
    }
}

// --- Room Type Modal Logic ---
function renderRoomTypeTable(roomTypes, hotelId, hotelName) {
    const tbody = document.getElementById('roomTypeTableBody');
    if (!tbody) return;
    tbody.innerHTML = '';
    
    if (!roomTypes || roomTypes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="p-4 text-center text-gray-500">This hotel has no room types yet.</td></tr>';
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
        
        const actionCell = row.insertCell();
        actionCell.className = 'px-4 py-2 whitespace-nowrap';
        const editBtn = document.createElement('button');
        editBtn.innerHTML = '<i class="ri-edit-line"></i> Edit';
        editBtn.className = 'text-xs text-yellow-600 hover:text-yellow-900 font-semibold p-1 rounded border border-yellow-200 hover:bg-yellow-50';
        editBtn.onclick = () => showEditRoomTypeModal(roomType.id, hotelId, hotelName); 

        actionCell.appendChild(editBtn);
    });
}

async function loadAndRenderRoomTypes(hotelId, hotelName) {
    const roomListMessage = document.getElementById('roomListMessage');
    const tbody = document.getElementById('roomTypeTableBody');
    if (!roomListMessage || !tbody) return;

    roomListMessage.textContent = 'Loading room list...';
    tbody.innerHTML = '<tr><td colspan="6" class="p-4 text-center text-gray-500"><i class="ri-loader-4-line animate-spin"></i></td></tr>';

    try {
        const roomTypes = await getRoomTypesByHotelId(hotelId);
        renderRoomTypeTable(roomTypes, hotelId, hotelName); 
        roomListMessage.textContent = `Found ${roomTypes.length} room types.`;
        roomListMessage.classList.remove('text-red-600');
        roomListMessage.classList.add('text-gray-500');

    } catch (error) {
        roomListMessage.textContent = `Error when loading room list: ${error.message}`;
        roomListMessage.classList.remove('text-gray-500');
        roomListMessage.classList.add('text-red-600');
        tbody.innerHTML = '<tr><td colspan="6" class="p-4 text-center text-red-500">Error loading data.</td></tr>';
    }
}


export function showRoomTypeModal(hotelId, hotelName) {
    const modalContainer = document.getElementById(ROOM_TYPE_MODAL_CONTAINER_ID);
    if (!modalContainer) return;

    const closeModal = () => modalContainer.innerHTML = '';
    modalContainer.innerHTML = `
        <div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-50 flex items-center justify-center p-4" id="roomTypeModalOverlay">
            <div class="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-y-auto transform transition-all duration-300">
                <div class="p-6">
                    <div class="flex justify-between items-center border-b pb-3 mb-4">
                        <h3 class="text-2xl font-bold text-gray-800 modal-title">Manage Room Types: ${hotelName}</h3>
                        <button type="button" class="text-gray-400 hover:text-gray-600 text-3xl" id="closeRoomTypeModalButton">&times;</button>
                    </div>

                    <div class="space-y-6">
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
                                            <th class="px-4 py-2 text-left text-xs font-medium text-gray-600 uppercase">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody id="roomTypeTableBody" class="bg-white divide-y divide-gray-200">
                                        <tr><td colspan="6" class="p-4 text-center text-gray-500">Loading room list...</td></tr>
                                    </tbody>
                                </table>
                            </div>
                            <div id="roomListMessage" class="mt-2 text-sm text-center text-gray-500"></div>
                        </div>

                        <div class="border-t pt-6">
                            <h4 class="text-xl font-semibold mb-3">Add New Room Type</h4>
                            <form id="createRoomTypeForm" data-hotel-id="${hotelId}" class="space-y-3">
                                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <label class="block"><span class="text-gray-700">Room Name (*)</span><input type="text" name="name" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                    <label class="block"><span class="text-gray-700">Price per Night (VND) (*)</span><input type="number" name="price_per_night" min="1000" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                    <label class="block"><span class="text-gray-700">Total Rooms (*)</span><input type="number" name="total_rooms" min="1" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                </div>
                                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                                    <label class="block"><span class="text-gray-700">Capacity (Adults) (*)</span><input type="number" name="capacity_adults" min="1" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                    <label class="block"><span class="text-gray-700">Capacity (Children)</span><input type="number" name="capacity_children" value="0" min="0" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                                    <label class="block"><span class="text-gray-700">Room Thumbnail Image (*)</span><input type="file" name="room_image" id="roomImageInput" accept="image/*" required class="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-1 file:px-2 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700"></label>
                                </div>
                                <label class="block"><span class="text-gray-700">Description (*)</span><textarea name="description" rows="2" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></textarea></label>
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

    document.getElementById('closeRoomTypeModalButton').addEventListener('click', closeModal);
    document.getElementById('roomTypeModalOverlay').addEventListener('click', (e) => {
        if (e.target.id === 'roomTypeModalOverlay') { closeModal(); }
    });
    
    document.getElementById('createRoomTypeForm').addEventListener('submit', (e) => handleCreateRoomTypeSubmit(e, hotelId, hotelName));

    loadAndRenderRoomTypes(hotelId, hotelName);
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
        name: form.name.value, description: form.description.value, price_per_night: parseFloat(form.price_per_night.value),
        capacity_adults: parseInt(form.capacity_adults.value), capacity_children: parseInt(form.capacity_children.value),
        total_rooms: parseInt(form.total_rooms.value)
    };

    try {
        submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (1/2) Creating record...';
        const createdRoomType = await createNewRoomType(newRoomType, hotelId);
        const roomTypeId = createdRoomType;

        if (!roomTypeId) throw new Error("Server did not return room type ID.");

        if (imageFile) {
            submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (2/2) Upload image...';
            await uploadRoomTypeImage(imageFile, roomTypeId);
        }

        alert(`Created room type "${newRoomType.name}" for hotel ${hotelName} successfully!`);
        
        loadAndRenderRoomTypes(hotelId, hotelName); 
        form.reset(); 

    } catch (error) {
        formMessage.textContent = `Processing Error: ${error.message}`;
        formMessage.classList.remove('hidden');
    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = '<i class="ri-add-line mr-1"></i> Add Room Type';
    }
}

export async function showEditRoomTypeModal(roomTypeId, hotelId, hotelName) {
    const modalContainer = document.getElementById(ROOM_TYPE_MODAL_CONTAINER_ID);
    if (!modalContainer) return;

    modalContainer.innerHTML = `<div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-50 flex items-center justify-center p-4" id="roomTypeModalOverlay"><div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-md text-center"><i class="ri-loader-4-line animate-spin text-3xl text-blue-600"></i><p class="mt-2 text-gray-600">Loading room type details...</p></div></div>`;

    try {
        const roomType = await getRoomTypeById(roomTypeId);
        const closeModal = () => showRoomTypeModal(hotelId, hotelName); 
        
        modalContainer.innerHTML = `
            <div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-50 flex items-center justify-center p-4" id="roomTypeModalOverlay">
                <div class="bg-white rounded-xl shadow-2xl w-full max-w-md max-h-[90vh] overflow-y-auto transform transition-all duration-300">
                    <form id="editRoomTypeForm" data-room-type-id="${roomTypeId}" class="p-6">
                        <div class="flex justify-between items-center border-b pb-3 mb-4">
                            <h3 class="text-2xl font-bold text-gray-800">Edit Room Type: ${roomType.name}</h3>
                            <button type="button" class="text-gray-400 hover:text-gray-600 text-3xl" id="closeEditRoomTypeModalButton">&times;</button>
                        </div>
                        
                        <div class="space-y-3">
                            <label class="block"><span class="text-gray-700">Room Name (*)</span><input type="text" name="name" value="${roomType.name}" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700">Price/Night (VND) (*)</span><input type="number" name="price_per_night" value="${roomType.price_per_night}" min="1000" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700">Total Rooms (*)</span><input type="number" name="total_rooms" value="${roomType.total_rooms}" min="1" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700">Capacity (Adults) (*)</span><input type="number" name="capacity_adults" value="${roomType.capacity_adults}" min="1" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700">Capacity (Children)</span><input type="number" name="capacity_children" value="${roomType.capacity_children}" min="0" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border"></label>
                            <label class="block"><span class="text-gray-700">Description (*)</span><textarea name="description" rows="2" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm p-2 border">${roomType.description}</textarea></label>
                            
                            <label class="block"><span class="text-gray-700">Update Room Thumbnail Image</span><input type="file" name="room_image_update" accept="image/*" class="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-1 file:px-2 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-yellow-50 file:text-yellow-700"></label>
                        </div>

                        <div class="mt-6 flex justify-end pt-4 space-x-3 border-t">
                            <button type="button" id="cancelEditRoomTypeButton" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition">Cancel</button>
                            <button type="submit" id="submitUpdateRoomTypeButton" class="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 transition flex items-center">
                                <i class="ri-save-line mr-1"></i> Save Updates
                            </button>
                        </div>
                        <div id="roomTypeUpdateMessage" class="mt-3 p-2 text-sm text-red-700 bg-red-100 rounded hidden"></div>
                    </form>
                </div>
            </div>`;

        document.getElementById('closeEditRoomTypeModalButton').addEventListener('click', closeModal);
        document.getElementById('cancelEditRoomTypeButton').addEventListener('click', closeModal);
        document.getElementById('roomTypeModalOverlay').addEventListener('click', (e) => {
            if (e.target.id === 'roomTypeModalOverlay') { closeModal(); }
        });
        
        document.getElementById('editRoomTypeForm').addEventListener('submit', (e) => handleUpdateRoomTypeSubmit(e, hotelId, hotelName));

    } catch (error) {
        modalContainer.innerHTML = `<div class="fixed inset-0 bg-gray-900 bg-opacity-75 z-50 flex items-center justify-center p-4" id="roomTypeModalOverlay"><div class="bg-white rounded-xl shadow-2xl p-6 w-full max-w-md"><p class="text-red-600 font-semibold">Error: Unable to load room type details. ${error.message}</p><button onclick="document.getElementById('${ROOM_TYPE_MODAL_CONTAINER_ID}').innerHTML='';" class="mt-4 px-4 py-2 bg-gray-200 rounded">Close</button></div></div>`;
    }
}

async function handleUpdateRoomTypeSubmit(event, hotelId, hotelName) {
    event.preventDefault();
    const form = event.target;
    const roomTypeId = form.getAttribute('data-room-type-id');
    const submitButton = document.getElementById('submitUpdateRoomTypeButton');
    const formMessage = document.getElementById('roomTypeUpdateMessage');
    const imageFile = form.room_image_update.files[0];

    submitButton.disabled = true;
    submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> Processing...';
    formMessage.classList.add('hidden');

    const roomTypeUpdate = {
        name: form.name.value, description: form.description.value, price_per_night: parseFloat(form.price_per_night.value),
        capacity_adults: parseInt(form.capacity_adults.value), capacity_children: parseInt(form.capacity_children.value),
        total_rooms: parseInt(form.total_rooms.value)
    };

    try {
        submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (1/2) Updating information...';
        await updateRoomTypeInfo(roomTypeId, roomTypeUpdate);
        
        if (imageFile) {
            submitButton.innerHTML = '<i class="ri-loader-4-line animate-spin mr-1"></i> (2/2) Uploading new image...';
            await uploadRoomTypeImage(imageFile, roomTypeId); 
        }

        alert(`Room type "${roomTypeUpdate.name}" has been successfully updated!`);
        showRoomTypeModal(hotelId, hotelName); 
        

    } catch (error) {
        formMessage.textContent = `Processing Error: ${error.message}`;
        formMessage.classList.remove('hidden');

    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = '<i class="ri-save-line mr-1"></i> Save Updates';
    }
}