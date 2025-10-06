package com.thesis.auth_service.document;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {
    @Id
    String id;

    Set<String> roles;

    String studentId;

    String schoolCode;

    String name;

    String gender;

    String email;

    String password;

    String createAt;
}
