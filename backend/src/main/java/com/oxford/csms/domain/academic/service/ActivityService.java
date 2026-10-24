package com.oxford.csms.domain.academic.service;

import com.oxford.csms.domain.academic.model.ActivityClub;
import com.oxford.csms.domain.academic.model.ActivityEvent;
import com.oxford.csms.domain.academic.model.ClubEnrollment;

import java.util.List;

public interface ActivityService {
    ActivityClub createClub(ActivityClub club);
    List<ActivityClub> getAllClubs();

    ActivityEvent createEvent(ActivityEvent event);
    List<ActivityEvent> getAllEvents();

    ClubEnrollment enrollStudentInClub(Long clubId, Long studentId, String studentName, String role);
    List<ClubEnrollment> getEnrollmentsByClub(Long clubId);
    List<ClubEnrollment> getEnrollmentsByStudent(Long studentId);
}
