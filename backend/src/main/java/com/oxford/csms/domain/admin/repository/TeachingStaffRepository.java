package com.oxford.csms.domain.admin.repository;

import com.oxford.csms.domain.admin.model.TeachingStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeachingStaffRepository extends JpaRepository<TeachingStaff, Long> {
    List<TeachingStaff> findBySubjectSpecialization(String subjectSpecialization);
    List<TeachingStaff> findByClassTeacherTrue();
}
