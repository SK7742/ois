package com.oxford.csms.domain.finance.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
public class Invoice extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String admissionNo;

    @Column(nullable = false)
    private String grade;

    private String section;

    private Integer rollNo;

    private String billingMonth;

    @Column(nullable = false)
    private String billingPeriod; // e.g., "Term 1 - 2026", "October 2026"

    // 14 Particulars matching Oxford International School Fee Receipt
    private BigDecimal admissionFee = BigDecimal.ZERO;
    private BigDecimal tuitionFee = BigDecimal.ZERO;
    private BigDecimal conveyanceFee = BigDecimal.ZERO;
    private BigDecimal smartClassFee = BigDecimal.ZERO; // Smart Class / A.C./ Electric Fee
    private BigDecimal computerFee = BigDecimal.ZERO;
    private BigDecimal lateFee = BigDecimal.ZERO;
    private BigDecimal examFee = BigDecimal.ZERO;
    private BigDecimal sportGamesFee = BigDecimal.ZERO;
    private BigDecimal medicalFee = BigDecimal.ZERO;
    private BigDecimal libraryFee = BigDecimal.ZERO;
    private BigDecimal annualDevelopmentFee = BigDecimal.ZERO;
    private BigDecimal miscellaneousFee = BigDecimal.ZERO;
    private BigDecimal othersFee = BigDecimal.ZERO;
    private BigDecimal previousDues = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal grossAmount = BigDecimal.ZERO;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal netPayable = BigDecimal.ZERO;

    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    private LocalDate dueDate;

    private String scholarshipApplied;

    public enum InvoiceStatus {
        PENDING, PARTIALLY_PAID, PAID, CANCELLED
    }

    public Invoice() {}

    public Invoice(String invoiceNumber, Long studentId, String studentName, String admissionNo,
                   String grade, String billingPeriod, BigDecimal grossAmount,
                   BigDecimal discountAmount, BigDecimal netPayable, LocalDate dueDate, String scholarshipApplied) {
        this.invoiceNumber = invoiceNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.admissionNo = admissionNo;
        this.grade = grade;
        this.billingPeriod = billingPeriod;
        this.grossAmount = grossAmount;
        this.discountAmount = discountAmount;
        this.netPayable = netPayable;
        this.paidAmount = BigDecimal.ZERO;
        this.dueDate = dueDate;
        this.scholarshipApplied = scholarshipApplied;
        this.status = InvoiceStatus.PENDING;
    }

    public BigDecimal getBalanceAmount() {
        BigDecimal net = netPayable != null ? netPayable : BigDecimal.ZERO;
        BigDecimal paid = paidAmount != null ? paidAmount : BigDecimal.ZERO;
        return net.subtract(paid).max(BigDecimal.ZERO);
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getNetPayable() {
        return netPayable;
    }

    public void setNetPayable(BigDecimal netPayable) {
        this.netPayable = netPayable;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getScholarshipApplied() {
        return scholarshipApplied;
    }

    public void setScholarshipApplied(String scholarshipApplied) {
        this.scholarshipApplied = scholarshipApplied;
    }

    public BigDecimal calculateGrossFromItems() {
        BigDecimal total = BigDecimal.ZERO;
        if (admissionFee != null) total = total.add(admissionFee);
        if (tuitionFee != null) total = total.add(tuitionFee);
        if (conveyanceFee != null) total = total.add(conveyanceFee);
        if (smartClassFee != null) total = total.add(smartClassFee);
        if (computerFee != null) total = total.add(computerFee);
        if (lateFee != null) total = total.add(lateFee);
        if (examFee != null) total = total.add(examFee);
        if (sportGamesFee != null) total = total.add(sportGamesFee);
        if (medicalFee != null) total = total.add(medicalFee);
        if (libraryFee != null) total = total.add(libraryFee);
        if (annualDevelopmentFee != null) total = total.add(annualDevelopmentFee);
        if (miscellaneousFee != null) total = total.add(miscellaneousFee);
        if (othersFee != null) total = total.add(othersFee);
        if (previousDues != null) total = total.add(previousDues);
        return total;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getRollNo() {
        return rollNo;
    }

    public void setRollNo(Integer rollNo) {
        this.rollNo = rollNo;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public BigDecimal getAdmissionFee() {
        return admissionFee;
    }

    public void setAdmissionFee(BigDecimal admissionFee) {
        this.admissionFee = admissionFee;
    }

    public BigDecimal getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(BigDecimal tuitionFee) {
        this.tuitionFee = tuitionFee;
    }

    public BigDecimal getConveyanceFee() {
        return conveyanceFee;
    }

    public void setConveyanceFee(BigDecimal conveyanceFee) {
        this.conveyanceFee = conveyanceFee;
    }

    public BigDecimal getSmartClassFee() {
        return smartClassFee;
    }

    public void setSmartClassFee(BigDecimal smartClassFee) {
        this.smartClassFee = smartClassFee;
    }

    public BigDecimal getComputerFee() {
        return computerFee;
    }

    public void setComputerFee(BigDecimal computerFee) {
        this.computerFee = computerFee;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public BigDecimal getExamFee() {
        return examFee;
    }

    public void setExamFee(BigDecimal examFee) {
        this.examFee = examFee;
    }

    public BigDecimal getSportGamesFee() {
        return sportGamesFee;
    }

    public void setSportGamesFee(BigDecimal sportGamesFee) {
        this.sportGamesFee = sportGamesFee;
    }

    public BigDecimal getMedicalFee() {
        return medicalFee;
    }

    public void setMedicalFee(BigDecimal medicalFee) {
        this.medicalFee = medicalFee;
    }

    public BigDecimal getLibraryFee() {
        return libraryFee;
    }

    public void setLibraryFee(BigDecimal libraryFee) {
        this.libraryFee = libraryFee;
    }

    public BigDecimal getAnnualDevelopmentFee() {
        return annualDevelopmentFee;
    }

    public void setAnnualDevelopmentFee(BigDecimal annualDevelopmentFee) {
        this.annualDevelopmentFee = annualDevelopmentFee;
    }

    public BigDecimal getMiscellaneousFee() {
        return miscellaneousFee;
    }

    public void setMiscellaneousFee(BigDecimal miscellaneousFee) {
        this.miscellaneousFee = miscellaneousFee;
    }

    public BigDecimal getOthersFee() {
        return othersFee;
    }

    public void setOthersFee(BigDecimal othersFee) {
        this.othersFee = othersFee;
    }

    public BigDecimal getPreviousDues() {
        return previousDues;
    }

    public void setPreviousDues(BigDecimal previousDues) {
        this.previousDues = previousDues;
    }
}
