package com.oxford.csms.domain.communication.service;

import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.communication.model.IssueTicket;
import com.oxford.csms.domain.communication.repository.IssueTicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class IssueEscalationServiceImpl implements IssueEscalationService {

    private static final Logger log = LoggerFactory.getLogger(IssueEscalationServiceImpl.class);

    private final IssueTicketRepository ticketRepository;

    @Value("${oxford.csms.institution.authorized-by:Anil Kumar Gupt (Management)}")
    private String managementEscalationTarget;

    public IssueEscalationServiceImpl(IssueTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public IssueTicket raiseTicket(IssueTicket ticket) {
        if (ticket.getTicketNumber() == null || ticket.getTicketNumber().isBlank()) {
            ticket.setTicketNumber("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (ticket.getPriority() == null) {
            ticket.setPriority(IssueTicket.IssuePriority.MEDIUM);
        }
        ticket.setSlaHours(ticket.getPriority().getDefaultSlaHours());
        ticket.setDeadline(LocalDateTime.now().plusHours(ticket.getSlaHours()));
        ticket.setStatus(IssueTicket.TicketStatus.OPEN);

        // Immediate escalation if CRITICAL priority
        if (ticket.getPriority() == IssueTicket.IssuePriority.CRITICAL) {
            ticket.setEscalated(true);
            ticket.setEscalatedTo(managementEscalationTarget);
            ticket.setEscalatedAt(LocalDateTime.now());
            ticket.setEscalationReason("CRITICAL severity immediate priority escalation to Higher Management");
            ticket.setStatus(IssueTicket.TicketStatus.ESCALATED_TO_MANAGEMENT);
        }

        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueTicket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public IssueTicket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    @Override
    public IssueTicket updateTicketStatus(Long id, IssueTicket.TicketStatus status, String notes) {
        IssueTicket ticket = getTicketById(id);
        ticket.setStatus(status);
        if (notes != null) {
            ticket.setResolutionNotes(notes);
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public IssueTicket escalateTicket(Long id, String reason) {
        IssueTicket ticket = getTicketById(id);
        ticket.setEscalated(true);
        ticket.setEscalatedTo(managementEscalationTarget);
        ticket.setEscalatedAt(LocalDateTime.now());
        ticket.setEscalationReason(reason != null ? reason : "Manual SLA breach escalation to Management");
        ticket.setStatus(IssueTicket.TicketStatus.ESCALATED_TO_MANAGEMENT);
        log.warn("Ticket {} has been escalated to {}", ticket.getTicketNumber(), managementEscalationTarget);
        return ticketRepository.save(ticket);
    }

    @Override
    public int runAutoEscalationCheck() {
        List<IssueTicket> openTickets = ticketRepository.findAll();
        int escalatedCount = 0;
        for (IssueTicket ticket : openTickets) {
            if (!ticket.isEscalated() && ticket.isSlaBreached()) {
                ticket.setEscalated(true);
                ticket.setEscalatedTo(managementEscalationTarget);
                ticket.setEscalatedAt(LocalDateTime.now());
                ticket.setEscalationReason("Automated SLA breach (" + ticket.getSlaHours() + "h exceeded)");
                ticket.setStatus(IssueTicket.TicketStatus.ESCALATED_TO_MANAGEMENT);
                ticketRepository.save(ticket);
                escalatedCount++;
            }
        }
        return escalatedCount;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTicketMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalTickets", ticketRepository.count());
        metrics.put("openTickets", ticketRepository.countByStatus(IssueTicket.TicketStatus.OPEN));
        metrics.put("inProgressTickets", ticketRepository.countByStatus(IssueTicket.TicketStatus.IN_PROGRESS));
        metrics.put("escalatedTickets", ticketRepository.countByEscalatedTrue());
        metrics.put("resolvedTickets", ticketRepository.countByStatus(IssueTicket.TicketStatus.RESOLVED));
        metrics.put("managementEscalationContact", managementEscalationTarget);
        return metrics;
    }
}
