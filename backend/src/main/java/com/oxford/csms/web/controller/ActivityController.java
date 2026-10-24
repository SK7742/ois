package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.academic.model.ActivityClub;
import com.oxford.csms.domain.academic.model.ActivityEvent;
import com.oxford.csms.domain.academic.model.ClubEnrollment;
import com.oxford.csms.domain.academic.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academic/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/clubs")
    public ResponseEntity<ApiResponse<ActivityClub>> createClub(@RequestBody ActivityClub club) {
        return ResponseEntity.ok(ApiResponse.ok("Club created", activityService.createClub(club)));
    }

    @GetMapping("/clubs")
    public ResponseEntity<ApiResponse<List<ActivityClub>>> getAllClubs() {
        return ResponseEntity.ok(ApiResponse.ok(activityService.getAllClubs()));
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<ActivityEvent>> createEvent(@RequestBody ActivityEvent event) {
        return ResponseEntity.ok(ApiResponse.ok("Event scheduled", activityService.createEvent(event)));
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<ActivityEvent>>> getAllEvents() {
        return ResponseEntity.ok(ApiResponse.ok(activityService.getAllEvents()));
    }

    @PostMapping("/clubs/enroll")
    public ResponseEntity<ApiResponse<ClubEnrollment>> enrollStudent(@RequestBody Map<String, Object> payload) {
        Long clubId = Long.parseLong(payload.get("clubId").toString());
        Long studentId = Long.parseLong(payload.get("studentId").toString());
        String studentName = (String) payload.get("studentName");
        String role = (String) payload.get("role");
        return ResponseEntity.ok(ApiResponse.ok("Enrolled into club",
                activityService.enrollStudentInClub(clubId, studentId, studentName, role)));
    }

    @GetMapping("/clubs/{clubId}/enrollments")
    public ResponseEntity<ApiResponse<List<ClubEnrollment>>> getClubEnrollments(@PathVariable Long clubId) {
        return ResponseEntity.ok(ApiResponse.ok(activityService.getEnrollmentsByClub(clubId)));
    }

    @GetMapping("/students/{studentId}/enrollments")
    public ResponseEntity<ApiResponse<List<ClubEnrollment>>> getStudentEnrollments(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(activityService.getEnrollmentsByStudent(studentId)));
    }
}
