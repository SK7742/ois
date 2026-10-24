package com.oxford.csms.domain.academic.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "exam_subjects")
public class ExamSubject extends BaseEntity {

    @Column(nullable = false)
    private Long examTermId;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private Double maxMarks = 100.0;

    @Column(nullable = false)
    private Double passMarks = 35.0;

    private LocalDate examDate;

    public ExamSubject() {}

    public ExamSubject(Long examTermId, String grade, String subjectName, Double maxMarks, Double passMarks, LocalDate examDate) {
        this.examTermId = examTermId;
        this.grade = grade;
        this.subjectName = subjectName;
        this.maxMarks = maxMarks;
        this.passMarks = passMarks;
        this.examDate = examDate;
    }

    public Long getExamTermId() {
        return examTermId;
    }

    public void setExamTermId(Long examTermId) {
        this.examTermId = examTermId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Double getPassMarks() {
        return passMarks;
    }

    public void setPassMarks(Double passMarks) {
        this.passMarks = passMarks;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }
}
