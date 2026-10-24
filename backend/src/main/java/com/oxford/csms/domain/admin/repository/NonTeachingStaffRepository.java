package com.oxford.csms.domain.admin.repository;

import com.oxford.csms.domain.admin.model.NonTeachingStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NonTeachingStaffRepository extends JpaRepository<NonTeachingStaff, Long> {
    List<NonTeachingStaff> findByOperationalDomain(NonTeachingStaff.OperationalDomain operationalDomain);
}
