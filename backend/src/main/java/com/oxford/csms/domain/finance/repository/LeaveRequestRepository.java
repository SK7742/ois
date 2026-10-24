package com.oxford.csms.domain.finance.repository;

import com.oxford.csms.domain.finance.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByApplicantIdAndApplicantType(Long applicantId, String applicantType);
    List<LeaveRequest> findByStatus(LeaveRequest.LeaveStatus status);
    long countByStatus(LeaveRequest.LeaveStatus status);
}
