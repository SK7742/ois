package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.communication.model.IssueTicket;
import com.oxford.csms.domain.communication.service.IssueEscalationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communication/tickets")
public class IssueEscalationController {

    private final IssueEscalationService escalationService;

    public IssueEscalationController(IssueEscalationService escalationService) {
        this.escalationService = escalationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IssueTicket>> raiseTicket(@RequestBody IssueTicket ticket) {
        return ResponseEntity.ok(ApiResponse.ok("Ticket registered", escalationService.raiseTicket(ticket)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IssueTicket>>> getAllTickets() {
        return ResponseEntity.ok(ApiResponse.ok(escalationService.getAllTickets()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IssueTicket>> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(escalationService.getTicketById(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<IssueTicket>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        IssueTicket.TicketStatus status = IssueTicket.TicketStatus.valueOf(payload.get("status"));
        String notes = payload.get("notes");
        return ResponseEntity.ok(ApiResponse.ok("Ticket updated", escalationService.updateTicketStatus(id, status, notes)));
    }

    @PostMapping("/{id}/escalate")
    public ResponseEntity<ApiResponse<IssueTicket>> escalateTicket(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload) {
        String reason = payload != null ? payload.get("reason") : "Manual escalation to Management";
        return ResponseEntity.ok(ApiResponse.ok("Ticket escalated to Higher Management",
                escalationService.escalateTicket(id, reason)));
    }

    @PostMapping("/auto-escalation-check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> runAutoEscalation() {
        int escalated = escalationService.runAutoEscalationCheck();
        return ResponseEntity.ok(ApiResponse.ok("Auto escalation check executed", Map.of("escalatedCount", escalated)));
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMetrics() {
        return ResponseEntity.ok(ApiResponse.ok(escalationService.getTicketMetrics()));
    }
}
