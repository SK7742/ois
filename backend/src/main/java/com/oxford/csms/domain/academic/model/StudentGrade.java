package com.oxford.csms.domain.academic.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_grades")
public class StudentGrade extends BaseEntity {

    @Column(nullable = false)
    private Long examTermId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String admissionNo;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private Double marksObtained;

    private Double maxMarks = 100.0;

    private String letterGrade; // A1, A2, B1, B2, C1, C2, D, E (Failed)

    private String remarks;

    public StudentGrade() {}

    public StudentGrade(Long examTermId, Long studentId, String studentName, String admissionNo,
                        String grade, String section, String subjectName, Double marksObtained, Double maxMarks) {
        this.examTermId = examTermId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.admissionNo = admissionNo;
        this.grade = grade;
        this.section = section;
        this.subjectName = subjectName;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks != null ? maxMarks : 100.0;
        this.letterGrade = computeGradeLetter(marksObtained, this.maxMarks);
    }

    public static String computeGradeLetter(Double marks, Double max) {
        if (marks == null || max == null || max == 0) return "N/A";
        double percentage = (marks / max) * 100.0;
        if (percentage >= 91) return "A1";
        if (percentage >= 81) return "A2";
        if (percentage >= 71) return "B1";
        if (percentage >= 61) return "B2";
        if (percentage >= 51) return "C1";
        if (percentage >= 41) return "C2";
        if (percentage >= 33) return "D";
        return "E (Needs Improvement)";
    }

    public Long getExamTermId() {
        return examTermId;
    }

    public void setExamTermId(Long examTermId) {
        this.examTermId = examTermId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Double marksObtained) {
        this.marksObtained = marksObtained;
        this.letterGrade = computeGradeLetter(marksObtained, this.maxMarks);
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
        this.letterGrade = computeGradeLetter(this.marksObtained, maxMarks);
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
