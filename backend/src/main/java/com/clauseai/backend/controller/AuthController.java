package com.clauseai.backend.controller;

import com.clauseai.backend.dto.request.LoginRequest;
import com.clauseai.backend.dto.request.OtpRequest;
import com.clauseai.backend.dto.request.PasswordResetRequest;
import com.clauseai.backend.dto.request.SignUpRequest;
import com.clauseai.backend.dto.response.JwtResponse;
import com.clauseai.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody OtpRequest otpRequest) {
        authService.generateAndSendOtp(otpRequest);
        return ResponseEntity.ok(Collections.singletonMap("message", "OTP sent successfully to " + otpRequest.getEmail()));
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        String result = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(Collections.singletonMap("message", result));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        String result = authService.resetPassword(resetRequest);
        return ResponseEntity.ok(Collections.singletonMap("message", result));
    }
}