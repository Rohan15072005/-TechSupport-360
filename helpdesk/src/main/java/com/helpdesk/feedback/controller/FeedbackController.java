package com.helpdesk.feedback.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.helpdesk.feedback.dto.FeedbackRequest;
import com.helpdesk.feedback.dto.FeedbackResponse;
import com.helpdesk.feedback.service.FeedbackService;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;
    public FeedbackController(FeedbackService feedbackService) { this.feedbackService = feedbackService; }
    @PostMapping
    public FeedbackResponse createFeedback(@Valid @RequestBody FeedbackRequest request, Principal principal) {
        return feedbackService.createFeedback(request, principal.getName());
    }
    @GetMapping
    public List<FeedbackResponse> getAllFeedback() { return feedbackService.getAllFeedback(); }
}
