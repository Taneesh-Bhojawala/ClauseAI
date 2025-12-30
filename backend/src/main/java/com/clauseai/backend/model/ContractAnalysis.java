package com.clauseai.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "analysis")
public class ContractAnalysis{
    @Id
    private String id;
    @Indexed
    private String userId;
    private String title;
    private String inputType;
    private LocalDateTime uploadDateTime = LocalDateTime.now();
    private double riskScore;
    private List<Clause> clauses;

    @Data
    public static class Clause{
        private String originalText;
        private String summary;
        private double riskScore;
        private String explanation;
    }
}
