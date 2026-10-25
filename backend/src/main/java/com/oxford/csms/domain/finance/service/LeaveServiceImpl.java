package com.oxford.csms.domain.finance.service;

import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.admin.repository.StudentRepository;
import com.oxford.csms.domain.finance.model.LeaveRequest;
import com.oxford.csms.domain.finance.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final StudentRepository studentRepository;

    public LeaveServiceImpl(LeaveRequestRepository leaveRequestRepository,
                            StudentRepository studentRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.studentRepository = studentRepository;
    }

    private void enrichStudentDetails(LeaveRequest req) {
        if ("STUDENT".equalsIgnoreCase(req.getApplicantType())) {
            if (req.getStudentClass() == null || req.getStudentClass().isBlank() || req.getRollNo() == null) {
                if (req.getApplicantId() != null) {
                    studentRepository.findById(req.getApplicantId()).ifPresent(student -> {
                        String cls = (student.getCurrentClass() != null ? student.getCurrentClass() : "") +
                                     (student.getSection() != null ? student.getSection() : "");
                        req.setStudentClass(cls.trim());
                        if (student.getRollNo() != null) {
                            req.setRollNo(String.valueOf(student.getRollNo()));
                        }
                    });
                }
            }
        }
    }

    @Override
    public LeaveRequest applyLeave(LeaveRequest request) {
        request.setStatus(LeaveRequest.LeaveStatus.PENDING);
        if ("STUDENT".equalsIgnoreCase(request.getApplicantType()) && request.getApplicantId() != null) {
            studentRepository.findById(request.getApplicantId()).ifPresent(student -> {
                String cls = (student.getCurrentClass() != null ? student.getCurrentClass() : "") +
                             (student.getSection() != null ? student.getSection() : "");
                request.setStudentClass(cls.trim());
                if (student.getRollNo() != null) {
                    request.setRollNo(String.valueOf(student.getRollNo()));
                }
            });
        }
        return leaveRequestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> list = leaveRequestRepository.findAll();
        for (LeaveRequest req : list) {
            enrichStudentDetails(req);
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeaveRequestsByApplicant(Long applicantId, String applicantType) {
        List<LeaveRequest> list = leaveRequestRepository.findByApplicantIdAndApplicantType(applicantId, applicantType);
        for (LeaveRequest req : list) {
            enrichStudentDetails(req);
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getStudentLeaveRequestsByClass(String studentClass) {
        List<LeaveRequest> all = getAllLeaveRequests();
        if (studentClass == null || studentClass.isBlank()) {
            return all.stream()
                    .filter(l -> "STUDENT".equalsIgnoreCase(l.getApplicantType()))
                    .toList();
        }
        String cleanTarget = studentClass.replaceAll("\\s+", "").toLowerCase();
        return all.stream()
                .filter(l -> "STUDENT".equalsIgnoreCase(l.getApplicantType()))
                .filter(l -> {
                    if (l.getStudentClass() == null) return false;
                    String itemClass = l.getStudentClass().replaceAll("\\s+", "").toLowerCase();
                    return itemClass.contains(cleanTarget) || cleanTarget.contains(itemClass);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequest getLeaveRequestById(Long id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        enrichStudentDetails(request);
        return request;
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
