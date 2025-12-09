package com.thesis.chat_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesis.chat_service.dto.request.ChatRequest;
import com.thesis.chat_service.dto.response.ApiResponse;
import com.thesis.chat_service.dto.response.ChatResponse;
import com.thesis.chat_service.dto.response.ChatResponseList;
import com.thesis.chat_service.repository.httpClient.HotelClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;


import java.util.List;

import java.util.UUID;

@Slf4j
@Service
public class ChatService {
    private final ChatClient chatClient;

    private final HotelClient hotelClient;

    private final BookingAgentTools bookingAgentTools;

    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            HotelClient hotelClient,
            BookingAgentTools bookingAgentTools,
            JdbcChatMemoryRepository jdbcChatMemoryRepository
    ) {
        this.hotelClient = hotelClient;
        this.bookingAgentTools = bookingAgentTools;
        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(20)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
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

    public String hotelAI(ChatRequest request) {
        String conversationId = "conversation8";
//        log.info("Request: {}", request);
//
//        String conversationId = request.getSessionId();
//        if (conversationId == null) {
//            conversationId = "chat_" + randomId.substring(0,10);
//            request.setSessionId(conversationId);
//        }
//        log.info("ConversationId: {}", conversationId);

        SystemMessage systemMessage = new SystemMessage("""
                You are an expert hotel reservation agent.
                   RULES:
                   1. Your FIRST ACTION must be calling the tool: getAvailability to find hotels.
                   2. The getAvailability tool will return a list of hotels and room types of this hotels including their 'id' (UUID) for hotel id and and 'name' , 'id' (UUID) for room type id.
                   3. When the user wants to book a room, you MUST use the 'id' (UUID) corresponding to the hotel name from the previous search results.
                   4. DO NOT guess the hotelId. DO NOT use the hotel name as the hotelId.
                   5. If you don't have the hotelId, ask the user to search for the hotel first.
                """);
        UserMessage userMessage = new UserMessage(request.getMessage());
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String finalConversationId = conversationId;
        return chatClient
                .prompt(prompt)
                .tools(bookingAgentTools)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID, finalConversationId))
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
}
