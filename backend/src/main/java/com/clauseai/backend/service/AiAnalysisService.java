package com.clauseai.backend.service;

import com.clauseai.backend.model.ContractAnalysis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiAnalysisService
{
    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GeminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    private ContractAnalysis callGemini(String contractText) throws Exception
    {
        String prompt = "You are a legal expert. Analyze this contract text. "+"RETURN ONLY RAW JSON. Do not use Markdown. "+
                "Use this exact schema matching the Java Model: "+"{ \"riskScore\": (double 0.0-100.0), "+
                "\"clauses\": [ { \"originalText\": (string), \"summary\": (string), \"riskScore\": (double), \"explanation\": (string) } ] } \n\n"+
                "CONTRACT TEXT: \n"+contractText;

        ObjectNode request = objectMapper.createObjectNode();
        ArrayNode contents = request.putArray("contents");
        ObjectNode contentNode = contents.addObject();
        contentNode.put("role", "user");
        contentNode.putArray("parts").addObject().put("text", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(request.toString(), headers);

        String response = restTemplate.postForObject(GeminiUrl + apiKey, requestEntity, String.class);

        JsonNode root = objectMapper.readTree(response);
        String aiText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        aiText = aiText.replace("```json", "").replace("```", "").trim();

        ContractAnalysis analysis = objectMapper.readValue(aiText, ContractAnalysis.class);

        return analysis;
    }

    public ContractAnalysis analyseContract(String contractText)
    {
        try
        {
            return callGemini(contractText);
        }
        catch (Exception e)
        {
            throw new RuntimeException("AI analysis failed: " + e.getMessage());
        }
    }
}