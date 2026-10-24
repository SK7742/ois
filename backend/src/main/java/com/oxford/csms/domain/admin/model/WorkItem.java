package com.oxford.csms.domain.admin.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_items")
public class WorkItem extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType = ItemType.TASK;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private String assignedToUsername;

    private String assignedToName;

    @Column(nullable = false)
    private String assignedByUsername;

    private String assignedByName;

    private String assignedByRole; // "MANAGEMENT", "ADMIN", "SELF"

    private LocalDate dueDate;

    private LocalDateTime completedAt;

    public enum ItemType {
        TASK,
        NOTE,
        REMINDER,
        ACADEMIC_DUTY
    }

    public enum Priority {
        URGENT,
        HIGH,
        MEDIUM,
        LOW
    }

    public enum Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }

    public WorkItem() {}

    public WorkItem(String title, String description, ItemType itemType, Priority priority,
                    String assignedToUsername, String assignedToName,
                    String assignedByUsername, String assignedByName, String assignedByRole,
                    LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.itemType = itemType != null ? itemType : ItemType.TASK;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.status = Status.PENDING;
        this.assignedToUsername = assignedToUsername;
        this.assignedToName = assignedToName;
        this.assignedByUsername = assignedByUsername;
        this.assignedByName = assignedByName;
        this.assignedByRole = assignedByRole;
        this.dueDate = dueDate;
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

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (status != Status.COMPLETED) {
            this.completedAt = null;
        }
    }

    public String getAssignedToUsername() {
        return assignedToUsername;
    }

    public void setAssignedToUsername(String assignedToUsername) {
        this.assignedToUsername = assignedToUsername;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public String getAssignedByUsername() {
        return assignedByUsername;
    }

    public void setAssignedByUsername(String assignedByUsername) {
        this.assignedByUsername = assignedByUsername;
    }

    public String getAssignedByName() {
        return assignedByName;
    }

    public void setAssignedByName(String assignedByName) {
        this.assignedByName = assignedByName;
    }

    public String getAssignedByRole() {
        return assignedByRole;
    }

    public void setAssignedByRole(String assignedByRole) {
        this.assignedByRole = assignedByRole;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
