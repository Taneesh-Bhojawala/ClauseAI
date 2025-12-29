package com.clauseai.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetRequest{

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "OTP is required")
    @Size(min = 6,max = 6,message = "Please enter a valid OTP")
    private String otp;

    @NotBlank(message = "Please enter a password")
    @Size(min = 6,max = 40,message = "Password must be between 6 and 40 characters")
    private String newPassword;
}