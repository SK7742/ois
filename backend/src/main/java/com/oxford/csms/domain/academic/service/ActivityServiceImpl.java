package com.oxford.csms.domain.academic.service;

import com.oxford.csms.common.BusinessValidationException;
import com.oxford.csms.domain.academic.model.ActivityClub;
import com.oxford.csms.domain.academic.model.ActivityEvent;
import com.oxford.csms.domain.academic.model.ClubEnrollment;
import com.oxford.csms.domain.academic.repository.ActivityClubRepository;
import com.oxford.csms.domain.academic.repository.ActivityEventRepository;
import com.oxford.csms.domain.academic.repository.ClubEnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final ActivityClubRepository clubRepository;
    private final ActivityEventRepository eventRepository;
    private final ClubEnrollmentRepository enrollmentRepository;

    public ActivityServiceImpl(ActivityClubRepository clubRepository,
                               ActivityEventRepository eventRepository,
                               ClubEnrollmentRepository enrollmentRepository) {
        this.clubRepository = clubRepository;
        this.eventRepository = eventRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public ActivityClub createClub(ActivityClub club) {
        return clubRepository.save(club);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityClub> getAllClubs() {
        return clubRepository.findAll();
    }

    @Override
    public ActivityEvent createEvent(ActivityEvent event) {
        return eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityEvent> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public ClubEnrollment enrollStudentInClub(Long clubId, Long studentId, String studentName, String role) {
        if (enrollmentRepository.existsByClubIdAndStudentId(clubId, studentId)) {
            throw new BusinessValidationException("Student is already enrolled in this club.");
        }
        ClubEnrollment enrollment = new ClubEnrollment(clubId, studentId, studentName, role);
        return enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubEnrollment> getEnrollmentsByClub(Long clubId) {
        return enrollmentRepository.findByClubId(clubId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubEnrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
}
