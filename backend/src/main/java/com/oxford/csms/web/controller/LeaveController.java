package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.finance.model.LeaveRequest;
import com.oxford.csms.domain.finance.service.LeaveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LeaveRequest>> applyLeave(@RequestBody LeaveRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Leave application submitted", leaveService.applyLeave(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> getAllLeaves(
            @RequestParam(required = false) Long applicantId,
            @RequestParam(required = false) String applicantType) {
        if (applicantId != null && applicantType != null) {
            return ResponseEntity.ok(ApiResponse.ok(leaveService.getLeaveRequestsByApplicant(applicantId, applicantType)));
        }
        return ResponseEntity.ok(ApiResponse.ok(leaveService.getAllLeaveRequests()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequest>> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(leaveService.getLeaveRequestById(id)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<LeaveRequest>> approveLeave(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload) {
        String reviewer = (payload != null && payload.get("reviewerName") != null) ?
                payload.get("reviewerName") : "Management / Principal";
        String remarks = payload != null ? payload.get("remarks") : "Approved";
        return ResponseEntity.ok(ApiResponse.ok("Leave approved", leaveService.approveLeave(id, reviewer, remarks)));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<LeaveRequest>> rejectLeave(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload) {
        String reviewer = (payload != null && payload.get("reviewerName") != null) ?
                payload.get("reviewerName") : "Management / Principal";
        String remarks = payload != null ? payload.get("remarks") : "Rejected";
        return ResponseEntity.ok(ApiResponse.ok("Leave rejected", leaveService.rejectLeave(id, reviewer, remarks)));
    }
}
