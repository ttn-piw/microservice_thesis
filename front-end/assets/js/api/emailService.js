const EMAIL_API_ENDPOINT = 'http://localhost:8888/api/v1/bookings/email/send';

const EMAIL_TEMPLATE_HTML = `
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Booking Confirmation</title>
    <style>
        body { margin: 0; padding: 0; background-color: #f4f4f4; font-family: 'Arial', sans-serif; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
        table { border-spacing: 0; border-collapse: collapse; }
        img { border: 0; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }
        
        @media screen and (max-width: 600px) {
            .container { width: 100% !important; }
            .mobile-stack { display: block !important; width: 100% !important; }
            .mobile-padding { padding: 20px 15px !important; }
            .header-text { font-size: 24px !important; }
        }
    </style>
</head>
<body style="margin: 0; padding: 0; background-color: #f4f4f4;">
    <table border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color: #f4f4f4;">
        <tr>
            <td align="center" style="padding: 20px 0;">
                <table border="0" cellpadding="0" cellspacing="0" width="600" class="container" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1);">
                    <!-- HEADER: Logo & Title -->
                    <tr>
                        <td align="center" style="background-color: #5392f9; padding: 40px 20px;">
                            <!-- Logo Text or Image -->
                            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold; letter-spacing: 1px;">TNEcoHotel</h1>
                            <p style="color: #e0f0ff; margin: 10px 0 0 0; font-size: 16px;">Booking Confirmation</p>
                        </td>
                    </tr>

                    <!-- SUCCESS MESSAGE -->
                    <tr>
                        <td class="mobile-padding" style="padding: 40px 40px 20px 40px; text-align: center;">
                            <img src="https://cdn-icons-png.flaticon.com/512/190/190411.png" alt="Success" width="64" height="64" style="display: block; margin: 0 auto 20px auto;">
                            <h2 style="color: #333333; margin: 0 0 10px 0; font-size: 24px;">Thank you, {{ params.guestName }}!</h2>
                            <p style="color: #666666; margin: 0; font-size: 16px; line-height: 1.5;">
                                Your booking at <strong>{{ params.hotelName }}</strong> has been confirmed.
                            </p>
                        </td>
                    </tr>

                    <!-- BOOKING ID BOX -->
                    <tr>
                        <td style="padding: 0 40px;">
                            <table border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color: #f8f9fa; border: 2px dashed #e0e0e0; border-radius: 8px;">
                                <tr>
                                    <td align="center" style="padding: 15px;">
                                        <span style="color: #888888; font-size: 12px; text-transform: uppercase; display: block;">Booking Reference ID</span>
                                        <span style="color: #5392f9; font-size: 24px; font-weight: bold; letter-spacing: 2px;">{{ params.bookingId }}</span>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>

                    <!-- DATE DETAILS -->
                    <tr>
                        <td class="mobile-padding" style="padding: 30px 40px;">
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <td align="center" width="45%" valign="top">
                                        <p style="color: #888888; font-size: 12px; text-transform: uppercase; margin: 0 0 5px 0;">Check-in</p>
                                        <h3 style="color: #333333; margin: 0; font-size: 18px;">{{ params.checkInDate }}</h3>
                                        <p style="color: #666666; font-size: 12px; margin: 5px 0 0 0;">After 14:00</p>
                                    </td>
                                    <td align="center" width="10%" valign="middle">
                                        <img src="https://cdn-icons-png.flaticon.com/512/545/545682.png" width="20" style="opacity: 0.5;">
                                    </td>
                                    <td align="center" width="45%" valign="top">
                                        <p style="color: #888888; font-size: 12px; text-transform: uppercase; margin: 0 0 5px 0;">Check-out</p>
                                        <h3 style="color: #333333; margin: 0; font-size: 18px;">{{ params.checkOutDate }}</h3>
                                        <p style="color: #666666; font-size: 12px; margin: 5px 0 0 0;">Before 12:00</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>

                    <!-- SEPARATOR -->
                    <tr>
                        <td style="padding: 0 40px;">
                            <div style="border-top: 1px solid #eeeeee;"></div>
                        </td>
                    </tr>

                    <!-- ROOM DETAILS & PRICE -->
                    <tr>
                        <td class="mobile-padding" style="padding: 30px 40px;">
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <td colspan="2" style="padding-bottom: 15px;">
                                        <h3 style="margin: 0; color: #333333; font-size: 16px;">Room Details</h3>
                                    </td>
                                </tr>
                                
                                <!-- Room Item Row -->
                                <tr>
                                    <td style="padding: 8px 0; color: #666666; font-size: 14px;">
                                        {{ params.roomDetails }} 
                                        <!-- Ví dụ giá trị: "1x Deluxe Room (3 Nights)" -->
                                    </td>
                                    <td align="right" style="padding: 8px 0; color: #333333; font-size: 14px; font-weight: bold;">
                                        <!-- Có thể để trống nếu giá đã gộp -->
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 20px 0 0 0; border-top: 2px solid #eeeeee; font-size: 16px; font-weight: bold; color: #333333;">
                                        TOTAL PRICE
                                    </td>
                                    <td align="right" style="padding: 20px 0 0 0; border-top: 2px solid #eeeeee; font-size: 24px; font-weight: bold; color: #5392f9;">
                                        {{ params.totalPrice }}
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2" align="right" style="color: #999999; font-size: 12px;">
                                        Includes taxes and fees
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>

                    <!-- SPECIAL REQUESTS -->
                    <tr>
                        <td class="mobile-padding" style="padding: 0px 40px 30px 40px;">
                            <h3 style="margin: 0; color: #333333; font-size: 16px; padding-bottom: 5px;">Special Requests</h3>
                            <p style="color: #666666; font-size: 14px;">{{ params.specialRequests }}</p>
                        </td>
                    </tr>

                    <!-- CTA BUTTON -->
                    <tr>
                        <td align="center" style="padding: 0 40px 40px 40px;">
                            <a href="{{ params.myBookingUrl }}" style="background-color: #36d548; color: #ffffff; display: inline-block; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 6px rgba(54, 213, 72, 0.2);">
                                View My Booking
                            </a>
                        </td>
                    </tr>

                    <!-- FOOTER -->
                    <tr>
                        <td align="center" style="background-color: #333333; padding: 30px 20px;">
                            <p style="color: #ffffff; margin: 0 0 10px 0; font-size: 14px; font-weight: bold;">TNEcoHotel</p>
                            <p style="color: #888888; margin: 0 0 20px 0; font-size: 12px;">
                                123 Hotel Street, City Center<br>
                                support@tnecohotel.com | +84 123 456 789
                            </p>
                            <div style="color: #666666; font-size: 11px;">
                                <a href="#" style="color: #888888; text-decoration: underline;">Privacy Policy</a> &bull; 
                                <a href="#" style="color: #888888; text-decoration: underline;">Terms of Service</a>
                            </div>
                        </td>
                    </tr>

                </table>
                <!-- End Email Container -->
                
                <!-- Unsubscribe Link (Giữ để tương thích Brevo) -->
                <p style="margin-top: 20px; color: #999999; font-size: 12px;">
                    You received this email because you booked with TNEcoHotel.<br>
                    <a href="{{ mirror }}" style="color: #999999;">View in browser</a> | <a href="{{ unsubscribe }}" style="color: #999999;">Unsubscribe</a>
                </p>

            </td>
        </tr>
    </table>

</body>
</html>
`;

