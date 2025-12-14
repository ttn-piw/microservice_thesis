package com.thesis.notification_service.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class TemplateService {
    public String generateContent(String templateName, Map<String, Object> params) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + templateName);
            String htmlTemplate = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String value = String.valueOf(entry.getValue());


                    htmlTemplate = htmlTemplate.replace("{{ params." + key + " }}", value);
                    // Handle {{params.KEY}}
                    htmlTemplate = htmlTemplate.replace("{{ params." + key + "}}", value);
                }
            }
            return htmlTemplate;
        } catch (IOException e) {
            throw new RuntimeException("Error loading email template: " + templateName, e);
        }
    }
}