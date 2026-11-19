package com.helpdesk.dashboard.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.helpdesk.common.enums.TicketStatus;
import com.helpdesk.dashboard.dto.DashboardResponse;
import com.helpdesk.ticket.repository.TicketRepository;

@Service
public class DashboardService {
    private final TicketRepository ticketRepository;
    public DashboardService(TicketRepository ticketRepository) { this.ticketRepository = ticketRepository; }
    public DashboardResponse getDashboard() {
        long total = ticketRepository.count();
        long open = ticketRepository.countByStatus(TicketStatus.NEW)
                + ticketRepository.countByStatus(TicketStatus.ASSIGNED)
                + ticketRepository.countByStatus(TicketStatus.IN_PROGRESS)
                + ticketRepository.countByStatus(TicketStatus.REOPENED);
        long closed = ticketRepository.countByStatus(TicketStatus.CLOSED);
        long resolved = ticketRepository.countByStatus(TicketStatus.RESOLVED);
        long breached = ticketRepository.countByDueAtBeforeAndStatusNotIn(
                LocalDateTime.now(), List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED));
        return new DashboardResponse(total, open, closed, resolved, breached);
    }
}
