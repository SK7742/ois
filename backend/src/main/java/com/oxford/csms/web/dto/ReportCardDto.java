package com.oxford.csms.web.dto;

import com.oxford.csms.domain.academic.model.StudentGrade;

import java.util.List;

public class ReportCardDto {
    private String institutionName = "Oxford International School";
    private String institutionLocation = "Harpur, Raxaul, East Champaran, Bihar";
    private String authorizedBy = "Anil Kumar Gupt (Management)";
    private String termName;
    private String academicYear;
    private String studentName;
    private String admissionNo;
    private Integer rollNo;
    private String grade;
    private String section;
    private Double attendancePercentage;
    private List<StudentGrade> subjectGrades;
    private Double totalMarksObtained;
    private Double totalMaxMarks;
    private Double percentage;
    private String overallGrade;
    private String overallResult; // PASS / PROMOTED
    private String remarks;

    public ReportCardDto() {}

    public String getInstitutionName() { return institutionName; }
    public String getInstitutionLocation() { return institutionLocation; }
    public String getAuthorizedBy() { return authorizedBy; }
    public String getTermName() { return termName; }
    public void setTermName(String termName) { this.termName = termName; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getAdmissionNo() { return admissionNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }
    public Integer getRollNo() { return rollNo; }
    public void setRollNo(Integer rollNo) { this.rollNo = rollNo; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public Double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(Double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    public List<StudentGrade> getSubjectGrades() { return subjectGrades; }
    public void setSubjectGrades(List<StudentGrade> subjectGrades) { this.subjectGrades = subjectGrades; }
    public Double getTotalMarksObtained() { return totalMarksObtained; }
    public void setTotalMarksObtained(Double totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; }
    public Double getTotalMaxMarks() { return totalMaxMarks; }
    public void setTotalMaxMarks(Double totalMaxMarks) { this.totalMaxMarks = totalMaxMarks; }
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    public String getOverallGrade() { return overallGrade; }
    public void setOverallGrade(String overallGrade) { this.overallGrade = overallGrade; }
    public String getOverallResult() { return overallResult; }
    public void setOverallResult(String overallResult) { this.overallResult = overallResult; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
