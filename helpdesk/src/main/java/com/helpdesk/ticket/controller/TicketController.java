package com.helpdesk.ticket.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.helpdesk.ticket.dto.AssignTicketRequest;
import com.helpdesk.ticket.dto.CreateTicketRequest;
import com.helpdesk.ticket.dto.TicketResponse;
import com.helpdesk.ticket.dto.TicketHistoryResponse;
import com.helpdesk.ticket.dto.UpdateTicketStatusRequest;
import com.helpdesk.ticket.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;
    public TicketController(TicketService ticketService) { this.ticketService = ticketService; }

    @PostMapping
    public TicketResponse createTicket(@Valid @RequestBody CreateTicketRequest request, Principal principal) {
        return ticketService.createTicket(request, principal.getName());
    }
    @GetMapping
    public List<TicketResponse> getAllTickets(Principal principal) {
        return ticketService.getAllTickets(principal.getName());
    }
    @GetMapping("/my")
    public List<TicketResponse> getMyTickets(Principal principal) {
        return ticketService.getMyTickets(principal.getName());
    }
    @GetMapping("/{id}/history")
    public List<TicketHistoryResponse> getTicketHistory(@PathVariable Long id, Principal principal) {
        return ticketService.getTicketHistory(id, principal.getName());
    }

    @PutMapping("/{id}/status")
    public TicketResponse updateStatus(@PathVariable Long id, @RequestBody UpdateTicketStatusRequest request, Principal principal) {
        return ticketService.updateStatus(id, request.getStatus(), principal.getName());
    }
    @PutMapping("/{id}/assign")
    public TicketResponse assignTicket(@PathVariable Long id, @RequestBody AssignTicketRequest request, Principal principal) {
        return ticketService.assignTicket(id, request.getUserId(), principal.getName());
    }
}
