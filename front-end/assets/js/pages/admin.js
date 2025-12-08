import { parseJwt } from '../utils/jwtUtils.js'; 
import { renderHotelView, loadHotelDataAndRender } from '../pages/admin/admin_hotel_view.js'; 
import { loadBookingsDashboard } from '../pages/admin/admin_booking_view.js';
import { loadOwnerReviewDashboard } from '../pages/admin/admin_review_view.js';

const ADMIN_EMAIL_DISPLAY_ID = 'adminEmailDisplay';
const ADMIN_CONTENT_AREA_ID = 'adminContentArea'; 
const HOTEL_NAV_ID = 'navHotels';
const BOOKING_NAV_ID = 'navBookings';

let currentView = 'hotels'; 

function updateActiveNav(newView) {
    const navHotels = document.getElementById(HOTEL_NAV_ID);
    const navBookings = document.getElementById(BOOKING_NAV_ID);

    if (navHotels) navHotels.classList.remove('bg-blue-600', 'transition', 'font-semibold');
    if (navBookings) navBookings.classList.remove('bg-blue-600', 'transition', 'font-semibold');

    if (newView === 'hotels') {
        if (navHotels) navHotels.classList.add('bg-blue-600', 'font-semibold');
        if (navBookings) navBookings.classList.add('hover:bg-gray-700');
    } else if (newView === 'bookings') {
        if (navBookings) navBookings.classList.add('bg-blue-600', 'font-semibold');
        if (navHotels) navHotels.classList.add('hover:bg-gray-700');
    }
}


function renderAdminContent() {
    const contentArea = document.getElementById(ADMIN_CONTENT_AREA_ID);
    
    updateActiveNav(currentView);

    if (currentView === 'hotels') {
        renderHotelView(contentArea);
        loadHotelDataAndRender(); 
    } else if (currentView === 'bookings') {
        contentArea.innerHTML = `<div id="tabContent"></div>`;
        loadBookingsDashboard(document.getElementById('tabContent'));
    } else if (currentView === 'reviews') {
        contentArea.innerHTML = `<div id="tabContent"></div>`;
        loadOwnerReviewDashboard(document.getElementById('tabContent'));
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('Bearer');
    
    if (token) {
        const payload = parseJwt(token);
        
        if (payload && payload.scope && payload.scope.toUpperCase().includes('ADMIN')) {
            document.getElementById(ADMIN_EMAIL_DISPLAY_ID).textContent = payload.sub || 'Admin';
            
            document.querySelectorAll(`[data-view]`).forEach(button => {
                button.addEventListener('click', (e) => {
                    const view = e.currentTarget.getAttribute('data-view');
                    currentView = view;
                    renderAdminContent(); 
                });
            });
            
            renderAdminContent(); 

        } else {
            alert("Access Denied. Redirecting.");
            window.location.href = 'main-page.html';
        }
    } else {
        alert("Please log in to access the admin page.");
        window.location.href = 'main-page.html';
    }

    document.getElementById('logoutButton').addEventListener('click', () => {
        localStorage.removeItem('Bearer');
        alert('Logging out...');
        window.location.href = 'login.html';
    });
});