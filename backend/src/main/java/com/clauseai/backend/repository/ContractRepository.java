package com.clauseai.backend.repository;

import com.clauseai.backend.model.ContractAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContractRepository extends MongoRepository<ContractAnalysis,String>
{
    List<ContractAnalysis> findByUserIdOrderByUploadDateTimeDesc(String userId);
    List<ContractAnalysis> findByUserIdAndTitleContainsIgnoreCase(String userId,String title);
}