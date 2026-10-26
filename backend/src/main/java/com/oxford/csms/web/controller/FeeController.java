package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.finance.model.FeeStructure;
import com.oxford.csms.domain.finance.model.Invoice;
import com.oxford.csms.domain.finance.model.PaymentRecord;
import com.oxford.csms.domain.finance.service.FeeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance/fees")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @PostMapping("/structures")
    public ResponseEntity<ApiResponse<FeeStructure>> createFeeStructure(@RequestBody FeeStructure structure) {
        return ResponseEntity.ok(ApiResponse.ok("Fee structure defined", feeService.createFeeStructure(structure)));
    }

    @GetMapping("/structures")
    public ResponseEntity<ApiResponse<List<FeeStructure>>> getAllStructures(@RequestParam(required = false) String grade) {
        if (grade != null) {
            return ResponseEntity.ok(ApiResponse.ok(feeService.getFeeStructuresByGrade(grade)));
        }
        return ResponseEntity.ok(ApiResponse.ok(feeService.getAllFeeStructures()));
    }

    @GetMapping("/structure-config")
    public ResponseEntity<ApiResponse<String>> getFeeStructureConfig() {
        return ResponseEntity.ok(ApiResponse.ok("Fee structure configuration loaded", feeService.getFeeStructureConfigJson()));
    }

    @PostMapping("/invoices/generate")
    public ResponseEntity<ApiResponse<Invoice>> generateInvoice(@RequestBody Map<String, Object> payload) {
        Long studentId = Long.parseLong(payload.get("studentId").toString());
        String billingPeriod = (String) payload.get("billingPeriod");
        String scholarshipType = (String) payload.get("scholarshipType");
        LocalDate dueDate = payload.get("dueDate") != null ?
                LocalDate.parse(payload.get("dueDate").toString()) : LocalDate.now().plusDays(15);

        return ResponseEntity.ok(ApiResponse.ok("Invoice generated",
                feeService.generateInvoice(studentId, billingPeriod, dueDate, scholarshipType)));
    }

    @PostMapping("/invoices/detailed")
    public ResponseEntity<ApiResponse<Invoice>> createDetailedInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(ApiResponse.ok("Fee receipt / invoice generated", feeService.createDetailedInvoice(invoice)));
    }

    @PostMapping("/invoices/generate-bulk")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateBulkInvoices(@RequestBody(required = false) Map<String, Object> payload) {
        String grade = payload != null && payload.get("grade") != null ? (String) payload.get("grade") : "ALL";
        String billingPeriod = payload != null && payload.get("billingPeriod") != null ? (String) payload.get("billingPeriod") : "Term 1 - 2026";
        LocalDate dueDate = payload != null && payload.get("dueDate") != null ?
                LocalDate.parse(payload.get("dueDate").toString()) : LocalDate.now().plusDays(15);

        int count = feeService.generateBulkInvoicesForClass(grade, billingPeriod, dueDate);
        return ResponseEntity.ok(ApiResponse.ok("Bulk generation complete", Map.of("invoicesGenerated", count, "grade", grade != null ? grade : "ALL")));
    }

    @GetMapping("/invoices")
    public ResponseEntity<ApiResponse<List<Invoice>>> getAllInvoices(@RequestParam(required = false) Long studentId) {
        if (studentId != null) {
            return ResponseEntity.ok(ApiResponse.ok(feeService.getInvoicesByStudent(studentId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(feeService.getAllInvoices()));
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<ApiResponse<Invoice>> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(feeService.getInvoiceById(id)));
    }

    @PostMapping("/invoices/{id}/pay")
    public ResponseEntity<ApiResponse<PaymentRecord>> recordPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        PaymentRecord.PaymentMethod method = PaymentRecord.PaymentMethod.valueOf(payload.get("paymentMethod").toString());
        String txnRef = (String) payload.get("transactionReference");
        String recordedBy = (String) payload.get("recordedBy");
        String remarks = (String) payload.get("remarks");

        BigDecimal discount = BigDecimal.ZERO;
        if (payload.get("discountAmount") != null && !payload.get("discountAmount").toString().isBlank()) {
            discount = new BigDecimal(payload.get("discountAmount").toString());
        }
        String discountReason = (String) payload.get("discountReason");

        PaymentRecord receipt = feeService.recordPaymentWithDiscount(id, amount, method, txnRef, recordedBy, remarks, discount, discountReason);
        return ResponseEntity.ok(ApiResponse.ok("Payment recorded, receipt generated", receipt));
    }

    @GetMapping("/invoices/{id}/receipts")
    public ResponseEntity<ApiResponse<List<PaymentRecord>>> getReceiptsForInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(feeService.getPaymentsForInvoice(id)));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        return ResponseEntity.ok(ApiResponse.ok(feeService.getFeeCollectionSummary()));
    }
}
