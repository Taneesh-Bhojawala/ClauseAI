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
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest otpRequest) {
        try {
            authService.generateAndSendOtp(otpRequest);
            return ResponseEntity.ok(Collections.singletonMap("message", "OTP sent successfully to " + otpRequest.getEmail()));
        } catch (Exception e) {
            // This ensures the frontend gets the actual error text (e.g. "Email already registered")
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            String result = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(Collections.singletonMap("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authService.authenticateUser(loginRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        try {
            String result = authService.resetPassword(resetRequest);
            return ResponseEntity.ok(Collections.singletonMap("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        String result = authService.logoutUser();
        return ResponseEntity.ok(Collections.singletonMap("message", result));
    }
}