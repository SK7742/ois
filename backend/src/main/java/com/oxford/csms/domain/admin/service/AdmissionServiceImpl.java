package com.oxford.csms.domain.admin.service;

import com.oxford.csms.common.BusinessValidationException;
import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.admin.model.AdmissionApplication;
import com.oxford.csms.domain.admin.model.AdmissionInquiry;
import com.oxford.csms.domain.admin.model.ApplicationComment;
import com.oxford.csms.domain.admin.model.InquiryComment;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.repository.AdmissionApplicationRepository;
import com.oxford.csms.domain.admin.repository.AdmissionInquiryRepository;
import com.oxford.csms.domain.admin.repository.ApplicationCommentRepository;
import com.oxford.csms.domain.admin.repository.InquiryCommentRepository;
import com.oxford.csms.domain.admin.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdmissionServiceImpl implements AdmissionService {

    private final AdmissionInquiryRepository inquiryRepository;
    private final AdmissionApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final InquiryCommentRepository inquiryCommentRepository;
    private final ApplicationCommentRepository applicationCommentRepository;

    public AdmissionServiceImpl(AdmissionInquiryRepository inquiryRepository,
                                AdmissionApplicationRepository applicationRepository,
                                StudentRepository studentRepository,
                                InquiryCommentRepository inquiryCommentRepository,
                                ApplicationCommentRepository applicationCommentRepository) {
        this.inquiryRepository = inquiryRepository;
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.inquiryCommentRepository = inquiryCommentRepository;
        this.applicationCommentRepository = applicationCommentRepository;
    }

    @Override
    public AdmissionInquiry createInquiry(AdmissionInquiry inquiry) {
        AdmissionInquiry saved = inquiryRepository.save(inquiry);
        // Add automatic system creation audit note
        inquiryCommentRepository.save(new InquiryComment(
                saved.getId(),
                "Admissions Portal",
                "System",
                "INITIAL_INQUIRY",
                "Prospect student inquiry registered for " + saved.getTargetGrade() + ". Parent contact: " + saved.getContactPhone(),
                "NEW"
        ));
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdmissionInquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public AdmissionInquiry getInquiryById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found with id: " + id));
    }

    @Override
    public AdmissionInquiry updateInquiryStatus(Long id, AdmissionInquiry.InquiryStatus status, String notes) {
        AdmissionInquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found with id: " + id));
        inquiry.setStatus(status);
        if (notes != null && !notes.isBlank()) {
            inquiry.setNotes(notes);
        }
        AdmissionInquiry updated = inquiryRepository.save(inquiry);

        inquiryCommentRepository.save(new InquiryComment(
                id,
                "Admissions Officer",
                "Staff",
                "STATUS_CHANGE",
                "Inquiry status updated to " + status + (notes != null && !notes.isBlank() ? ". Note: " + notes : ""),
                status.name()
        ));

        return updated;
    }

    @Override
    public InquiryComment addInquiryComment(Long inquiryId, String authorName, String authorRole, String interactionType, String commentText, String stage) {
        AdmissionInquiry inq = getInquiryById(inquiryId);
        if (stage != null && !stage.isBlank()) {
            try {
                inq.setStatus(AdmissionInquiry.InquiryStatus.valueOf(stage));
                inquiryRepository.save(inq);
            } catch (Exception ignored) {}
        }
        InquiryComment comment = new InquiryComment(
                inquiryId,
                authorName != null && !authorName.isBlank() ? authorName : "Admissions Desk",
                authorRole != null && !authorRole.isBlank() ? authorRole : "Staff",
                interactionType != null && !interactionType.isBlank() ? interactionType : "GENERAL_NOTE",
                commentText,
                stage != null ? stage : inq.getStatus().name()
        );
        return inquiryCommentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InquiryComment> getInquiryComments(Long inquiryId) {
        return inquiryCommentRepository.findByInquiryIdOrderByCreatedAtDesc(inquiryId);
    }

    @Override
    public AdmissionApplication submitApplication(AdmissionApplication application) {
        if (application.getApplicationNumber() == null || application.getApplicationNumber().isBlank()) {
            String generatedCode = "REG-" + (1000 + applicationRepository.count() + 1);
            application.setApplicationNumber(generatedCode);
        }
        if (application.getSlNo() == null || application.getSlNo().isBlank()) {
            application.setSlNo("SL-" + String.format("%03d", applicationRepository.count() + 1));
        }
        if (application.getAppliedGrade() == null || application.getAppliedGrade().isBlank()) {
            application.setAppliedGrade(application.getTargetGrade());
        }
        if (application.getDeclarationDate() == null) {
            application.setDeclarationDate(java.time.LocalDate.now());
        }
        if (application.getDeclarationPlace() == null || application.getDeclarationPlace().isBlank()) {
            application.setDeclarationPlace("Harpur, Raxaul");
        }
        if (application.getNationality() == null || application.getNationality().isBlank()) {
            application.setNationality("Indian");
        }
        if (application.getWrittenTestDate() == null || application.getWrittenTestDate().isBlank()) {
            application.setWrittenTestDate(java.time.LocalDate.now().plusDays(7).toString() + " at 10:00 AM");
        }
        if (application.getStatus() == null) {
            application.setStatus(AdmissionApplication.ApplicationStatus.SUBMITTED);
        }
        AdmissionApplication saved = applicationRepository.save(application);

        // Record initial application audit comment
        applicationCommentRepository.save(new ApplicationComment(
                saved.getId(),
                "Admissions Desk",
                "System",
                "INITIAL_SUBMISSION",
                "Formal student admission application submitted for " + saved.getTargetGrade() +
                        (saved.getAppliedGrade() != null && !saved.getAppliedGrade().equals(saved.getTargetGrade()) ?
                                " (Initially applied for: " + saved.getAppliedGrade() + ")" : "") +
                        ". Documents: " + (saved.isDocumentsVerified() ? "Verified" : "Pending Verification"),
                saved.getAppliedGrade(),
                saved.getTargetGrade(),
                null,
                saved.getStatus().name()
        ));

        // If class was adjusted during creation with a reason, log that explicitly
        if (saved.getClassChangeReason() != null && !saved.getClassChangeReason().isBlank()) {
            applicationCommentRepository.save(new ApplicationComment(
                    saved.getId(),
                    "Admissions Management",
                    "Management",
                    "CLASS_ADJUSTMENT",
                    "Class placement adjusted to " + saved.getTargetGrade() +
                            (saved.getAssignedSection() != null ? " (" + saved.getAssignedSection() + ")" : "") +
                            ". Pedagogical justification: " + saved.getClassChangeReason(),
                    saved.getAppliedGrade(),
                    saved.getTargetGrade(),
                    saved.getStatus().name(),
                    saved.getStatus().name()
            ));
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdmissionApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdmissionApplication> searchApplications(String query, String grade, String status, Boolean active) {
        return applicationRepository.findAll().stream()
                .filter(app -> {
                    if (active != null && app.isActive() != active) return false;
                    if (grade != null && !grade.isBlank() && !grade.equalsIgnoreCase(app.getTargetGrade())) return false;
                    if (status != null && !status.isBlank() && !status.equalsIgnoreCase(app.getStatus().name())) return false;
                    if (query != null && !query.isBlank()) {
                        String q = query.toLowerCase().trim();
                        boolean matchesName = app.getStudentName() != null && app.getStudentName().toLowerCase().contains(q);
                        boolean matchesAppNo = app.getApplicationNumber() != null && app.getApplicationNumber().toLowerCase().contains(q);
                        boolean matchesFather = app.getFatherName() != null && app.getFatherName().toLowerCase().contains(q);
                        boolean matchesPhone = app.getContactPhone() != null && app.getContactPhone().contains(q);
                        return matchesName || matchesAppNo || matchesFather || matchesPhone;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdmissionApplication getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    @Override
    public AdmissionApplication verifyDocuments(Long id, boolean verified, String remarks) {
        AdmissionApplication app = getApplicationById(id);
        app.setDocumentsVerified(verified);
        if (verified) {
            app.setStatus(AdmissionApplication.ApplicationStatus.DOCUMENTS_VERIFIED);
        }
        if (remarks != null) {
            app.setReviewRemarks(remarks);
        }
        AdmissionApplication updated = applicationRepository.save(app);

        applicationCommentRepository.save(new ApplicationComment(
                id,
                "Verification Officer",
                "Staff",
                "DOCUMENT_VERIFIED",
                "Credentials and birth/transfer certificates " + (verified ? "verified successfully." : "marked pending/rejected.") +
                        (remarks != null ? " Remarks: " + remarks : ""),
                app.getTargetGrade(),
                app.getTargetGrade(),
                app.getStatus().name(),
                updated.getStatus().name()
        ));

        return updated;
    }

    @Override
    public AdmissionApplication updateApplicationStatus(Long id, AdmissionApplication.ApplicationStatus status, String remarks) {
        AdmissionApplication app = getApplicationById(id);
        AdmissionApplication.ApplicationStatus oldStatus = app.getStatus();
        app.setStatus(status);
        if (remarks != null) {
            app.setReviewRemarks(remarks);
        }
        AdmissionApplication updated = applicationRepository.save(app);

        applicationCommentRepository.save(new ApplicationComment(
                id,
                "Admissions Desk",
                "Staff",
                "STATUS_CHANGE",
                "Status changed from " + oldStatus + " to " + status + (remarks != null ? ". " + remarks : ""),
                app.getTargetGrade(),
                app.getTargetGrade(),
                oldStatus.name(),
                status.name()
        ));

        return updated;
    }

    @Override
    public AdmissionApplication markApplicationActiveStatus(Long id, boolean active, String reason, String authorName) {
        AdmissionApplication app = getApplicationById(id);
        boolean oldActive = app.isActive();
        app.setActive(active);
        if (!active) {
            app.setStatus(AdmissionApplication.ApplicationStatus.INACTIVE);
        } else if (app.getStatus() == AdmissionApplication.ApplicationStatus.INACTIVE) {
            app.setStatus(AdmissionApplication.ApplicationStatus.SUBMITTED);
        }
        AdmissionApplication updated = applicationRepository.save(app);

        String action = active ? "REACTIVATED" : "INACTIVATED";
        String commentMsg = active ?
                "Application restored to active review pipeline. Note: " + (reason != null ? reason : "Reactivated by admin") :
                "Application marked as INACTIVE. Reason: " + (reason != null ? reason : "Archived by administration");

        applicationCommentRepository.save(new ApplicationComment(
                id,
                authorName != null && !authorName.isBlank() ? authorName : "Administration",
                "Management",
                action,
                commentMsg,
                app.getTargetGrade(),
                app.getTargetGrade(),
                oldActive ? "ACTIVE" : "INACTIVE",
                active ? "ACTIVE" : "INACTIVE"
        ));

        return updated;
    }

    @Override
    public AdmissionApplication adjustApplicationClass(Long id, String targetGrade, String section, String reason, String authorName) {
        if (reason == null || reason.trim().isBlank()) {
            throw new BusinessValidationException("A mandatory justification comment is required when adjusting the student's assigned class or section.");
        }
        if (targetGrade == null || targetGrade.trim().isBlank()) {
            throw new BusinessValidationException("Target class is required.");
        }

        AdmissionApplication app = getApplicationById(id);
        String previousClass = app.getTargetGrade();
        if (app.getAppliedGrade() == null || app.getAppliedGrade().isBlank()) {
            app.setAppliedGrade(previousClass);
        }

        app.setTargetGrade(targetGrade);
        if (section != null && !section.isBlank()) {
            app.setAssignedSection(section);
        }
        app.setClassChangeReason(reason);
        AdmissionApplication updated = applicationRepository.save(app);

        applicationCommentRepository.save(new ApplicationComment(
                id,
                authorName != null && !authorName.isBlank() ? authorName : "Academic Management",
                "Management",
                "CLASS_ADJUSTMENT",
                "Class adjusted from " + previousClass + " to " + targetGrade +
                        (section != null ? " (" + section + ")" : "") +
                        ". Academic rationale / Parent consultation: " + reason,
                previousClass,
                targetGrade,
                app.getStatus().name(),
                app.getStatus().name()
        ));

        return updated;
    }

    @Override
    public ApplicationComment addApplicationComment(Long applicationId, String authorName, String authorRole,
                                                   String interactionType, String commentText, String previousClass,
                                                   String recommendedClass, String previousStatus, String newStatus) {
        AdmissionApplication app = getApplicationById(applicationId);
        String oldStatus = previousStatus != null ? previousStatus : app.getStatus().name();
        if (newStatus != null && !newStatus.isBlank()) {
            try {
                app.setStatus(AdmissionApplication.ApplicationStatus.valueOf(newStatus));
                applicationRepository.save(app);
            } catch (Exception ignored) {}
        }
        ApplicationComment comment = new ApplicationComment(
                applicationId,
                authorName != null && !authorName.isBlank() ? authorName : "Admissions Committee",
                authorRole != null && !authorRole.isBlank() ? authorRole : "Staff",
                interactionType != null && !interactionType.isBlank() ? interactionType : "GENERAL_NOTE",
                commentText,
                previousClass != null ? previousClass : app.getTargetGrade(),
                recommendedClass != null ? recommendedClass : app.getTargetGrade(),
                oldStatus,
                newStatus != null ? newStatus : app.getStatus().name()
        );
        return applicationCommentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationComment> getApplicationComments(Long applicationId) {
        return applicationCommentRepository.findByApplicationIdOrderByCreatedAtDesc(applicationId);
    }

    @Override
    public Student enrollApplication(Long applicationId, String targetClass, String assignedSection, Integer rollNo) {
        AdmissionApplication app = getApplicationById(applicationId);
        if (app.getStatus() == AdmissionApplication.ApplicationStatus.ENROLLED) {
            throw new BusinessValidationException("Application is already enrolled as Student ID: " + app.getEnrolledStudentId());
        }

        long nextIndex = studentRepository.count() + 1001;
        String admissionNo = "OIS-2026-" + nextIndex;

        String[] parts = app.getStudentName().trim().split("\\s+", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";

        String grade = (targetClass != null && !targetClass.isBlank()) ? targetClass : app.getTargetGrade();
        String section = (assignedSection != null && !assignedSection.isBlank()) ? assignedSection : (app.getAssignedSection() != null ? app.getAssignedSection() : "A");
        Integer assignedRollNo = rollNo != null ? rollNo : (int) (studentRepository.count() % 40 + 1);

        Student student = new Student(
                admissionNo,
                assignedRollNo,
                firstName,
                lastName,
                app.getDateOfBirth(),
                app.getGender(),
                grade,
                section,
                app.getFatherName(),
                app.getContactPhone(),
                app.getContactEmail(),
                app.getBloodGroup() != null ? app.getBloodGroup() : "B+",
                app.getAddress()
        );

        Student savedStudent = studentRepository.save(student);

        app.setStatus(AdmissionApplication.ApplicationStatus.ENROLLED);
        app.setEnrolledStudentId(savedStudent.getId());
        applicationRepository.save(app);

        applicationCommentRepository.save(new ApplicationComment(
                applicationId,
                "Admissions Registrar",
                "Management",
                "ENROLLED",
                "Student formally admitted and enrolled into Oxford International School. " +
                        "Assigned Admission No: " + admissionNo + ", Class: " + grade + " " + section + ", Roll No: #" + assignedRollNo,
                app.getTargetGrade(),
                grade,
                "DOCUMENTS_VERIFIED",
                "ENROLLED"
        ));

        return savedStudent;
    }
}
