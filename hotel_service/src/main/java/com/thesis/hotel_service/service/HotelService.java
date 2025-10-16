package com.thesis.hotel_service.service;

import com.thesis.hotel_service.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotelService {
    @Autowired
    HotelRepository hotelRepository;


}
