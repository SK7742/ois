package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.admin.model.AdmissionApplication;
import com.oxford.csms.domain.admin.model.AdmissionInquiry;
import com.oxford.csms.domain.admin.model.ApplicationComment;
import com.oxford.csms.domain.admin.model.InquiryComment;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.service.AdmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/admissions")
public class AdmissionController {

    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @PostMapping("/inquiries")
    public ResponseEntity<ApiResponse<AdmissionInquiry>> createInquiry(@RequestBody AdmissionInquiry inquiry) {
        return ResponseEntity.ok(ApiResponse.ok("Inquiry registered", admissionService.createInquiry(inquiry)));
    }

    @GetMapping("/inquiries")
    public ResponseEntity<ApiResponse<List<AdmissionInquiry>>> getAllInquiries() {
        return ResponseEntity.ok(ApiResponse.ok(admissionService.getAllInquiries()));
    }

    @GetMapping("/inquiries/{id}")
    public ResponseEntity<ApiResponse<AdmissionInquiry>> getInquiryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(admissionService.getInquiryById(id)));
    }

    @PatchMapping("/inquiries/{id}/status")
    public ResponseEntity<ApiResponse<AdmissionInquiry>> updateInquiryStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        AdmissionInquiry.InquiryStatus status = AdmissionInquiry.InquiryStatus.valueOf(payload.get("status"));
        String notes = payload.get("notes");
        return ResponseEntity.ok(ApiResponse.ok("Status updated", admissionService.updateInquiryStatus(id, status, notes)));
    }

    @GetMapping("/inquiries/{id}/comments")
    public ResponseEntity<ApiResponse<List<InquiryComment>>> getInquiryComments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(admissionService.getInquiryComments(id)));
    }

    @PostMapping("/inquiries/{id}/comments")
    public ResponseEntity<ApiResponse<InquiryComment>> addInquiryComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String authorName = payload.get("authorName");
        String authorRole = payload.get("authorRole");
        String interactionType = payload.get("interactionType");
        String commentText = payload.get("commentText");
        String stage = payload.get("stage");

        InquiryComment saved = admissionService.addInquiryComment(id, authorName, authorRole, interactionType, commentText, stage);
        return ResponseEntity.ok(ApiResponse.ok("Comment added to inquiry history", saved));
    }

    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<AdmissionApplication>> submitApplication(@RequestBody AdmissionApplication application) {
        return ResponseEntity.ok(ApiResponse.ok("Application submitted", admissionService.submitApplication(application)));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<AdmissionApplication>>> getAllApplications(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(ApiResponse.ok(admissionService.searchApplications(query, grade, status, active)));
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApiResponse<AdmissionApplication>> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(admissionService.getApplicationById(id)));
    }

    @GetMapping("/applications/{id}/comments")
    public ResponseEntity<ApiResponse<List<ApplicationComment>>> getApplicationComments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(admissionService.getApplicationComments(id)));
    }

    @PostMapping("/applications/{id}/comments")
    public ResponseEntity<ApiResponse<ApplicationComment>> addApplicationComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String authorName = payload.get("authorName");
        String authorRole = payload.get("authorRole");
        String interactionType = payload.get("interactionType");
        String commentText = payload.get("commentText");
        String previousClass = payload.get("previousClass");
        String recommendedClass = payload.get("recommendedClass");
        String newStatus = payload.get("newStatus");

        ApplicationComment saved = admissionService.addApplicationComment(
                id, authorName, authorRole, interactionType, commentText, previousClass, recommendedClass, null, newStatus);
        return ResponseEntity.ok(ApiResponse.ok("Comment added to application history", saved));
    }

    @PatchMapping("/applications/{id}/active")
    public ResponseEntity<ApiResponse<AdmissionApplication>> toggleActive(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        boolean active = Boolean.parseBoolean(String.valueOf(payload.get("active")));
        String reason = (String) payload.get("reason");
        String author = (String) payload.get("authorName");
        return ResponseEntity.ok(ApiResponse.ok("Application active status updated",
                admissionService.markApplicationActiveStatus(id, active, reason, author)));
    }

    @PatchMapping("/applications/{id}/class-adjustment")
    public ResponseEntity<ApiResponse<AdmissionApplication>> adjustClass(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String targetGrade = payload.get("targetGrade");
        String section = payload.get("section");
        String reason = payload.get("reason");
        String author = payload.get("authorName");
        return ResponseEntity.ok(ApiResponse.ok("Class adjusted with justification note",
                admissionService.adjustApplicationClass(id, targetGrade, section, reason, author)));
    }

    @PostMapping("/applications/{id}/verify-documents")
    public ResponseEntity<ApiResponse<AdmissionApplication>> verifyDocuments(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        boolean verified = Boolean.parseBoolean(String.valueOf(payload.get("verified")));
        String remarks = (String) payload.get("remarks");
        return ResponseEntity.ok(ApiResponse.ok("Document verification updated", admissionService.verifyDocuments(id, verified, remarks)));
    }

    @PostMapping("/applications/{id}/enroll")
    public ResponseEntity<ApiResponse<Student>> enrollApplication(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> payload) {
        String grade = payload != null ? (String) payload.get("targetClass") : null;
        String section = payload != null ? (String) payload.get("assignedSection") : null;
        Integer rollNo = (payload != null && payload.get("rollNo") != null) ?
                Integer.parseInt(payload.get("rollNo").toString()) : null;

        Student student = admissionService.enrollApplication(id, grade, section, rollNo);
        return ResponseEntity.ok(ApiResponse.ok("Student enrolled successfully into Oxford International School", student));
    }
}