function populateTemplate(html, params) {
    let content = html;
    for (const key in params) {

        const regex = new RegExp(`{{\\s*params\\.${key}\\s*}}`, 'g');
        content = content.replace(regex, params[key]);
    }
    
    content = content.replace(/{{\s*mirror\s*}}/g, '#');
    content = content.replace(/{{\s*unsubscribe\s*}}/g, '#');
    return content;
}

export async function sendConfirmationEmail(bookingData, bookingId) {
    
    const emailParams = {
        guestName: bookingData.guestName,
        hotelName: bookingData.hotelName,
        bookingId: bookingId,
        checkInDate: bookingData.checkInDate,
        checkOutDate: bookingData.checkOutDate,
        totalPrice: parseInt(bookingData.totalPrice).toLocaleString() + " VND",
        specialRequests: bookingData.specialRequests,
        myBookingUrl: `http://localhost:8083/api/v1/bookings/me/bookings/${bookingId}`, //update email link
        
        roomDetails: bookingData.roomTypes.map(rt => {
            const nights = Math.ceil(Math.abs(new Date(bookingData.checkOutDate) - new Date(bookingData.checkInDate)) / (1000 * 60 * 60 * 24));
            return `${rt.quantity}x ${rt.roomTypeName} (${nights} Nights)`;
        }).join('<br>'),
    };

    //Fill htmlContent
    const finalHtmlContent = populateTemplate(EMAIL_TEMPLATE_HTML, emailParams)
    
    const payload = {
        to: {
            email: bookingData.email,
            name: bookingData.guestName
        },
        htmlContent: finalHtmlContent,
        subject: `Booking Confirmed: ${bookingData.hotelName} (Ref: ${bookingId})`
    };

    try {
        console.log("Attempting to send confirmation email...");
        const response = await fetch(EMAIL_API_ENDPOINT, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (response.ok || (result.code && result.code === 8083404)) {
            console.log("Email sent successfully. Message ID:", result.data?.messageId);
            return true;
        } else {
            console.error("Email API reported failure:", result);
            return false;
        }
    } catch (error) {
        console.error('Error connecting to email service:', error);
        return false;
    }
}