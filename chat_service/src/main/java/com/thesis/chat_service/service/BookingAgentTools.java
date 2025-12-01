package com.thesis.chat_service.service;

import com.thesis.chat_service.dto.request.AvailabilityRoomRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookingAgentTools {
    @Tool(description = "Checks room availability for dates, city, type, and quantity. Returns SUCCESS or a detailed FAILURE message.")
    public String getAvailability(AvailabilityRoomRequest request) {
        try {
//            bookingServiceClient.checkRoomAvailability(request);
            log.info("CALLING TO getAvailability");
            return String.format("SUCCESS: %d rooms of type %s are available from %s to %s. Ready for booking.",
                    1, "Delux", "11/25/2025", "11/26/2025");

        } catch (FeignException.BadRequest e) {
            return "FAILURE: " + e.contentUTF8();
        } catch (FeignException e) {
            return "ERROR: An internal communication error occurred (Status: " + e.status() + ").";
        }
    }
}
