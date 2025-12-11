package com.thesis.hotel_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminHotelDashboardResponse {
    private Integer total_hotels;
    private Double star_rating;
}
