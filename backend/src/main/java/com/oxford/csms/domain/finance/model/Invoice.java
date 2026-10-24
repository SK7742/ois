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

    @Column(nullable = false)
    private String billingPeriod; // e.g., "Term 1 - 2026", "October 2026"

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
}
