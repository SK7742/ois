package com.oxford.csms.domain.finance.service;

import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.finance.model.LeaveRequest;
import com.oxford.csms.domain.finance.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveServiceImpl(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    public LeaveRequest applyLeave(LeaveRequest request) {
        request.setStatus(LeaveRequest.LeaveStatus.PENDING);
        return leaveRequestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeaveRequestsByApplicant(Long applicantId, String applicantType) {
        return leaveRequestRepository.findByApplicantIdAndApplicantType(applicantId, applicantType);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
    }

    @Override
    public LeaveRequest approveLeave(Long id, String reviewerName, String remarks) {
        LeaveRequest request = getLeaveRequestById(id);
        request.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        request.setReviewedBy(reviewerName);
        if (remarks != null) {
            request.setRemarks(remarks);
        }
        return leaveRequestRepository.save(request);
    }

    @Override
    public LeaveRequest rejectLeave(Long id, String reviewerName, String remarks) {
        LeaveRequest request = getLeaveRequestById(id);
        request.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        request.setReviewedBy(reviewerName);
        if (remarks != null) {
            request.setRemarks(remarks);
        }
        return leaveRequestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingRequests() {
        return leaveRequestRepository.countByStatus(LeaveRequest.LeaveStatus.PENDING);
    }
}
