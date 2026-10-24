package com.oxford.csms.domain.finance.repository;

import com.oxford.csms.domain.finance.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStudentId(Long studentId);
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    List<Invoice> findByGrade(String grade);

    @Query("SELECT COALESCE(SUM(i.netPayable), 0) FROM Invoice i")
    BigDecimal getTotalBilledAmount();

    @Query("SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i")
    BigDecimal getTotalCollectedAmount();

    @Query("SELECT count(i) FROM Invoice i WHERE i.status = 'PENDING'")
    long countPendingInvoices();
}
