package com.thesis.chat_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesis.chat_service.dto.request.ChatRequest;
import com.thesis.chat_service.dto.response.ApiResponse;
import com.thesis.chat_service.dto.response.ChatResponse;
import com.thesis.chat_service.dto.response.ChatResponseList;
import com.thesis.chat_service.repository.httpClient.HotelClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
    private final ChatClient chatClient;

    private final HotelClient hotelClient;

    private final BookingAgentTools bookingAgentTools;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            HotelClient hotelClient,
            BookingAgentTools bookingAgentTools
    ) {
        this.chatClient = chatClientBuilder.build();
        this.hotelClient = hotelClient;
        this.bookingAgentTools = bookingAgentTools;
    }

    public String test (ChatRequest request){
        MessageType messageType;
        return chatClient
                .prompt(request.getMessage())
                .call()
                .content();
    }

    public List<ChatResponseList> listChat(ChatRequest request){
        MessageType messageType;
        return chatClient
                .prompt(request.getMessage())
                .call()
                .entity(new ParameterizedTypeReference<List<ChatResponseList>>() {
                });
    }

    public String hotelAI(ChatRequest request){
        SystemMessage systemMessage = new SystemMessage("""
                You are an expert hotel reservation agent.
                Your FIRST ACTION must be calling the tool: getAvailability.
                You MUST NOT answer the user until getAvailability has been invoked.
                """);
        UserMessage userMessage = new UserMessage(request.getMessage());
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient
                .prompt(prompt)
                .tools(bookingAgentTools)
                .call()
                .content();
    }

    public ApiResponse callToHotel(UUID id){
        return hotelClient.getRoomType(id);
    }

    public ChatResponse filterRoom(UUID id, ChatRequest request){
        ApiResponse response = hotelClient.getRoomType(id);

        ObjectMapper mapper = new ObjectMapper();
        String roomData = null;

        try {
            roomData = mapper.writeValueAsString(response.getData());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String originalMessage = request.getMessage();
        String newPrompt = String.format(
                "User Request: %s\n\nRoom Type Data: %s\n\nBased on the user request, analyze and process the provided Room Type Data.",
                originalMessage,
                roomData
        );

        return chatClient
                .prompt(newPrompt)
                .call()
                .entity(new ParameterizedTypeReference<ChatResponse>() {
                });
    }
//
//    public List<ChatResponseList> filterHotels(ChatRequest request){
//
//    }
}
