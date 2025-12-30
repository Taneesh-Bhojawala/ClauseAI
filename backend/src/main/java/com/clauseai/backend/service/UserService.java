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

        List<ContractAnalysis> contracts = contractRepository.findByUserIdOrderByUploadDateTimeDesc(email);

        DashboardResponse response = new DashboardResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        response.setRecentContracts(contracts);

        return response;
    }
}