package com.oxford.csms.domain.admin.repository;

import com.oxford.csms.domain.admin.model.AdmissionInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionInquiryRepository extends JpaRepository<AdmissionInquiry, Long> {
    List<AdmissionInquiry> findByStatus(AdmissionInquiry.InquiryStatus status);
    List<AdmissionInquiry> findByTargetGrade(String targetGrade);
}
