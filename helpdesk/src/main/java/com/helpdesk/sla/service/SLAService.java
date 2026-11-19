package com.helpdesk.sla.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.helpdesk.category.entity.Category;
import com.helpdesk.category.repository.CategoryRepository;
import com.helpdesk.sla.dto.SLARequest;
import com.helpdesk.sla.entity.SLA;
import com.helpdesk.sla.repository.SLARepository;

@Service
public class SLAService {

    private final SLARepository slaRepository;
    private final CategoryRepository categoryRepository;

    public SLAService(
            SLARepository slaRepository,
            CategoryRepository categoryRepository) {

        this.slaRepository = slaRepository;
        this.categoryRepository = categoryRepository;
    }

    public SLA createSLA(SLARequest request) {

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        if (request.getResponseTimeHours() == null || request.getResponseTimeHours() <= 0
                || request.getResolutionTimeHours() == null || request.getResolutionTimeHours() <= 0) {
            throw new IllegalArgumentException("SLA response and resolution hours must be positive");
        }
        SLA sla = slaRepository.findByCategory(category).orElseGet(SLA::new);
        sla.setCategory(category);
        sla.setResponseTimeHours(request.getResponseTimeHours());
        sla.setResolutionTimeHours(request.getResolutionTimeHours());

        return slaRepository.save(sla);
    }

    public List<SLA> getAllSLA() {
        return slaRepository.findAll();
    }
}