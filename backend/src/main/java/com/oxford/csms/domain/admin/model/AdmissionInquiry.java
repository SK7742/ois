package com.oxford.csms.domain.admin.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "admission_inquiries")
public class AdmissionInquiry extends BaseEntity {

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false)
    private String parentName;

    @Column(nullable = false)
    private String contactPhone;

    private String email;

    @Column(nullable = false)
    private String targetGrade;

    private String previousSchool;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status = InquiryStatus.NEW;

    private String notes;

    public enum InquiryStatus {
        NEW, CONTACTED, TEST_SCHEDULED, QUALIFIED, DISQUALIFIED
    }

    public AdmissionInquiry() {}

    public AdmissionInquiry(String candidateName, String parentName, String contactPhone, String email, String targetGrade, String previousSchool, String notes) {
        this.candidateName = candidateName;
        this.parentName = parentName;
        this.contactPhone = contactPhone;
        this.email = email;
        this.targetGrade = targetGrade;
        this.previousSchool = previousSchool;
        this.notes = notes;
        this.status = InquiryStatus.NEW;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTargetGrade() {
        return targetGrade;
    }

    public void setTargetGrade(String targetGrade) {
        this.targetGrade = targetGrade;
    }

    public String getPreviousSchool() {
        return previousSchool;
    }

    public void setPreviousSchool(String previousSchool) {
        this.previousSchool = previousSchool;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
