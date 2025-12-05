package com.thesis.chat_service.controller;

import com.thesis.chat_service.dto.request.ChatRequest;
import com.thesis.chat_service.dto.response.ApiResponse;
import com.thesis.chat_service.dto.response.ChatResponse;
import com.thesis.chat_service.dto.response.ChatResponseList;
import com.thesis.chat_service.repository.httpClient.HotelClient;
import com.thesis.chat_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {
    @Autowired
    ChatService chatService;

    @Autowired
    HotelClient hotelClient;


    @GetMapping("/testHotelClient")
    public ApiResponse<List<Map<String, Object>>> chatNormal(@RequestParam(required = false) String city,
                                                             @RequestParam(required = false) String roomType){
        return hotelClient.searchForChat(city,roomType);
    }
    @GetMapping("/callToHotel/{id}")
    public ChatResponse callToHotelService(@PathVariable("id") UUID id, @RequestBody ChatRequest request){
        return chatService.filterRoom(id, request);
    }
//
//    @GetMapping("/filterHotels")
//    public List<ChatResponseList> filterHotels(@RequestBody ChatRequest request){
//        return chatService.filterHotels(request);
//    }

    @PostMapping("/hotels")
    public String hotelsAI(@RequestBody ChatRequest request) {
        return chatService.hotelAI(request);
    }

    @PostMapping("/chat")
    public List<ChatResponseList> chat(@RequestBody ChatRequest request){
        return chatService.listChat(request);
    }

}
