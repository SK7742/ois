package com.oxford.csms.domain.academic.repository;

import com.oxford.csms.domain.academic.model.ExamTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamTermRepository extends JpaRepository<ExamTerm, Long> {
    Optional<ExamTerm> findByTermName(String termName);
}
