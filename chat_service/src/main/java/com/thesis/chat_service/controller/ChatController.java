package com.thesis.chat_service.controller;

import com.thesis.chat_service.dto.request.ChatRequest;
import com.thesis.chat_service.dto.response.ChatResponse;
import com.thesis.chat_service.dto.response.ResultArray;
import com.thesis.chat_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {
    @Autowired
    ChatService chatService;

    @PostMapping("/chat")
    public List<ChatResponse> chat(@RequestBody ChatRequest request){
        return chatService.test(request);
    }
}
