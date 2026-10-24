package com.oxford.csms.domain.communication.service;

import com.oxford.csms.domain.communication.model.IssueTicket;

import java.util.List;
import java.util.Map;

public interface IssueEscalationService {
    IssueTicket raiseTicket(IssueTicket ticket);
    List<IssueTicket> getAllTickets();
    IssueTicket getTicketById(Long id);
    IssueTicket updateTicketStatus(Long id, IssueTicket.TicketStatus status, String notes);
    IssueTicket escalateTicket(Long id, String reason);
    int runAutoEscalationCheck();
    Map<String, Object> getTicketMetrics();
}
