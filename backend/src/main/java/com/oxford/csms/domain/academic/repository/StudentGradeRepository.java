package com.oxford.csms.domain.academic.repository;

import com.oxford.csms.domain.academic.model.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {
    List<StudentGrade> findByExamTermIdAndStudentId(Long examTermId, Long studentId);
    List<StudentGrade> findByExamTermIdAndGradeAndSection(Long examTermId, String grade, String section);
    Optional<StudentGrade> findByExamTermIdAndStudentIdAndSubjectName(Long examTermId, Long studentId, String subjectName);
}
