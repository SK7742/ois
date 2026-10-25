package com.oxford.csms.domain.finance.service;

import com.oxford.csms.domain.finance.model.LeaveRequest;

import java.util.List;

public interface LeaveService {
    LeaveRequest applyLeave(LeaveRequest request);
    List<LeaveRequest> getAllLeaveRequests();
    List<LeaveRequest> getLeaveRequestsByApplicant(Long applicantId, String applicantType);
    List<LeaveRequest> getStudentLeaveRequestsByClass(String studentClass);
    LeaveRequest getLeaveRequestById(Long id);
    LeaveRequest approveLeave(Long id, String reviewerName, String remarks);
    LeaveRequest rejectLeave(Long id, String reviewerName, String remarks);
    long countPendingRequests();
}
