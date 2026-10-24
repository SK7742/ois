package com.oxford.csms.domain.academic.repository;

import com.oxford.csms.domain.academic.model.ExamSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSubjectRepository extends JpaRepository<ExamSubject, Long> {
    List<ExamSubject> findByExamTermIdAndGrade(Long examTermId, String grade);
    List<ExamSubject> findByExamTermId(Long examTermId);
}
