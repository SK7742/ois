package com.oxford.csms.domain.admin.repository;

import com.oxford.csms.domain.admin.model.ApplicationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationCommentRepository extends JpaRepository<ApplicationComment, Long> {
    List<ApplicationComment> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);
}
