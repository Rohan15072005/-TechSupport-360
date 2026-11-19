package com.helpdesk.dashboard.dto;

public class DashboardResponse {
    private Long totalTickets;
    private Long openTickets;
    private Long closedTickets;
    private Long resolvedTickets;
    private Long slaBreachedTickets;

    public DashboardResponse(Long totalTickets, Long openTickets, Long closedTickets, Long resolvedTickets, Long slaBreachedTickets) {
        this.totalTickets = totalTickets;
        this.openTickets = openTickets;
        this.closedTickets = closedTickets;
        this.resolvedTickets = resolvedTickets;
        this.slaBreachedTickets = slaBreachedTickets;
    }
    public Long getTotalTickets() { return totalTickets; }
    public Long getOpenTickets() { return openTickets; }
    public Long getClosedTickets() { return closedTickets; }
    public Long getResolvedTickets() { return resolvedTickets; }
    public Long getSlaBreachedTickets() { return slaBreachedTickets; }
}
