package com.oxford.csms.domain.academic.repository;

import com.oxford.csms.domain.academic.model.ActivityClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityClubRepository extends JpaRepository<ActivityClub, Long> {
    Optional<ActivityClub> findByClubName(String clubName);
}
