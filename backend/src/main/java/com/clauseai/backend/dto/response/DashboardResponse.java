package com.clauseai.backend.dto.response;

import com.clauseai.backend.model.ContractAnalysis;
import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse
{
    private String username;
    private String email;
    private List<ContractAnalysis> recentContracts;
}