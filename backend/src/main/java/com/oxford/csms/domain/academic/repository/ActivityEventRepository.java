package com.oxford.csms.domain.academic.repository;

import com.oxford.csms.domain.academic.model.ActivityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityEventRepository extends JpaRepository<ActivityEvent, Long> {
    List<ActivityEvent> findByStatus(String status);
}
