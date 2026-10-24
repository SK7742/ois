package com.oxford.csms.domain.academic.repository;

import com.oxford.csms.domain.academic.model.ClubEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubEnrollmentRepository extends JpaRepository<ClubEnrollment, Long> {
    List<ClubEnrollment> findByClubId(Long clubId);
    List<ClubEnrollment> findByStudentId(Long studentId);
    boolean existsByClubIdAndStudentId(Long clubId, Long studentId);
}
