package com.oxford.csms.domain.finance.repository;

import com.oxford.csms.domain.finance.model.StudentTransit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentTransitRepository extends JpaRepository<StudentTransit, Long> {
    Optional<StudentTransit> findByStudentId(Long studentId);
    long countByRouteId(Long routeId);
}
