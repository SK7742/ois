package com.oxford.csms.domain.finance.repository;

import com.oxford.csms.domain.finance.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    Optional<PaymentRecord> findByReceiptNumber(String receiptNumber);
    List<PaymentRecord> findByInvoiceId(Long invoiceId);
    List<PaymentRecord> findByStudentId(Long studentId);
}
