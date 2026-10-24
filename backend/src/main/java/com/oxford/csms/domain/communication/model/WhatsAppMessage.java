package com.oxford.csms.domain.communication.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_messages")
public class WhatsAppMessage extends BaseEntity {

    @Column(nullable = false)
    private String recipientPhone;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String templateName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String messageBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.QUEUED;

    private LocalDateTime sentAt;

    private String providerMessageId;

    public enum DeliveryStatus {
        QUEUED, SENT, DELIVERED, FAILED
    }

    public WhatsAppMessage() {}

    public WhatsAppMessage(String recipientPhone, String recipientName, String templateName, String messageBody) {
        this.recipientPhone = recipientPhone;
        this.recipientName = recipientName;
        this.templateName = templateName;
        this.messageBody = messageBody;
        this.status = DeliveryStatus.QUEUED;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public void setProviderMessageId(String providerMessageId) {
        this.providerMessageId = providerMessageId;
    }
}
