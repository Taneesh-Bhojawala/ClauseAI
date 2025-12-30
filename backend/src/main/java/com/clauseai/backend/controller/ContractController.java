package com.clauseai.backend.controller;

import com.clauseai.backend.model.ContractAnalysis;
import com.clauseai.backend.repository.ContractRepository;
import com.clauseai.backend.service.AiAnalysisService;
import com.clauseai.backend.service.PdfExtractionService;
import jakarta.mail.Multipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "https://localhost:3000", allowCredentials = "true")
public class ContractController{
    @Autowired
    private PdfExtractionService pdfExtractionService;
    @Autowired
    private AiAnalysisService aiAnalysisService;
    @Autowired
    private ContractRepository contractRepository;


    @GetMapping("/analyze/pdf")
    public ResponseEntity<?>analyzePDF(@RequestParam("file")MultipartFile file){
        if (file.isEmpty()) return ResponseEntity.badRequest().body("File is empty");

        try {
            // 1. Extract Text
            String text = pdfExtractionService.extractText(file);
            if (text == null) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("PDF text not readable.");
            }

            // 2. AI Analysis
            ContractAnalysis analysis = aiAnalysisService.analyseContract(text, "user1");

            // 3. Save to DB
            analysis.setTitle(file.getOriginalFilename());
            analysis.setInputType("PDF_UPLOAD");
            analysis.setUserId(getCurrentUserEmail());
            contractRepository.save(analysis);
            return ResponseEntity.ok(analysis);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/analyze/text")
    public ResponseEntity<?> analyzeText(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        String title = payload.getOrDefault("title", "Untitled Text");

        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Text cannot be empty");
        }

        try {
            ContractAnalysis analysis = aiAnalysisService.analyseContract(text, "user1");
            analysis.setTitle(title);
            analysis.setInputType("TEXT");
            analysis.setUserId(getCurrentUserEmail());
            contractRepository.save(analysis);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/history")
    public ResponseEntity<List<ContractAnalysis>> getUserHistory() {
        String userId = getCurrentUserEmail();
        List<ContractAnalysis> history = contractRepository.findByUserIdOrderByUploadDateTimeDesc(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContractAnalysis>> searchContracts(@RequestParam String query) {
        String userId = getCurrentUserEmail();
        List<ContractAnalysis> results = contractRepository.findByUserIdAndTitleContainsIgnoreCase(userId, query);
        return ResponseEntity.ok(results);
    }
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }
}