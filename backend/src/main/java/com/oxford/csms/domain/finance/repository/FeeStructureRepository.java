package com.oxford.csms.domain.finance.repository;

import com.oxford.csms.domain.finance.model.FeeCategory;
import com.oxford.csms.domain.finance.model.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByGrade(String grade);
    List<FeeStructure> findByCategory(FeeCategory category);
}
