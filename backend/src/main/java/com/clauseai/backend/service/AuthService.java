package com.clauseai.backend.service;

import com.clauseai.backend.dto.request.LoginRequest;
import com.clauseai.backend.dto.request.OtpRequest;
import com.clauseai.backend.dto.request.PasswordResetRequest;
import com.clauseai.backend.dto.request.SignUpRequest;
import com.clauseai.backend.dto.response.JwtResponse;
import com.clauseai.backend.model.OtpToken;
import com.clauseai.backend.model.User;
import com.clauseai.backend.repository.OtpRepository;
import com.clauseai.backend.repository.UserRepository;
import com.clauseai.backend.security.JwtUtils;
import com.clauseai.backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OtpRepository otpRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    EmailService emailService;

    // --- 1. SEND OTP ---
    public void generateAndSendOtp(OtpRequest otpRequest) {
        String email = otpRequest.getEmail();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (email == null || !email.matches(emailRegex)) {
            throw new RuntimeException("Error: Invalid email format. Please enter a valid email.");
        }
//        if (userRepository.existsByEmail(email)) {
//            throw new RuntimeException("Error: Email is already registered. Please Login.");
//        } -> This should be there for sign up
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        otpRepository.deleteByEmail(email);

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otpCode);
        otpRepository.save(token);

        emailService.sendOtpEmail(email, otpCode);
    }

    // --- 2. SIGNUP ---
    public String registerUser(SignUpRequest signUpRequest) {
        verifyOtp(signUpRequest.getEmail(), signUpRequest.getOtp());

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);
        otpRepository.deleteByEmail(signUpRequest.getEmail()); // Cleanup OTP

        return "User registered successfully!";
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getDisplayName(),
                userDetails.getEmail()
        );
    }

    public String logoutUser() {
        SecurityContextHolder.clearContext();
        return "Log out successful!";
    }

    public String resetPassword(PasswordResetRequest resetRequest) {
        verifyOtp(resetRequest.getEmail(), resetRequest.getOtp());

        User user = userRepository.findByEmail(resetRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: User not found"));

        user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
        userRepository.save(user);

        otpRepository.deleteByEmail(resetRequest.getEmail()); // Cleanup OTP

        return "Password reset successfully!";
    }

    private void verifyOtp(String email, String otpInput) {
        OtpToken otpToken = otpRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("Error: OTP not found. Please request a new one."));

        if (!otpToken.getOtp().equals(otpInput)) {
            throw new RuntimeException("Error: Invalid OTP.");
        }

        if (otpToken.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            otpRepository.deleteByEmail(email);
            throw new RuntimeException("Error: OTP has expired.");
        }
    }
}