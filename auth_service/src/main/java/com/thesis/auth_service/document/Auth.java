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
@Document(value = "auth")
public class Auth {
    @Id
    String id;

    String user_id;

    String username;

    String email;

    String password;

    Set<String> roles;

    String status;

    String created_at;

    String updated_at;
}
