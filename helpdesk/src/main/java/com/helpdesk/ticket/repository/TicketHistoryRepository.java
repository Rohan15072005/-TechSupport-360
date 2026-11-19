package com.helpdesk.ticket.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.helpdesk.ticket.entity.TicketHistory;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
    List<TicketHistory> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
}
