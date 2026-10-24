package com.oxford.csms.domain.admin.service;

import com.oxford.csms.domain.admin.model.AdmissionApplication;
import com.oxford.csms.domain.admin.model.AdmissionInquiry;
import com.oxford.csms.domain.admin.model.ApplicationComment;
import com.oxford.csms.domain.admin.model.InquiryComment;
import com.oxford.csms.domain.admin.model.Student;

import java.util.List;

public interface AdmissionService {
    AdmissionInquiry createInquiry(AdmissionInquiry inquiry);
    List<AdmissionInquiry> getAllInquiries();
    AdmissionInquiry getInquiryById(Long id);
    AdmissionInquiry updateInquiryStatus(Long id, AdmissionInquiry.InquiryStatus status, String notes);

    InquiryComment addInquiryComment(Long inquiryId, String authorName, String authorRole, String interactionType, String commentText, String stage);
    List<InquiryComment> getInquiryComments(Long inquiryId);

    AdmissionApplication submitApplication(AdmissionApplication application);
    List<AdmissionApplication> getAllApplications();
    List<AdmissionApplication> searchApplications(String query, String grade, String status, Boolean active);
    AdmissionApplication getApplicationById(Long id);
    AdmissionApplication verifyDocuments(Long id, boolean verified, String remarks);
    AdmissionApplication updateApplicationStatus(Long id, AdmissionApplication.ApplicationStatus status, String remarks);
    AdmissionApplication markApplicationActiveStatus(Long id, boolean active, String reason, String authorName);
    AdmissionApplication adjustApplicationClass(Long id, String targetGrade, String section, String reason, String authorName);
    Student enrollApplication(Long applicationId, String targetClass, String assignedSection, Integer rollNo);

    ApplicationComment addApplicationComment(Long applicationId, String authorName, String authorRole, String interactionType,
                                            String commentText, String previousClass, String recommendedClass,
                                            String previousStatus, String newStatus);
    List<ApplicationComment> getApplicationComments(Long applicationId);
}
