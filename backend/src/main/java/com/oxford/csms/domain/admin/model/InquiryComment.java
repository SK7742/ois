package com.oxford.csms.domain.admin.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inquiry_comments")
public class InquiryComment extends BaseEntity {

    @Column(nullable = false)
    private Long inquiryId;

    @Column(nullable = false)
    private String authorName;

    private String authorRole;

    @Column(nullable = false)
    private String interactionType; // PHONE_CALL, CAMPUS_VISIT, DOCUMENT_FOLLOWUP, ASSESSMENT, GENERAL_NOTE

    @Column(nullable = false, length = 1500)
    private String commentText;

    private String stage; // NEW, CONTACTED, TEST_SCHEDULED, QUALIFIED, DISQUALIFIED

    public InquiryComment() {}

    public InquiryComment(Long inquiryId, String authorName, String authorRole, String interactionType, String commentText, String stage) {
        this.inquiryId = inquiryId;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.interactionType = interactionType;
        this.commentText = commentText;
        this.stage = stage;
    }

    public Long getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId) {
        this.inquiryId = inquiryId;
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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
