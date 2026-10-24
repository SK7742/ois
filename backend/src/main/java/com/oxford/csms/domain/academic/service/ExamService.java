package com.oxford.csms.domain.academic.service;

import com.oxford.csms.domain.academic.model.ExamSubject;
import com.oxford.csms.domain.academic.model.ExamTerm;
import com.oxford.csms.domain.academic.model.StudentGrade;
import com.oxford.csms.web.dto.ReportCardDto;

import java.util.List;

public interface ExamService {
    ExamTerm createExamTerm(ExamTerm term);
    List<ExamTerm> getAllExamTerms();
    ExamTerm getExamTermById(Long id);

    ExamSubject addExamSubject(ExamSubject subject);
    List<ExamSubject> getSubjectsForTermAndGrade(Long examTermId, String grade);

    StudentGrade recordStudentGrade(StudentGrade grade);
    List<StudentGrade> getGradesForStudentInTerm(Long examTermId, Long studentId);
    List<StudentGrade> getGradesForClassInTerm(Long examTermId, String grade, String section);

    ReportCardDto generateReportCard(Long examTermId, Long studentId);
}
