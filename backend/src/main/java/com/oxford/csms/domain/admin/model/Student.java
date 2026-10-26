package com.oxford.csms.domain.admin.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String admissionNo;

    private Integer rollNo;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate dateOfBirth;

    private String gender;

    @Column(nullable = false)
    private String currentClass;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private String guardianName;

    @Column(nullable = false)
    private String guardianPhone;

    private String guardianEmail;

    private String bloodGroup;

    private String residentialAddress;

    private Long busRouteId;

    private Double attendanceRate = 95.0;

    @Column(length = 1000)
    private String notesSummary;

    @Enumerated(EnumType.STRING)
    private StudentFeeStatus feeStatus = StudentFeeStatus.CLEARED;

    private boolean active = true;

    // --- Transportation Option ---
    private Boolean transportOpted = false;

    private String busRouteName;

    private String pickupDropPoint;

    private BigDecimal transportFeeMonthly = BigDecimal.ZERO;

    // --- Fee Detail & Concession ---
    private String feeCategory = "GENERAL";

    private Double concessionPercentage = 0.0;

    private BigDecimal concessionAmount = BigDecimal.ZERO;

    private String concessionReason;

    // --- Additional Identity Info ---
    private String aadharNo;

    private String emergencyContact;

    public enum StudentFeeStatus {
        CLEARED, PENDING, OVERDUE
    }

    public Student() {}

    public Student(String admissionNo, Integer rollNo, String firstName, String lastName,
                   LocalDate dateOfBirth, String gender, String currentClass, String section,
                   String guardianName, String guardianPhone, String guardianEmail,
                   String bloodGroup, String residentialAddress) {
        this.admissionNo = admissionNo;
        this.rollNo = rollNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.currentClass = currentClass;
        this.section = section;
        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
        this.guardianEmail = guardianEmail;
        this.bloodGroup = bloodGroup;
        this.residentialAddress = residentialAddress;
        this.active = true;
        this.feeStatus = StudentFeeStatus.CLEARED;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public Integer getRollNo() {
        return rollNo;
    }

    public void setRollNo(Integer rollNo) {
        this.rollNo = rollNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return (firstName != null ? firstName : "") + (lastName != null && !lastName.isBlank() ? " " + lastName : "");
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

    public String getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = currentClass;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public void setGuardianEmail(String guardianEmail) {
        this.guardianEmail = guardianEmail;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public Long getBusRouteId() {
        return busRouteId;
    }

    public void setBusRouteId(Long busRouteId) {
        this.busRouteId = busRouteId;
    }

    public Double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(Double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public String getNotesSummary() {
        return notesSummary;
    }

    public void setNotesSummary(String notesSummary) {
        this.notesSummary = notesSummary;
    }

    public StudentFeeStatus getFeeStatus() {
        return feeStatus;
    }

    public void setFeeStatus(StudentFeeStatus feeStatus) {
        this.feeStatus = feeStatus;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // --- Transportation Option Getters & Setters ---
    public Boolean getTransportOpted() {
        return transportOpted != null && transportOpted;
    }

    public void setTransportOpted(Boolean transportOpted) {
        this.transportOpted = transportOpted;
    }

    public String getBusRouteName() {
        return busRouteName;
    }

    public void setBusRouteName(String busRouteName) {
        this.busRouteName = busRouteName;
    }

    public String getPickupDropPoint() {
        return pickupDropPoint;
    }

    public void setPickupDropPoint(String pickupDropPoint) {
        this.pickupDropPoint = pickupDropPoint;
    }

    public BigDecimal getTransportFeeMonthly() {
        return transportFeeMonthly != null ? transportFeeMonthly : BigDecimal.ZERO;
    }

    public void setTransportFeeMonthly(BigDecimal transportFeeMonthly) {
        this.transportFeeMonthly = transportFeeMonthly;
    }

    // --- Fee Detail & Concession Getters & Setters ---
    public String getFeeCategory() {
        return feeCategory != null ? feeCategory : "GENERAL";
    }

    public void setFeeCategory(String feeCategory) {
        this.feeCategory = feeCategory;
    }

    public Double getConcessionPercentage() {
        return concessionPercentage != null ? concessionPercentage : 0.0;
    }

    public void setConcessionPercentage(Double concessionPercentage) {
        this.concessionPercentage = concessionPercentage;
    }

    public BigDecimal getConcessionAmount() {
        return concessionAmount != null ? concessionAmount : BigDecimal.ZERO;
    }

    public void setConcessionAmount(BigDecimal concessionAmount) {
        this.concessionAmount = concessionAmount;
    }

    public String getConcessionReason() {
        return concessionReason;
    }

    public void setConcessionReason(String concessionReason) {
        this.concessionReason = concessionReason;
    }

    // --- Additional Identity Info Getters & Setters ---
    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}
