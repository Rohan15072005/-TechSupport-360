package com.helpdesk.ticket.dto;

import java.time.LocalDateTime;

public class TicketHistoryResponse {
    private Long id;
    private String action;
    private String details;
    private String changedBy;
    private LocalDateTime createdAt;

    public TicketHistoryResponse(Long id, String action, String details, String changedBy, LocalDateTime createdAt) {
        this.id = id;
        this.action = action;
        this.details = details;
        this.changedBy = changedBy;
        this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public String getChangedBy() { return changedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
