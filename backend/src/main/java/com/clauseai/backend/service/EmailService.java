package com.clauseai.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("chizurumizu@gmail.com");
        message.setTo(toEmail);
        message.setSubject("ClauseAI - Your Verification Code");
        message.setText("Hello,\n\n" +
                "Your verification code for ClauseAI is: " + otp + "\n\n" +"This code expires in 5 minutes.\n" +"If you did not request this, please ignore this email.\n\n" +"Best,\n" +
                "The ClauseAI Team");

        mailSender.send(message);
        System.out.println("Mail sent successfully to " + toEmail);
    }
}