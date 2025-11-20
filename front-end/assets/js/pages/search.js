import { searchHomePage } from '../api/hotelService.js';

const API_GATEWAY_URL = 'http://localhost:8888';
const IMAGE_BASE_URL = `${API_GATEWAY_URL}/uploads/`;
const DEFAULT_IMAGE_URL = "../assets/images/default-placeholder.jpg";
/**
 * @param {string} type - 'loading', 'error', 'no-results'
 * @param {string} text - A message 
 */
function showMessage(type, text) {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.className = `message ${type}`; // Reset class
    messageContainer.textContent = text;
}

function hideMessage() {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.style.display = 'none';
}
/**
 * @param {Array} hotels 
 */
function renderHotelResults(hotels) {
    const resultsGrid = document.getElementById('resultsGrid');
    resultsGrid.innerHTML = ''; 

    if (hotels.length === 0) {
        showMessage('no-results', 'No hotels found matching your criteria.');
        return;
    }

    console.log('Rendering hotels:', hotels);

    hotels.forEach(hotel => {
    const thumbnail = hotel.hotelImages.find(img => img.isThumbnail) || hotel.hotelImages[0];  
    const imageUrl = thumbnail 
            ? `${IMAGE_BASE_URL}${thumbnail.imageUrl}` 
            : DEFAULT_IMAGE_URL 

        const hotelCard = document.createElement('div');
        hotelCard.className = 'hotel-card';
        hotelCard.dataset.hotelId = hotel.id;

        hotelCard.innerHTML = `
            <img src="${imageUrl}" alt="${hotel.name}" class="hotel-card__image">
            <div class="hotel-card__content">
                <div class="hotel-card__header">
                    <h3>${hotel.name}</h3>
                    <span class="hotel-card__rating">
                        ${hotel.star_rating} <i class="fa-solid fa-star"></i>
                    </span>
                </div>
                <p class="hotel-card__address">${hotel.address_line}, ${hotel.city}</p>
                <p class="hotel-card__description">${hotel.description}</p>
            </div>
        `;
        
        resultsGrid.appendChild(hotelCard);
    });

    addClickEventsToCards();
}

function addClickEventsToCards() {
    const checkIn = document.getElementById('checkInInput').value;
    const checkOut = document.getElementById('checkOutInput').value;
    // const room = document.getElementById('roomInput').value;

    document.querySelectorAll('.hotel-card').forEach(card => {
        card.addEventListener('click', () => {
            const hotelId = card.dataset.hotelId;
            if (hotelId) {
                const params = new URLSearchParams();
                params.append('hotel_id', hotelId);
                if (checkIn) params.append('checkIn', checkIn);
                if (checkOut) params.append('checkOut', checkOut);
                // if (room) params.append('rooms', room);

                window.location.href = `hotel-details.html?${params.toString()}`;
            }
        });
    });
}

// function addClickEventsToCards() {
//     document.querySelectorAll('.hotel-card').forEach(card => {
//         card.addEventListener('click', () => {
//             const hotelId = card.dataset.hotelId;
//             if (hotelId) {
//                 window.location.href = `hotel-details.html?hotel_id=${hotelId}`;
//             }
//         });
//     });
// }

async function performSearch(searchData) {
    const resultsGrid = document.getElementById('resultsGrid');
    resultsGrid.innerHTML = ''; 
    showMessage('loading', 'Searching for the best hotels...');

    try {
        const apiResponse = await searchHomePage(searchData);

        if (apiResponse && (apiResponse.code === 82200 || apiResponse.code === 200) && Array.isArray(apiResponse.data)) {
            hideMessage();
            renderHotelResults(apiResponse.data);
        } if (apiResponse.code === 83104) {
            showMessage('no-results', 'No hotels found matching your finding location.');
        } else {
            showMessage('error', `Search failed: ${apiResponse.message}`);
        }
    } catch (error) {
        console.error('Search failed:', error);
        showMessage('error', `An error occurred: ${error.message}`);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('searchForm');
    const cityInput = document.getElementById('cityInput');
    const checkInInput = document.getElementById('checkInInput');
    const checkOutInput = document.getElementById('checkOutInput');
    const roomInput = document.getElementById('roomInput');

    const urlParams = new URLSearchParams(window.location.search);
    const locationParam = urlParams.get('location');
    const checkInParam = urlParams.get('checkIn');
    const checkOutParam = urlParams.get('checkOut');
    const roomsParam = urlParams.get('rooms'); 
    
    if (locationParam) {
        cityInput.value = locationParam;
    }
    if (checkInParam) {
        checkInInput.value = checkInParam;
    }
    if (checkOutParam) {
        checkOutInput.value = checkOutParam;
    }
    if (roomsParam) {
        roomInput.value = roomsParam; 
    }

    const today = new Date().toISOString().split('T')[0];
    if (!checkInInput.value) { 
        checkInInput.value = today;
    }
    checkInInput.setAttribute('min', today); 

   //Prevent submit form and perform search
    function handleSearch(e) {
        if (e) {
            e.preventDefault(); 
        }

        const searchData = {
            city: cityInput.value,
            checkIn: checkInInput.value,
            checkOut: checkOutInput.value,
            bookedRoom: roomInput.value
        };

        performSearch(searchData);
    }

    searchForm.addEventListener('submit', handleSearch);

    if (locationParam || checkInParam || checkOutParam || roomsParam) {
        handleSearch();
    }
});