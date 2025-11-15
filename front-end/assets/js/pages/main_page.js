import { getHotelMainPage } from '../api/hotelService.js';

const API_GATEWAY_URL = 'http://localhost:8888';
const IMAGE_BASE_URL = `${API_GATEWAY_URL}/uploads/`;
const DEFAULT_IMAGE_URL = "../assets/images/default-placeholder.jpg";

function initMap() {
    const location = { lat: 10.030009, lng: 105.7699716 }; 
    const map = new google.maps.Map(document.getElementById("map"), {
        zoom: 15,
        center: location,
    });
    new google.maps.Marker({
        position: location,
        map: map,
    });
}

window.initMap = initMap;

document.addEventListener("DOMContentLoaded", () => {

    const searchButton = document.querySelector('.btn');
    const searchForm = document.getElementById('searchForm');
    
    if (searchButton && searchForm) {
        searchButton.addEventListener('click', (e) => {
            e.preventDefault(); 
            
            const location = document.getElementById('locationInput').value.trim();
            if (!location) {
                alert('Please enter a location.');
                return;
            }
            sessionStorage.setItem('sessionLocation', location);
            window.location.href = '/hotels/search';
        });
    }

    const signUpButton = document.getElementById('btnHomeSignUp');
    if (signUpButton) {
        signUpButton.addEventListener('click', function () {
            window.location.href = '/pages/register.html';
        });
    }

    //popular hotels
    async function loadPopularHotels() {
        const grid = document.getElementById('popularHotelsGrid');
        if (!grid) return;

        try {
            const apiResponse = await getHotelMainPage();

            console.log('Response Hotels:', apiResponse);

            if (apiResponse && apiResponse.code === 200 && Array.isArray(apiResponse.data)) {
                
                const hotels = apiResponse.data;
                
                if (hotels.length > 0) {
                    grid.innerHTML = ''; 
                    
                    hotels.forEach(hotel => {
                        const name = hotel.name;
                        const address = hotel.address_line;
                        const star = hotel.star_rating;
                    
                        let imageUrl = DEFAULT_IMAGE_URL;
                        if (hotel.hotelImages && hotel.hotelImages.length > 0) {
                            const thumbnail = hotel.hotelImages.find(img => img.isThumbnail);
                           let imageFileName = ""; 
                            
                            if (thumbnail) {
                                imageFileName = thumbnail.imageUrl;
                            } else {
                                imageFileName = hotel.hotelImages[0].imageUrl;
                            }
                            console.log("Image url before API Gateway:", imageFileName);

                            if (imageFileName) {
                                imageUrl = `${IMAGE_BASE_URL}${imageFileName}`;
                                console.log("Image url after API Gateway:", imageUrl);
                            }
                        }
                
                        const hotelId = hotel.id; 
                        
                        if (!hotelId) {
                            console.warn("Hotel without ID", hotel.name);
                        }

                        const hotelCard = document.createElement('div');
                        hotelCard.classList.add('popular__card');
                        hotelCard.innerHTML = `
                            <img src="${imageUrl}"
                                 alt="${name}"
                                 class="hotel-image"
                                 data-id="${hotelId}" />
                            <div class="popular__content">
                                <div class="popular__card__header">
                                    <h4>${name}</h4>
                                    <h4>
                                        ${star}
                                        <i class="fa-solid fa-star"></i>
                                    </h4>
                                </div>
                                <p>${address}</p>
                            </div>
                        `;
                        grid.appendChild(hotelCard);
                    });

                    // Event click to navigate to hotel detail page
                    document.querySelectorAll('.hotel-image').forEach(img => {
                        img.addEventListener('click', (event) => {
                            const hotelId = event.target.dataset.id;
                            console.log("Navigating to hotel detail with ID:", hotelId);
                            if (hotelId && hotelId !== "undefined" && hotelId !== "null") {
                                window.location.href = `hotel-details.html?hotel_id=${hotelId}`;
                            } else {
                                console.error('Không tìm thấy Hotel ID để điều hướng.');
                            }
                        });
                    });

                } else {
                     grid.innerHTML = '<p>Không tìm thấy khách sạn nào.</p>';
                }
            } else {
                console.error("API response unvalid", apiResponse.message);
                grid.innerHTML = `<p>Error fetching hotels: ${apiResponse.message}</p>`;
            }
        } catch (error) {
            console.error("Error when fetching hotels:", error);
            grid.innerHTML = `<p>Cannot connect to server.</p>`;
        }
    }

    async function loadBestReviews() {
       const clientSection = document.querySelector('.client');
       if(clientSection) {
           clientSection.style.display = 'none';
       }
       console.warn("Best reviews section is under maintenance.");
    }

    loadPopularHotels();
    loadBestReviews(); 
});