import { getOwnerReviews } from '../../api/reviewServiceAdmin.js';

const REVIEW_TABLE_BODY_ID = 'ownerReviewTableBody';

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', { year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' });
    } catch (e) {
        return dateString;
    }
}

function renderStars(rating) {
    let starsHtml = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= rating) {
            starsHtml += '<i class="ri-star-fill text-yellow-400"></i>';
        } else {
            starsHtml += '<i class="ri-star-line text-gray-300"></i>';
        }
    }
    return `<div class="flex text-sm">${starsHtml}</div>`;
}

// --- Review Logic ---
function renderReviewTable(reviews) {
    const tbody = document.getElementById(REVIEW_TABLE_BODY_ID);
    if (!tbody) return;
    tbody.innerHTML = '';

    if (!reviews || reviews.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="p-6 text-center text-gray-500">No reviews available.</td></tr>';
        return;
    }

    reviews.forEach(review => {
    const row = tbody.insertRow();
    row.className = 'hover:bg-gray-50 transition border-b border-gray-100 align-top';
        let c1 = row.insertCell();
        c1.className = "px-6 py-4 align-top";
        c1.innerHTML = `
            <div class="space-y-1">
                ${renderStars(review.rating)}
                <p class="text-xs text-gray-500">${formatDate(review.createdAt)}</p>
            </div>
        `;

        let c2 = row.insertCell();
        c2.className = "px-6 py-4 align-top";
        c2.innerHTML = `
            <div class="text-sm">
                <p><span class="font-medium">Booking ID:</span> 
                    <code class="bg-gray-100 px-1 rounded text-xs">${review.bookingId}</code>
                </p>
                <p class="text-xs text-gray-500 mt-1">User ID: ${review.userId}</p>
            </div>
        `;

        let c3 = row.insertCell();
        c3.className = "px-6 py-4 align-top";
        c3.innerHTML = `
            <div class="text-sm text-gray-700 italic">
                "${review.comment || 'No comments'}"
            </div>
        `;

        let c4 = row.insertCell();
        c4.className = "px-6 py-4 align-top";
        c4.innerHTML = `
            <code class="text-xs text-gray-500">${review.hotelId}</code>
        `;

        let c5 = row.insertCell();
        c5.className = "px-6 py-4 align-top";
        c5.innerHTML = `
            <button class="text-blue-600 hover:text-blue-800 text-sm font-medium">Reply</button>
        `;
    });
}


export async function loadOwnerReviewDashboard(container) {
    container.innerHTML = `
        <div class="bg-white p-6 rounded-xl shadow-lg animate-fade-in-up">
            <div class="flex justify-between items-center mb-6 border-b pb-4">
                <h3 class="text-2xl font-bold text-gray-800">Customer Reviews</h3>
                <div class="text-sm text-gray-500">Feedback on service quality</div>
            </div>
            
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-32">Rating</th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-60">Booking Info</th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Comment</th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-48">Hotel ID</th>
                            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-24">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="${REVIEW_TABLE_BODY_ID}" class="bg-white divide-y divide-gray-200 leading-relaxed">
                        <tr><td colspan="5" class="p-6 text-center text-blue-500"><i class="ri-loader-4-line animate-spin mr-2"></i> Loading reviews...</td></tr>
                    </tbody>
                </table>
            </div>
            <div id="reviewMessageContainer" class="mt-4"></div>
        </div>
    `;

    try {
        const reviews = await getOwnerReviews();
        renderReviewTable(reviews);
    } catch (error) {
        const tbody = document.getElementById(REVIEW_TABLE_BODY_ID);
        const errorMsg = error.message.includes("Session expired") 
            ? "Session expired." 
            : `Error loading data: ${error.message}`;

        if (tbody) tbody.innerHTML = `<tr><td colspan="5" class="p-6 text-center text-red-600 font-medium">${errorMsg}</td></tr>`;
    }
}