package com.oxford.csms.domain.communication.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "issue_tickets")
public class IssueTicket extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssuePriority priority;

    private Integer slaHours;

    private LocalDateTime deadline;

    @Column(nullable = false)
    private String reportedBy;

    private String reporterContact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    private String assignedTo;

    private boolean escalated = false;

    private String escalatedTo; // "Anil Kumar Gupt (Management)"

    private LocalDateTime escalatedAt;

    private String escalationReason;

    private String resolutionNotes;

    public enum IssueCategory {
        FACILITIES,
        ACADEMIC_GRIEVANCE,
        IT_SUPPORT,
        TRANSPORTATION,
        ADMINISTRATION,
        SECURITY
    }

    public enum IssuePriority {
        LOW(48),
        MEDIUM(24),
        HIGH(12),
        CRITICAL(4);

        private final int defaultSlaHours;

        IssuePriority(int defaultSlaHours) {
            this.defaultSlaHours = defaultSlaHours;
        }

        public int getDefaultSlaHours() {
            return defaultSlaHours;
        }
    }

    public enum TicketStatus {
        OPEN,
        IN_PROGRESS,
        ESCALATED_TO_MANAGEMENT,
        RESOLVED,
        CLOSED
    }

    public IssueTicket() {}

    public IssueTicket(String ticketNumber, String title, String description,
                       IssueCategory category, IssuePriority priority,
                       String reportedBy, String reporterContact) {
        this.ticketNumber = ticketNumber;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.slaHours = priority.getDefaultSlaHours();
        this.deadline = LocalDateTime.now().plusHours(this.slaHours);
        this.reportedBy = reportedBy;
        this.reporterContact = reporterContact;
        this.status = TicketStatus.OPEN;
    }

    public boolean isSlaBreached() {
        if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
            return false;
        }
        return deadline != null && LocalDateTime.now().isAfter(deadline);
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueCategory getCategory() {
        return category;
    }

    public void setCategory(IssueCategory category) {
        this.category = category;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public void setPriority(IssuePriority priority) {
        this.priority = priority;
    }

    public Integer getSlaHours() {
        return slaHours;
    }

    public void setSlaHours(Integer slaHours) {
        this.slaHours = slaHours;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReporterContact() {
        return reporterContact;
    }

    public void setReporterContact(String reporterContact) {
        this.reporterContact = reporterContact;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public void setEscalated(boolean escalated) {
        this.escalated = escalated;
    }

    public String getEscalatedTo() {
        return escalatedTo;
    }

    public void setEscalatedTo(String escalatedTo) {
        this.escalatedTo = escalatedTo;
    }

    public LocalDateTime getEscalatedAt() {
        return escalatedAt;
    }

    public void setEscalatedAt(LocalDateTime escalatedAt) {
        this.escalatedAt = escalatedAt;
    }

    public String getEscalationReason() {
        return escalationReason;
    }

    public void setEscalationReason(String escalationReason) {
        this.escalationReason = escalationReason;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
