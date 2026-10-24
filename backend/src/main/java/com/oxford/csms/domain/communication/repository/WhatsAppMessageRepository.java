package com.oxford.csms.domain.communication.repository;

import com.oxford.csms.domain.communication.model.WhatsAppMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhatsAppMessageRepository extends JpaRepository<WhatsAppMessage, Long> {
    List<WhatsAppMessage> findByStatus(WhatsAppMessage.DeliveryStatus status);
    List<WhatsAppMessage> findByRecipientPhone(String recipientPhone);
    long countByStatus(WhatsAppMessage.DeliveryStatus status);
}
