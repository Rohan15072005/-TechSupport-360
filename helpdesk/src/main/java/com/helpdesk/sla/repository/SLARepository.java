package com.helpdesk.sla.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.helpdesk.sla.entity.SLA;
import com.helpdesk.category.entity.Category;

public interface SLARepository extends JpaRepository<SLA, Long> {
    Optional<SLA> findByCategory(Category category);
}
