package com.oxford.csms.domain.communication.service;

import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.repository.StudentRepository;
import com.oxford.csms.domain.communication.model.WhatsAppMessage;
import com.oxford.csms.domain.communication.repository.WhatsAppMessageRepository;
import com.oxford.csms.domain.finance.model.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class WhatsAppNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationService.class);

    private final WhatsAppMessageRepository messageRepository;
    private final StudentRepository studentRepository;

    public WhatsAppNotificationService(WhatsAppMessageRepository messageRepository,
                                       StudentRepository studentRepository) {
        this.messageRepository = messageRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public WhatsAppMessage enqueueMessage(String phone, String recipientName, String templateName, String body) {
        WhatsAppMessage message = new WhatsAppMessage(phone, recipientName, templateName, body);
        message.setStatus(WhatsAppMessage.DeliveryStatus.QUEUED);
        WhatsAppMessage saved = messageRepository.save(message);

        // Trigger decoupled async delivery
        dispatchAsync(saved.getId());
        return saved;
    }

    @Async
    public CompletableFuture<Void> dispatchAsync(Long messageId) {
        try {
            // Decoupled async simulation: simulate provider network latency (50ms)
            Thread.sleep(50);
            messageRepository.findById(messageId).ifPresent(msg -> {
                msg.setStatus(WhatsAppMessage.DeliveryStatus.DELIVERED);
                msg.setSentAt(LocalDateTime.now());
                msg.setProviderMessageId("WA-MSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                messageRepository.save(msg);
                log.info("WhatsApp dispatched successfully to {} [Template: {}]", msg.getRecipientPhone(), msg.getTemplateName());
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture(null);
    }

    public WhatsAppMessage sendFeeDueAlert(Invoice invoice) {
        String body = String.format(
                "Dear Parent, fee dues of Rs. %s for %s (%s, %s) are pending for %s (Due: %s). " +
                "Kindly pay via our digital portal or at the school cash counter. " +
                "Oxford International School, Harpur, Raxaul, Bihar.",
                invoice.getBalanceAmount(),
                invoice.getStudentName(),
                invoice.getAdmissionNo(),
                invoice.getGrade(),
                invoice.getBillingPeriod(),
                invoice.getDueDate()
        );

        Student student = studentRepository.findById(invoice.getStudentId()).orElse(null);
        String phone = student != null ? student.getGuardianPhone() : "+91-9876543210";
        String recipient = student != null ? student.getGuardianName() : invoice.getStudentName();

        return enqueueMessage(phone, recipient, "FEE_DUES_REMINDER", body);
    }

    public WhatsAppMessage sendStudentFeeReminder(Student student) {
        String feeStatus = student.getFeeStatus() != null ? student.getFeeStatus().name() : "PENDING";
        String body = String.format(
                "Dear %s, this is a reminder from Oxford International School regarding fee status for your ward %s (Class %s %s, Roll No %s). " +
                "Current fee status is: %s. Kindly ensure pending dues are cleared at the school accounts desk or online portal at your earliest convenience. Thank you.",
                student.getGuardianName() != null ? student.getGuardianName() : "Parent",
                student.getFullName(),
                student.getCurrentClass() != null ? student.getCurrentClass() : "",
                student.getSection() != null ? student.getSection() : "",
                student.getRollNo() != null ? student.getRollNo() : "",
                feeStatus
        );

        String phone = student.getGuardianPhone() != null && !student.getGuardianPhone().isBlank() ? student.getGuardianPhone() : "+91-9876543210";
        String recipient = student.getGuardianName() != null ? student.getGuardianName() : student.getFullName();

        return enqueueMessage(phone, recipient, "FEE_REMINDER", body);
    }

    public WhatsAppMessage sendAbsentAlert(Student student, String date) {
        String body = String.format(
                "Dear Parent, your ward %s (Class %s, Roll No %s) is marked ABSENT today (%s). " +
                "Please submit a leave note if this absence is unplanned. " +
                "Oxford International School, Harpur, Raxaul, Bihar.",
                student.getFullName(),
                student.getCurrentClass() + " " + student.getSection(),
                student.getRollNo(),
                date
        );

        return enqueueMessage(student.getGuardianPhone(), student.getGuardianName(), "STUDENT_ABSENT_ALERT", body);
    }

    public int broadcastEmergencyNotice(String announcement) {
        List<Student> students = studentRepository.findAll();
        int sentCount = 0;
        for (Student s : students) {
            if (s.getGuardianPhone() != null && !s.getGuardianPhone().isBlank()) {
                String body = String.format(
                        "URGENT NOTICE - Oxford International School, Harpur, Raxaul:\n%s\n- Authorized By: Anil Kumar Gupt (Management)",
                        announcement
                );
                enqueueMessage(s.getGuardianPhone(), s.getGuardianName(), "EMERGENCY_BROADCAST", body);
                sentCount++;
            }
        }
        return sentCount;
    }

    public List<WhatsAppMessage> getAllMessages() {
        return messageRepository.findAll();
    }
}
