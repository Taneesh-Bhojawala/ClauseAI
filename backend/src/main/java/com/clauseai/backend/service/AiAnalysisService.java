package com.clauseai.backend.service;

import com.clauseai.backend.model.ContractAnalysis;
import com.clauseai.backend.repository.ContractRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AiAnalysisService {

    @Autowired
    private ContractRepository repository;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ContractAnalysis callGemini(String contractText) throws Exception {
        Client client = Client.builder().apiKey(apiKey).build();

        String prompt = "You are a legal expert. Analyze this contract text. " +
                "RETURN ONLY RAW JSON. Do not use Markdown. " +
                "Use this exact schema matching the Java Model: " +
                "{ \"riskScore\": (double 0.0-100.0), " +
                "\"clauses\": [ { \"originalText\": (string), \"summary\": (string), \"riskScore\": (double), \"explanation\": (string) } ] } \n\n" +
                "CONTRACT TEXT: \n" + contractText;

        GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);

        String aiText = response.text();

        aiText = aiText.replace("```json", "").replace("```", "").trim();

        ContractAnalysis analysis = objectMapper.readValue(aiText, ContractAnalysis.class);

        return analysis;
    }

    public ContractAnalysis analyseContract(String contractText, String userId) {
        try {
            ContractAnalysis analysis = callGemini(contractText);
            analysis.setUserId(userId);
            analysis.setUploadDateTime(LocalDateTime.now());

            repository.save(analysis);

            return analysis;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI analysis failed: " + e.getMessage());
        }
    }
}