package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.academic.model.ExamSubject;
import com.oxford.csms.domain.academic.model.ExamTerm;
import com.oxford.csms.domain.academic.model.StudentGrade;
import com.oxford.csms.domain.academic.service.ExamService;
import com.oxford.csms.web.dto.ReportCardDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping("/terms")
    public ResponseEntity<ApiResponse<ExamTerm>> createTerm(@RequestBody ExamTerm term) {
        return ResponseEntity.ok(ApiResponse.ok("Exam term created", examService.createExamTerm(term)));
    }

    @GetMapping("/terms")
    public ResponseEntity<ApiResponse<List<ExamTerm>>> getAllTerms() {
        return ResponseEntity.ok(ApiResponse.ok(examService.getAllExamTerms()));
    }

    @PostMapping("/subjects")
    public ResponseEntity<ApiResponse<ExamSubject>> addSubject(@RequestBody ExamSubject subject) {
        return ResponseEntity.ok(ApiResponse.ok("Exam subject mapped", examService.addExamSubject(subject)));
    }

    @GetMapping("/terms/{termId}/subjects")
    public ResponseEntity<ApiResponse<List<ExamSubject>>> getSubjectsForTerm(
            @PathVariable Long termId,
            @RequestParam String grade) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getSubjectsForTermAndGrade(termId, grade)));
    }

    @PostMapping("/grades")
    public ResponseEntity<ApiResponse<StudentGrade>> recordGrade(@RequestBody StudentGrade grade) {
        return ResponseEntity.ok(ApiResponse.ok("Grade recorded", examService.recordStudentGrade(grade)));
    }

    @PostMapping("/grades/batch")
    public ResponseEntity<ApiResponse<List<StudentGrade>>> recordBatchGrades(@RequestBody List<StudentGrade> grades) {
        List<StudentGrade> saved = grades.stream().map(examService::recordStudentGrade).toList();
        return ResponseEntity.ok(ApiResponse.ok("Batch grades successfully recorded", saved));
    }

    @GetMapping("/terms/{termId}/grades/class")
    public ResponseEntity<ApiResponse<List<StudentGrade>>> getClassGrades(
            @PathVariable Long termId,
            @RequestParam String grade,
            @RequestParam String section) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getGradesForClassInTerm(termId, grade, section)));
    }

    @GetMapping("/terms/{termId}/students/{studentId}/report-card")
    public ResponseEntity<ApiResponse<ReportCardDto>> getReportCard(
            @PathVariable Long termId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(examService.generateReportCard(termId, studentId)));
    }
}
