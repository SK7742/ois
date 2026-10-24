package com.oxford.csms.domain.admin.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "admission_applications")
public class AdmissionApplication extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String applicationNumber;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String targetGrade;

    private String appliedGrade;

    private String assignedSection = "A";

    private boolean active = true;

    @Column(length = 1000)
    private String classChangeReason;

    @Column(nullable = false)
    private String fatherName;

    private String fatherOccupation;

    @Column(nullable = false)
    private String motherName;

    @Column(nullable = false)
    private String contactPhone;

    private String contactEmail;

    @Column(nullable = false)
    private String address;

    private String bloodGroup;

    private boolean documentsVerified = false;

    private String birthCertificateRef;

    private String transferCertificateRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    private String reviewRemarks;

    private Long enrolledStudentId;

    public enum ApplicationStatus {
        SUBMITTED, UNDER_REVIEW, DOCUMENTS_VERIFIED, APPROVED, REJECTED, ENROLLED, INACTIVE
    }

    public AdmissionApplication() {}

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTargetGrade() {
        return targetGrade;
    }

    public void setTargetGrade(String targetGrade) {
        this.targetGrade = targetGrade;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherOccupation() {
        return fatherOccupation;
    }

    public void setFatherOccupation(String fatherOccupation) {
        this.fatherOccupation = fatherOccupation;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public boolean isDocumentsVerified() {
        return documentsVerified;
    }

    public void setDocumentsVerified(boolean documentsVerified) {
        this.documentsVerified = documentsVerified;
    }

    public String getBirthCertificateRef() {
        return birthCertificateRef;
    }

    public void setBirthCertificateRef(String birthCertificateRef) {
        this.birthCertificateRef = birthCertificateRef;
    }

    public String getTransferCertificateRef() {
        return transferCertificateRef;
    }

    public void setTransferCertificateRef(String transferCertificateRef) {
        this.transferCertificateRef = transferCertificateRef;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getReviewRemarks() {
        return reviewRemarks;
    }

    public void setReviewRemarks(String reviewRemarks) {
        this.reviewRemarks = reviewRemarks;
    }

    public Long getEnrolledStudentId() {
        return enrolledStudentId;
    }

    public void setEnrolledStudentId(Long enrolledStudentId) {
        this.enrolledStudentId = enrolledStudentId;
    }

    public String getAppliedGrade() {
        return appliedGrade;
    }

    public void setAppliedGrade(String appliedGrade) {
        this.appliedGrade = appliedGrade;
    }

    public String getAssignedSection() {
        return assignedSection;
    }

    public void setAssignedSection(String assignedSection) {
        this.assignedSection = assignedSection;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getClassChangeReason() {
        return classChangeReason;
    }

    public void setClassChangeReason(String classChangeReason) {
        this.classChangeReason = classChangeReason;
    }
}
