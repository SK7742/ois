package com.oxford.csms.domain.admin.repository;

import com.oxford.csms.domain.admin.model.InquiryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryCommentRepository extends JpaRepository<InquiryComment, Long> {
    List<InquiryComment> findByInquiryIdOrderByCreatedAtDesc(Long inquiryId);
}
