package com.clauseai.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "otp_tokens")
public class OtpToken{
    @Id
    private String id;
    private String otp;
    private String email;
    @Indexed(expireAfter = "5m")
    private LocalDateTime createdAt = LocalDateTime.now();
}