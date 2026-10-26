package com.oxford.csms.domain.finance.service;

import com.oxford.csms.domain.finance.model.FeeCategory;
import com.oxford.csms.domain.finance.model.FeeStructure;
import com.oxford.csms.domain.finance.model.Invoice;
import com.oxford.csms.domain.finance.model.PaymentRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FeeService {
    FeeStructure createFeeStructure(FeeStructure feeStructure);
    List<FeeStructure> getFeeStructuresByGrade(String grade);
    List<FeeStructure> getAllFeeStructures();

    Invoice generateInvoice(Long studentId, String billingPeriod, LocalDate dueDate, String scholarshipType);
    Invoice createDetailedInvoice(Invoice invoice);
    int generateBulkInvoicesForClass(String grade, String billingPeriod, LocalDate dueDate);
    List<Invoice> getAllInvoices();
    List<Invoice> getInvoicesByStudent(Long studentId);
    Invoice getInvoiceById(Long id);

    PaymentRecord recordPayment(Long invoiceId, BigDecimal amount, PaymentRecord.PaymentMethod method, String transactionRef, String recordedBy, String remarks);
    List<PaymentRecord> getPaymentsForInvoice(Long invoiceId);

    Map<String, Object> getFeeCollectionSummary();
}
