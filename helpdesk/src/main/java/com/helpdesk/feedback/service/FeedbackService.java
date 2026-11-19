package com.helpdesk.feedback.service;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.helpdesk.common.enums.TicketStatus;
import com.helpdesk.feedback.dto.FeedbackRequest;
import com.helpdesk.feedback.dto.FeedbackResponse;
import com.helpdesk.feedback.entity.Feedback;
import com.helpdesk.feedback.repository.FeedbackRepository;
import com.helpdesk.ticket.entity.Ticket;
import com.helpdesk.ticket.repository.TicketRepository;
import com.helpdesk.user.entity.User;
import com.helpdesk.user.repository.UserRepository;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId());
        response.setRating(feedback.getRating());
        response.setComment(feedback.getComment());
        return response;
    }

    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest request, String email) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Feedback rating must be between 1 and 5");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (!ticket.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only submit feedback for your own tickets");
        }
        if (ticket.getStatus() != TicketStatus.RESOLVED && ticket.getStatus() != TicketStatus.CLOSED) {
            throw new IllegalArgumentException("Feedback can be submitted after a ticket is resolved");
        }
        if (feedbackRepository.existsByTicketId(ticket.getId())) {
            throw new IllegalArgumentException("Feedback has already been submitted for this ticket");
        }
        Feedback feedback = new Feedback();
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setTicket(ticket);
        return mapToResponse(feedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAllFeedback() {
        return feedbackRepository.findAll().stream().map(this::mapToResponse).toList();
    }
}
