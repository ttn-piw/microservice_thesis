package com.thesis.chat_service.service;

import com.thesis.chat_service.dto.request.ChatRequest;
import com.thesis.chat_service.dto.response.ChatResponse;
import com.thesis.chat_service.dto.response.ResultArray;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ChatService {
    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public List<ChatResponse> test(ChatRequest request){
        MessageType messageType;
        return chatClient
                .prompt(request.getMessage())
                .call()
                .entity(new ParameterizedTypeReference<List<ChatResponse>>() {
                });
    }
}
