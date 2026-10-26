package com.oxford.csms.domain.finance.service;

import com.oxford.csms.common.BusinessValidationException;
import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.repository.StudentRepository;
import com.oxford.csms.domain.finance.model.FeeCategory;
import com.oxford.csms.domain.finance.model.FeeStructure;
import com.oxford.csms.domain.finance.model.Invoice;
import com.oxford.csms.domain.finance.model.PaymentRecord;
import com.oxford.csms.domain.finance.repository.FeeStructureRepository;
import com.oxford.csms.domain.finance.repository.InvoiceRepository;
import com.oxford.csms.domain.finance.repository.PaymentRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class FeeServiceImpl implements FeeService {

    private final FeeStructureRepository feeStructureRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final StudentRepository studentRepository;
    private final FeeCalculationService feeCalculationService;

    public FeeServiceImpl(FeeStructureRepository feeStructureRepository,
                          InvoiceRepository invoiceRepository,
                          PaymentRecordRepository paymentRecordRepository,
                          StudentRepository studentRepository,
                          FeeCalculationService feeCalculationService) {
        this.feeStructureRepository = feeStructureRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.studentRepository = studentRepository;
        this.feeCalculationService = feeCalculationService;
    }

    @Override
    public FeeStructure createFeeStructure(FeeStructure feeStructure) {
        return feeStructureRepository.save(feeStructure);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeStructure> getFeeStructuresByGrade(String grade) {
        return feeStructureRepository.findByGrade(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepository.findAll();
    }

    @Override
    public Invoice generateInvoice(Long studentId, String billingPeriod, LocalDate dueDate, String scholarshipType) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        List<FeeStructure> structures = feeStructureRepository.findByGrade(student.getCurrentClass());
        BigDecimal gross = structures.stream()
                .map(FeeStructure::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Fallback default amount if no structure defined yet
        if (gross.compareTo(BigDecimal.ZERO) == 0) {
            gross = new BigDecimal("4500.00"); // Standard Oxford term tuition
        }

        // Include transport fee if student opted for institutional transport
        BigDecimal transportFee = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(student.getTransportOpted()) && student.getTransportFeeMonthly() != null && student.getTransportFeeMonthly().compareTo(BigDecimal.ZERO) > 0) {
            transportFee = student.getTransportFeeMonthly();
            gross = gross.add(transportFee);
        }

        FeeCalculationService.FeeCalculationResult calcResult =
                feeCalculationService.calculateNetFee(gross, student, scholarshipType);

        String invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate due = dueDate != null ? dueDate : LocalDate.now().plusDays(15);

        Invoice invoice = new Invoice(
                invoiceNumber,
                student.getId(),
                student.getFullName(),
                student.getAdmissionNo(),
                student.getCurrentClass(),
                billingPeriod != null ? billingPeriod : "Term 1 - 2026",
                calcResult.getGrossAmount(),
                calcResult.getDiscountAmount(),
                calcResult.getNetPayable(),
                due,
                calcResult.getScholarshipApplied()
        );
        invoice.setSection(student.getSection());
        invoice.setRollNo(student.getRollNo());
        invoice.setBillingMonth(billingPeriod != null ? billingPeriod : "Term 1 - 2026");
        invoice.setTuitionFee(calcResult.getGrossAmount().subtract(transportFee));
        invoice.setConveyanceFee(transportFee);

        student.setFeeStatus(Student.StudentFeeStatus.PENDING);
        studentRepository.save(student);

        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice createDetailedInvoice(Invoice invoice) {
        if (invoice.getStudentId() != null) {
            studentRepository.findById(invoice.getStudentId()).ifPresent(student -> {
                if (invoice.getStudentName() == null || invoice.getStudentName().isBlank()) {
                    invoice.setStudentName(student.getFullName());
                }
                if (invoice.getAdmissionNo() == null || invoice.getAdmissionNo().isBlank()) {
                    invoice.setAdmissionNo(student.getAdmissionNo());
                }
                if (invoice.getGrade() == null || invoice.getGrade().isBlank()) {
                    invoice.setGrade(student.getCurrentClass());
                }
                if (invoice.getSection() == null || invoice.getSection().isBlank()) {
                    invoice.setSection(student.getSection());
                }
                if (invoice.getRollNo() == null) {
                    invoice.setRollNo(student.getRollNo());
                }
                // Auto-apply transport / conveyance fee if opted by student and not set in invoice
                if (Boolean.TRUE.equals(student.getTransportOpted()) && student.getTransportFeeMonthly() != null && student.getTransportFeeMonthly().compareTo(BigDecimal.ZERO) > 0) {
                    if (invoice.getConveyanceFee() == null || invoice.getConveyanceFee().compareTo(BigDecimal.ZERO) == 0) {
                        invoice.setConveyanceFee(student.getTransportFeeMonthly());
                    }
                }
                student.setFeeStatus(Student.StudentFeeStatus.PENDING);
                studentRepository.save(student);
            });
        }

        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isBlank()) {
            invoice.setInvoiceNumber("REC-" + String.format("%04d", invoiceRepository.count() + 101));
        }

        BigDecimal itemsGross = invoice.calculateGrossFromItems();
        if (itemsGross.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setGrossAmount(itemsGross);
        } else if (invoice.getGrossAmount() == null || invoice.getGrossAmount().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setGrossAmount(new BigDecimal("4500.00"));
            invoice.setTuitionFee(new BigDecimal("4500.00"));
        }

        BigDecimal discount = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO;
        invoice.setDiscountAmount(discount);
        invoice.setNetPayable(invoice.getGrossAmount().subtract(discount).max(BigDecimal.ZERO));
        if (invoice.getPaidAmount() == null) {
            invoice.setPaidAmount(BigDecimal.ZERO);
        }
        invoice.setStatus(invoice.getPaidAmount().compareTo(invoice.getNetPayable()) >= 0 ? Invoice.InvoiceStatus.PAID : Invoice.InvoiceStatus.PENDING);
        if (invoice.getDueDate() == null) {
            invoice.setDueDate(LocalDate.now().plusDays(15));
        }
        if (invoice.getBillingPeriod() == null || invoice.getBillingPeriod().isBlank()) {
            invoice.setBillingPeriod(invoice.getBillingMonth() != null ? invoice.getBillingMonth() : "Current Term");
        }

        return invoiceRepository.save(invoice);
    }

    @Override
    public int generateBulkInvoicesForClass(String grade, String billingPeriod, LocalDate dueDate) {
        List<Student> students;
        if (grade == null || grade.isBlank() || "ALL".equalsIgnoreCase(grade) || "ALL_STUDENTS".equalsIgnoreCase(grade) || "All Students".equalsIgnoreCase(grade)) {
            students = studentRepository.findAll();
        } else {
            students = studentRepository.findByCurrentClass(grade);
        }
        int count = 0;
        for (Student student : students) {
            generateInvoice(student.getId(), billingPeriod, dueDate, "NONE");
            count++;
        }
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByStudent(Long studentId) {
        return invoiceRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    @Override
    public PaymentRecord recordPayment(Long invoiceId, BigDecimal amount, PaymentRecord.PaymentMethod method,
                                       String transactionRef, String recordedBy, String remarks) {
        return recordPaymentWithDiscount(invoiceId, amount, method, transactionRef, recordedBy, remarks, BigDecimal.ZERO, null);
    }

    @Override
    public PaymentRecord recordPaymentWithDiscount(Long invoiceId, BigDecimal amount, PaymentRecord.PaymentMethod method,
                                                  String transactionRef, String recordedBy, String remarks,
                                                  BigDecimal discount, String discountReason) {
        Invoice invoice = getInvoiceById(invoiceId);

        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new BusinessValidationException("Invoice is already fully paid.");
        }

        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            if (discountReason == null || discountReason.trim().isEmpty()) {
                throw new BusinessValidationException("A mandatory reason must be provided when granting a fee discount.");
            }
            BigDecimal currentDisc = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal newDisc = currentDisc.add(discount);
            invoice.setDiscountAmount(newDisc);
            BigDecimal newNet = invoice.getGrossAmount().subtract(newDisc).max(BigDecimal.ZERO);
            invoice.setNetPayable(newNet);

            String existingScholarship = invoice.getScholarshipApplied();
            if (existingScholarship != null && !existingScholarship.isBlank() && !existingScholarship.equalsIgnoreCase("NONE")) {
                invoice.setScholarshipApplied(existingScholarship + "; Concession: " + discountReason.trim());
            } else {
                invoice.setScholarshipApplied("Concession: " + discountReason.trim());
            }

            remarks = (remarks != null && !remarks.isBlank() ? remarks + " | " : "") +
                    "Discount Granted: Rs. " + discount + " (Reason: " + discountReason.trim() + ")";
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Payment amount cannot be negative.");
        }

        BigDecimal currentPaid = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidAmount = currentPaid.add(amount);
        invoice.setPaidAmount(newPaidAmount);

        if (newPaidAmount.compareTo(invoice.getNetPayable()) >= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        String receiptNumber = "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentRecord record = new PaymentRecord(
                receiptNumber,
                invoiceId,
                invoice.getStudentId(),
                amount,
                method,
                transactionRef != null && !transactionRef.isBlank() ? transactionRef : "TXN-" + System.currentTimeMillis(),
                recordedBy != null && !recordedBy.isBlank() ? recordedBy : "Accounts Office",
                remarks != null && !remarks.isBlank() ? remarks : "Tuition Fee Oxford International School"
        );

        // Check if all student's invoices are cleared
        List<Invoice> studentInvoices = invoiceRepository.findByStudentId(invoice.getStudentId());
        boolean hasPending = studentInvoices.stream().anyMatch(i -> i.getStatus() != Invoice.InvoiceStatus.PAID);
        if (!hasPending) {
            studentRepository.findById(invoice.getStudentId()).ifPresent(s -> {
                s.setFeeStatus(Student.StudentFeeStatus.CLEARED);
                studentRepository.save(s);
            });
        }

        return paymentRecordRepository.save(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> getPaymentsForInvoice(Long invoiceId) {
        return paymentRecordRepository.findByInvoiceId(invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getFeeCollectionSummary() {
        BigDecimal totalBilled = invoiceRepository.getTotalBilledAmount();
        BigDecimal totalCollected = invoiceRepository.getTotalCollectedAmount();
        BigDecimal totalPending = totalBilled.subtract(totalCollected).max(BigDecimal.ZERO);
        long pendingCount = invoiceRepository.countPendingInvoices();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalBilled", totalBilled);
        summary.put("totalCollected", totalCollected);
        summary.put("totalPending", totalPending);
        summary.put("pendingInvoicesCount", pendingCount);
        summary.put("collectionPercentage", totalBilled.compareTo(BigDecimal.ZERO) > 0 ?
                totalCollected.multiply(new BigDecimal(100)).divide(totalBilled, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        return summary;
    }
}
