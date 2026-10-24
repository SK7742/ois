package com.oxford.csms.domain.academic.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "exam_terms")
public class ExamTerm extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String termName; // e.g. "Term 1 Mid-Term Examination 2026"

    @Column(nullable = false)
    private String academicYear; // "2026-2027"

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean resultsPublished = false;

    public ExamTerm() {}

    public ExamTerm(String termName, String academicYear, LocalDate startDate, LocalDate endDate) {
        this.termName = termName;
        this.academicYear = academicYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.resultsPublished = false;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isResultsPublished() {
        return resultsPublished;
    }

    public void setResultsPublished(boolean resultsPublished) {
        this.resultsPublished = resultsPublished;
    }
}
