package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.admin.repository.AdmissionApplicationRepository;
import com.oxford.csms.domain.admin.repository.EmployeeRepository;
import com.oxford.csms.domain.admin.repository.StudentRepository;
import com.oxford.csms.domain.communication.repository.IssueTicketRepository;
import com.oxford.csms.domain.communication.repository.WhatsAppMessageRepository;
import com.oxford.csms.domain.finance.repository.InvoiceRepository;
import com.oxford.csms.domain.finance.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;
    private final AdmissionApplicationRepository applicationRepository;
    private final InvoiceRepository invoiceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final IssueTicketRepository issueTicketRepository;
    private final WhatsAppMessageRepository whatsAppMessageRepository;

    @Value("${oxford.csms.institution.name}")
    private String institutionName;

    @Value("${oxford.csms.institution.location}")
    private String location;

    @Value("${oxford.csms.institution.authorized-by}")
    private String authorizedBy;

    @Value("${oxford.csms.institution.lead-architect}")
    private String leadArchitect;

    @Value("${oxford.csms.institution.target-capacity:2000}")
    private int targetCapacity;

    public DashboardController(StudentRepository studentRepository,
                               EmployeeRepository employeeRepository,
                               AdmissionApplicationRepository applicationRepository,
                               InvoiceRepository invoiceRepository,
                               LeaveRequestRepository leaveRequestRepository,
                               IssueTicketRepository issueTicketRepository,
                               WhatsAppMessageRepository whatsAppMessageRepository) {
        this.studentRepository = studentRepository;
        this.employeeRepository = employeeRepository;
        this.applicationRepository = applicationRepository;
        this.invoiceRepository = invoiceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.issueTicketRepository = issueTicketRepository;
        this.whatsAppMessageRepository = whatsAppMessageRepository;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        long activeStudents = studentRepository.countByActiveTrue();
        long activeStaff = employeeRepository.countByActiveTrue();
        long pendingAdmissions = applicationRepository.count();
        BigDecimal totalBilled = invoiceRepository.getTotalBilledAmount();
        BigDecimal totalCollected = invoiceRepository.getTotalCollectedAmount();
        long pendingLeaves = leaveRequestRepository.count();
        long openTickets = issueTicketRepository.count();
        long escalatedTickets = issueTicketRepository.countByEscalatedTrue();
        long whatsAppSent = whatsAppMessageRepository.count();

        double capacityUtilization = (double) activeStudents / (double) targetCapacity * 100.0;

        Map<String, Object> data = new HashMap<>();
        data.put("institutionName", institutionName);
        data.put("location", location);
        data.put("authorizedBy", authorizedBy);
        data.put("leadArchitect", leadArchitect);
        data.put("targetCapacity", targetCapacity);
        data.put("activeStudents", activeStudents);
        data.put("capacityUtilizationPercentage", Math.round(capacityUtilization * 10.0) / 10.0);
        data.put("activeStaff", activeStaff);
        data.put("pendingAdmissions", pendingAdmissions);
        data.put("totalBilled", totalBilled);
        data.put("totalCollected", totalCollected);
        data.put("pendingLeaves", pendingLeaves);
        data.put("openTickets", openTickets);
        data.put("escalatedTickets", escalatedTickets);
        data.put("whatsAppDispatched", whatsAppSent);

        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
