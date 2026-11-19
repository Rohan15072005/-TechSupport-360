package com.helpdesk.ticket.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helpdesk.category.entity.Category;
import com.helpdesk.category.repository.CategoryRepository;
import com.helpdesk.common.enums.Role;
import com.helpdesk.common.enums.TicketStatus;
import com.helpdesk.sla.entity.SLA;
import com.helpdesk.sla.repository.SLARepository;
import com.helpdesk.ticket.dto.CreateTicketRequest;
import com.helpdesk.ticket.dto.TicketResponse;
import com.helpdesk.ticket.dto.UpdateTicketStatusRequest;
import com.helpdesk.ticket.entity.Ticket;
import com.helpdesk.ticket.entity.TicketHistory;
import com.helpdesk.ticket.dto.TicketHistoryResponse;
import com.helpdesk.ticket.repository.TicketHistoryRepository;
import com.helpdesk.ticket.repository.TicketRepository;
import com.helpdesk.user.entity.User;
import com.helpdesk.user.repository.UserRepository;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SLARepository slaRepository;
    private final TicketHistoryRepository historyRepository;

    public TicketService(TicketRepository ticketRepository, CategoryRepository categoryRepository,
                         UserRepository userRepository, SLARepository slaRepository,
                         TicketHistoryRepository historyRepository) {
        this.ticketRepository = ticketRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.slaRepository = slaRepository;
        this.historyRepository = historyRepository;
    }

    private void recordHistory(Ticket ticket, User actor, String action, String details) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setChangedBy(actor);
        history.setAction(action);
        history.setDetails(details);
        historyRepository.save(history);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
    }

    private Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setPriority(ticket.getPriority());
        response.setStatus(ticket.getStatus());
        response.setAssignedTo(ticket.getAssignedTo() == null ? null : ticket.getAssignedTo().getEmail());
        response.setCreatedBy(ticket.getCreatedBy() == null ? null : ticket.getCreatedBy().getEmail());
        response.setCategory(ticket.getCategory() == null ? null : ticket.getCategory().getName());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setDueAt(ticket.getDueAt());
        response.setResolvedAt(ticket.getResolvedAt());
        return response;
    }

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request, String email) {
        User user = getUser(email);
        if (user.getRole() == Role.SUPPORT_AGENT) {
            throw new AccessDeniedException("Support agents cannot submit employee tickets");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Ticket title is required");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Ticket description is required");
        }
        if (request.getPriority() == null || request.getCategoryId() == null) {
            throw new IllegalArgumentException("Priority and category are required");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle().trim());
        ticket.setDescription(request.getDescription().trim());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.NEW);
        ticket.setCreatedBy(user);
        ticket.setCategory(category);
        slaRepository.findByCategory(category).ifPresent(sla -> {
            if (sla.getResolutionTimeHours() != null && sla.getResolutionTimeHours() > 0) {
                ticket.setDueAt(LocalDateTime.now().plusHours(sla.getResolutionTimeHours()));
            }
        });
        Ticket saved = ticketRepository.save(ticket);
        recordHistory(saved, user, "CREATED", "Ticket created with priority " + saved.getPriority());
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets(String email) {
        User user = getUser(email);
        if (user.getRole() != Role.ADMIN) throw new AccessDeniedException("Only administrators can view all tickets");
        return ticketRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets(String email) {
        User user = getUser(email);
        List<Ticket> tickets = user.getRole() == Role.SUPPORT_AGENT
                ? ticketRepository.findByAssignedTo(user)
                : ticketRepository.findByCreatedBy(user);
        return tickets.stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public TicketResponse updateStatus(Long ticketId, TicketStatus status, String email) {
        if (status == null) throw new IllegalArgumentException("Ticket status is required");
        User actor = getUser(email);
        Ticket ticket = getTicket(ticketId);
        boolean admin = actor.getRole() == Role.ADMIN;
        boolean assignedAgent = actor.getRole() == Role.SUPPORT_AGENT
                && ticket.getAssignedTo() != null
                && ticket.getAssignedTo().getId().equals(actor.getId());
        if (!admin && !assignedAgent) throw new AccessDeniedException("You may only update tickets assigned to you");
        if (assignedAgent && status != TicketStatus.IN_PROGRESS && status != TicketStatus.RESOLVED) {
            throw new AccessDeniedException("Support agents may only mark assigned tickets In Progress or Resolved");
        }
        TicketStatus previousStatus = ticket.getStatus();
        ticket.setStatus(status);
        if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else {
            ticket.setResolvedAt(null);
        }
        Ticket saved = ticketRepository.save(ticket);
        recordHistory(saved, actor, "STATUS_CHANGED", previousStatus + " -> " + status);
        return mapToResponse(saved);
    }

    @Transactional
    public TicketResponse assignTicket(Long ticketId, Long userId, String email) {
        User actor = getUser(email);
        if (actor.getRole() != Role.ADMIN) throw new AccessDeniedException("Only administrators can assign tickets");
        Ticket ticket = getTicket(ticketId);
        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (assignee.getRole() != Role.SUPPORT_AGENT) {
            throw new IllegalArgumentException("Tickets can only be assigned to support agents");
        }
        User previousAssignee = ticket.getAssignedTo();
        ticket.setAssignedTo(assignee);
        if (ticket.getStatus() == TicketStatus.NEW || ticket.getStatus() == TicketStatus.REOPENED) {
            ticket.setStatus(TicketStatus.ASSIGNED);
        }
        Ticket saved = ticketRepository.save(ticket);
        String previous = previousAssignee == null ? "Unassigned" : previousAssignee.getEmail();
        recordHistory(saved, actor, "ASSIGNED", previous + " -> " + assignee.getEmail());
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketHistoryResponse> getTicketHistory(Long ticketId, String email) {
        User actor = getUser(email);
        Ticket ticket = getTicket(ticketId);
        boolean owner = ticket.getCreatedBy() != null && ticket.getCreatedBy().getId().equals(actor.getId());
        boolean assignedAgent = ticket.getAssignedTo() != null && ticket.getAssignedTo().getId().equals(actor.getId());
        if (actor.getRole() != Role.ADMIN && !owner && !assignedAgent) {
            throw new AccessDeniedException("You cannot view this ticket history");
        }
        return historyRepository.findByTicketIdOrderByCreatedAtDesc(ticketId).stream()
                .map(item -> new TicketHistoryResponse(item.getId(), item.getAction(), item.getDetails(),
                        item.getChangedBy().getEmail(), item.getCreatedAt()))
                .toList();
    }
}
