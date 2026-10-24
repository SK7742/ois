package com.oxford.csms.domain.admin.repository;

import com.oxford.csms.domain.admin.model.AdmissionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, Long> {
    Optional<AdmissionApplication> findByApplicationNumber(String applicationNumber);
    List<AdmissionApplication> findByStatus(AdmissionApplication.ApplicationStatus status);
    List<AdmissionApplication> findByTargetGrade(String targetGrade);
    long countByStatus(AdmissionApplication.ApplicationStatus status);
}
