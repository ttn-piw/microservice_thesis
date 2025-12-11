import { parseJwt } from '../utils/jwtUtils.js'; 
import { renderHotelView, loadHotelDataAndRender } from '../pages/admin/admin_hotel_view.js'; 
import { loadBookingsDashboard } from '../pages/admin/admin_booking_view.js';
import { loadOwnerReviewDashboard } from '../pages/admin/admin_review_view.js';
import { getTotalHotels, getBookingStats } from '../api/adminDashboard.js'; 

const ADMIN_EMAIL_DISPLAY_ID = 'adminEmailDisplay';
const ADMIN_CONTENT_AREA_ID = 'adminContentArea'; 
const token = localStorage.getItem('Bearer');

let currentAdminEmail = parseJwt(token).sub;

const NAV_IDS = {
    dashboard: 'navDashboard',
    hotels: 'navHotels',
    bookings: 'navBookings',
    reviews: 'navReviews'
};


let currentView = 'dashboard'; 

const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
};

function renderDashboardOverview(container) {
    const hours = new Date().getHours();
    const greeting = hours < 12 ? 'Good Morning' : hours < 18 ? 'Good Afternoon' : 'Good Evening';

    container.innerHTML = `
        <div class="animate-fade-in-up">
            <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-2xl p-8 text-white shadow-lg mb-8 relative overflow-hidden">
                <div class="relative z-10">
                    <h2 class="text-3xl font-bold mb-2">${greeting}, Admin!</h2>
                    <p class="text-blue-100 opacity-90 text-lg">Here's your system overview for today.</p>
                </div>
                <div class="absolute right-0 top-0 h-full w-1/3 bg-white opacity-10 transform skew-x-12"></div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center hover:shadow-md transition">
                    <div class="p-3 bg-blue-100 text-blue-600 rounded-full mr-4">
                        <i class="ri-hotel-fill text-2xl"></i>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500 font-medium">Total Hotels</p>
                        <h3 id="statTotalHotels" class="text-2xl font-bold text-gray-800">...</h3> 
                    </div>
                </div>

                <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center hover:shadow-md transition">
                    <div class="p-3 bg-green-100 text-green-600 rounded-full mr-4">
                        <i class="ri-calendar-check-fill text-2xl"></i>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500 font-medium">Total Bookings</p>
                        <h3 id="statTotalBookings" class="text-2xl font-bold text-gray-800">...</h3>
                    </div>
                </div>

                <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center hover:shadow-md transition">
                    <div class="p-3 bg-yellow-100 text-yellow-600 rounded-full mr-4">
                        <i class="ri-money-dollar-circle-fill text-2xl"></i>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500 font-medium">Total Revenue</p>
                        <h3 id="statTotalRevenue" class="text-2xl font-bold text-gray-800">...</h3>
                    </div>
                </div>
            </div>

             <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 col-span-1">
                    <h3 class="font-bold text-gray-800 mb-4">Quick Actions</h3>
                    <div class="space-y-3">
                        <button onclick="document.querySelector('[data-view=hotels]').click()" class="w-full flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition group cursor-pointer">
                            <span class="font-medium">Manage Hotels</span>
                            <i class="ri-arrow-right-line text-gray-400 group-hover:text-blue-600"></i>
                        </button>
                        <button onclick="document.querySelector('[data-view=bookings]').click()" class="w-full flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition group cursor-pointer">
                            <span class="font-medium">Check Bookings</span>
                            <i class="ri-arrow-right-line text-gray-400 group-hover:text-blue-600"></i>
                        </button>
                    </div>
                </div>
                
                <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 col-span-1 lg:col-span-2">
                    <h3 class="font-bold text-gray-800 mb-4">System Status</h3>
                    <div class="flex items-center p-4 bg-green-50 text-green-700 rounded-lg border border-green-200">
                        <i class="ri-checkbox-circle-line text-xl mr-3"></i>
                        <span>System is connected to API. Live data loaded.</span>
                    </div>
                </div>
            </div>
        </div>
    `;
    loadDashboardData();
}



async function loadDashboardData() {
    if (!currentAdminEmail) return;

    const hotelCount = await getTotalHotels(currentAdminEmail);
    const hotelEl = document.getElementById('statTotalHotels');
    if (hotelEl) hotelEl.textContent = hotelCount;

    const bookingStats = await getBookingStats(currentAdminEmail);
    
    const bookingEl = document.getElementById('statTotalBookings');
    const revenueEl = document.getElementById('statTotalRevenue');

    if (bookingEl) bookingEl.textContent = bookingStats.total_bookings;
    if (revenueEl) revenueEl.textContent = formatCurrency(bookingStats.total_revenue);
}

// Update active navigation button styling
function updateActiveNav(newView) {
    Object.values(NAV_IDS).forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.classList.remove('bg-blue-600', 'font-semibold', 'text-white');
            el.classList.add('hover:bg-gray-700', 'text-gray-300');
        }
    });

    const activeId = NAV_IDS[newView];
    const activeEl = document.getElementById(activeId);
    if (activeEl) {
        activeEl.classList.remove('hover:bg-gray-700', 'text-gray-300');
        activeEl.classList.add('bg-blue-600', 'font-semibold', 'text-white');
    }
}


function renderAdminContent() {
    const contentArea = document.getElementById(ADMIN_CONTENT_AREA_ID);
    
    updateActiveNav(currentView);

    if (currentView === 'dashboard') {
        renderDashboardOverview(contentArea);
    } else if (currentView === 'hotels') {
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
            currentAdminEmail = payload.sub;
            document.getElementById(ADMIN_EMAIL_DISPLAY_ID).textContent = currentAdminEmail || 'Admin';
            
            document.querySelectorAll(`[data-view]`).forEach(button => {
                button.addEventListener('click', (e) => {
                    const view = e.currentTarget.getAttribute('data-view');
                    currentView = view;
                    renderAdminContent(); 
                });
            });
            
            renderAdminContent(); 

        } else {
            alert("Access Denied.");
            window.location.href = 'main-page.html';
        }
    } else {
        alert("Please log in.");
        window.location.href = 'main-page.html';
    }

    document.getElementById('logoutButton').addEventListener('click', () => {
        localStorage.removeItem('Bearer');
        window.location.href = 'login.html';
    });
});