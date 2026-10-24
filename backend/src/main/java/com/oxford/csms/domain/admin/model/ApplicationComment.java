package com.oxford.csms.domain.admin.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "application_comments")
public class ApplicationComment extends BaseEntity {

    @Column(nullable = false)
    private Long applicationId;

    @Column(nullable = false)
    private String authorName;

    private String authorRole;

    @Column(nullable = false)
    private String interactionType; // CLASS_ADJUSTMENT, STATUS_CHANGE, DOCUMENT_VERIFIED, COUNSELING, GENERAL_NOTE, INACTIVATED, REACTIVATED

    @Column(nullable = false, length = 1500)
    private String commentText;

    private String previousClass;

    private String recommendedClass;

    private String previousStatus;

    private String newStatus;

    public ApplicationComment() {}

    public ApplicationComment(Long applicationId, String authorName, String authorRole, String interactionType,
                              String commentText, String previousClass, String recommendedClass,
                              String previousStatus, String newStatus) {
        this.applicationId = applicationId;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.interactionType = interactionType;
        this.commentText = commentText;
        this.previousClass = previousClass;
        this.recommendedClass = recommendedClass;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getPreviousClass() {
        return previousClass;
    }

    public void setPreviousClass(String previousClass) {
        this.previousClass = previousClass;
    }

    public String getRecommendedClass() {
        return recommendedClass;
    }

    public void setRecommendedClass(String recommendedClass) {
        this.recommendedClass = recommendedClass;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
