package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.service.StudentService;
import com.oxford.csms.domain.communication.model.WhatsAppMessage;
import com.oxford.csms.domain.communication.service.WhatsAppNotificationService;
import com.oxford.csms.domain.finance.model.Invoice;
import com.oxford.csms.domain.finance.service.FeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communication/whatsapp")
public class WhatsAppController {

    private final WhatsAppNotificationService whatsAppService;
    private final FeeService feeService;
    private final StudentService studentService;

    public WhatsAppController(WhatsAppNotificationService whatsAppService,
                              FeeService feeService,
                              StudentService studentService) {
        this.whatsAppService = whatsAppService;
        this.feeService = feeService;
        this.studentService = studentService;
    }

    @PostMapping("/send-custom")
    public ResponseEntity<ApiResponse<WhatsAppMessage>> sendCustom(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");
        String name = payload.get("name");
        String template = payload.get("template");
        String body = payload.get("body");
        return ResponseEntity.ok(ApiResponse.ok("Message enqueued",
                whatsAppService.enqueueMessage(phone, name, template, body)));
    }

    @PostMapping("/trigger/fee-due/{invoiceId}")
    public ResponseEntity<ApiResponse<WhatsAppMessage>> triggerFeeDueAlert(@PathVariable Long invoiceId) {
        Invoice invoice = feeService.getInvoiceById(invoiceId);
        WhatsAppMessage msg = whatsAppService.sendFeeDueAlert(invoice);
        return ResponseEntity.ok(ApiResponse.ok("Fee dues WhatsApp notification dispatched", msg));
    }

    @PostMapping("/trigger/fee-reminder/{studentId}")
    public ResponseEntity<ApiResponse<WhatsAppMessage>> triggerStudentFeeReminder(@PathVariable Long studentId) {
        Student student = studentService.getStudentById(studentId);
        WhatsAppMessage msg = whatsAppService.sendStudentFeeReminder(student);
        return ResponseEntity.ok(ApiResponse.ok("Fee reminder WhatsApp notification dispatched to " + (student.getGuardianName() != null ? student.getGuardianName() : "parent"), msg));
    }

    @PostMapping("/trigger/absent/{studentId}")
    public ResponseEntity<ApiResponse<WhatsAppMessage>> triggerAbsentAlert(
            @PathVariable Long studentId,
            @RequestBody(required = false) Map<String, String> payload) {
        Student student = studentService.getStudentById(studentId);
        String date = payload != null && payload.get("date") != null ? payload.get("date") : LocalDate.now().toString();
        WhatsAppMessage msg = whatsAppService.sendAbsentAlert(student, date);
        return ResponseEntity.ok(ApiResponse.ok("Absent notification dispatched to parent", msg));
    }

    @PostMapping("/trigger/emergency-broadcast")
    public ResponseEntity<ApiResponse<Map<String, Object>>> broadcastEmergency(@RequestBody Map<String, String> payload) {
        String notice = payload.get("message");
        int count = whatsAppService.broadcastEmergencyNotice(notice);
        return ResponseEntity.ok(ApiResponse.ok("Emergency broadcast queued", Map.of("recipients", count, "status", "DISPATCHED")));
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<WhatsAppMessage>>> getLogs() {
        return ResponseEntity.ok(ApiResponse.ok(whatsAppService.getAllMessages()));
    }
}
