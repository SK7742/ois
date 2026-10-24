package com.oxford.csms.domain.admin.repository;

import com.oxford.csms.domain.admin.model.WorkItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

    List<WorkItem> findByAssignedToUsernameIgnoreCaseOrderByCreatedAtDesc(String username);

    List<WorkItem> findAllByOrderByCreatedAtDesc();
}
