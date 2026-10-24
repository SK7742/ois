package com.oxford.csms.domain.communication.repository;

import com.oxford.csms.domain.communication.model.IssueTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueTicketRepository extends JpaRepository<IssueTicket, Long> {
    Optional<IssueTicket> findByTicketNumber(String ticketNumber);
    List<IssueTicket> findByStatus(IssueTicket.TicketStatus status);
    List<IssueTicket> findByPriority(IssueTicket.IssuePriority priority);
    List<IssueTicket> findByEscalatedTrue();
    long countByStatus(IssueTicket.TicketStatus status);
    long countByEscalatedTrue();
}
