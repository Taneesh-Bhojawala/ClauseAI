package com.clauseai.backend.dto.response;

import lombok.Data;

@Data
public class JwtResponse
{
    private String token;
    private String userId;
    private String type = "Bearer";
    private String username;
    private String email;

    public JwtResponse(String token, String userId, String username, String email) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}