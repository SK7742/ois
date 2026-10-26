package com.oxford.csms.domain.admin.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "admission_applications")
public class AdmissionApplication extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String applicationNumber;

    private String slNo;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    private String dobWords;

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

    private String motherOccupation;

    private String guardianName;

    private String fatherQualification;

    private String motherQualification;

    private String previousSchool;

    private String previousPercentage;

    private String previousMarks;

    private String nationality = "Indian";

    private String permanentAddress;

    private String presentAddress;

    private String aadharNo;

    private String declarationPlace = "Harpur, Raxaul";

    private LocalDate declarationDate;

    private Boolean declarationAccepted = true;

    private String writtenTestDate;

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

    public String getSlNo() {
        return slNo;
    }

    public void setSlNo(String slNo) {
        this.slNo = slNo;
    }

    public String getDobWords() {
        return dobWords;
    }

    public void setDobWords(String dobWords) {
        this.dobWords = dobWords;
    }

    public String getMotherOccupation() {
        return motherOccupation;
    }

    public void setMotherOccupation(String motherOccupation) {
        this.motherOccupation = motherOccupation;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getFatherQualification() {
        return fatherQualification;
    }

    public void setFatherQualification(String fatherQualification) {
        this.fatherQualification = fatherQualification;
    }

    public String getMotherQualification() {
        return motherQualification;
    }

    public void setMotherQualification(String motherQualification) {
        this.motherQualification = motherQualification;
    }

    public String getPreviousSchool() {
        return previousSchool;
    }

    public void setPreviousSchool(String previousSchool) {
        this.previousSchool = previousSchool;
    }

    public String getPreviousPercentage() {
        return previousPercentage;
    }

    public void setPreviousPercentage(String previousPercentage) {
        this.previousPercentage = previousPercentage;
    }

    public String getPreviousMarks() {
        return previousMarks;
    }

    public void setPreviousMarks(String previousMarks) {
        this.previousMarks = previousMarks;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getPresentAddress() {
        return presentAddress;
    }

    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getDeclarationPlace() {
        return declarationPlace;
    }

    public void setDeclarationPlace(String declarationPlace) {
        this.declarationPlace = declarationPlace;
    }

    public LocalDate getDeclarationDate() {
        return declarationDate;
    }

    public void setDeclarationDate(LocalDate declarationDate) {
        this.declarationDate = declarationDate;
    }

    public Boolean getDeclarationAccepted() {
        return declarationAccepted;
    }

    public void setDeclarationAccepted(Boolean declarationAccepted) {
        this.declarationAccepted = declarationAccepted;
    }

    public String getWrittenTestDate() {
        return writtenTestDate;
    }

    public void setWrittenTestDate(String writtenTestDate) {
        this.writtenTestDate = writtenTestDate;
    }
}
