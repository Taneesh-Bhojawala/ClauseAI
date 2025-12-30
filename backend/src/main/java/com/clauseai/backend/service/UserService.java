package com.clauseai.backend.service;

import com.clauseai.backend.dto.response.DashboardResponse;
import com.clauseai.backend.model.ContractAnalysis;
import com.clauseai.backend.model.User;
import com.clauseai.backend.repository.ContractRepository;
import com.clauseai.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractRepository contractRepository;

    public DashboardResponse getUserDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ContractAnalysis> recent3 = contractRepository.findTop3ByUserIdOrderByUploadDateTimeDesc(email);
        List<ContractAnalysis> allContracts = contractRepository.findByUserIdOrderByUploadDateTimeDesc(email);

        double avgScore = 0.0;
        if (!allContracts.isEmpty()) {
            avgScore = allContracts.stream()
                    .mapToDouble(ContractAnalysis::getRiskScore)
                    .average()
                    .orElse(0.0);
        }

        // Round to 1 decimal place for cleaner UI
        avgScore = Math.round(avgScore * 10.0) / 10.0;

        DashboardResponse response = new DashboardResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setTotalCount(contractRepository.countByUserId(email));
        response.setRecentContracts(recent3);
        response.setAverageRiskScore(avgScore);

        return response;
    }
}