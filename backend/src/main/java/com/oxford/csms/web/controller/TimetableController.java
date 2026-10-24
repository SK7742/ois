package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.academic.model.TimetableSlot;
import com.oxford.csms.domain.academic.service.TimetableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academic/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping("/class")
    public ResponseEntity<ApiResponse<List<TimetableSlot>>> getTimetableForClass(
            @RequestParam String grade,
            @RequestParam String section) {
        return ResponseEntity.ok(ApiResponse.ok(timetableService.getTimetableForClass(grade, section)));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<TimetableSlot>>> getTimetableForTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(ApiResponse.ok(timetableService.getTimetableForTeacher(teacherId)));
    }

    @PostMapping("/auto-generate")
    public ResponseEntity<ApiResponse<List<TimetableSlot>>> autoGenerateTimetable(@RequestBody Map<String, String> payload) {
        String grade = payload.get("grade");
        String section = payload.get("section");
        String room = payload.get("roomNumber");
        List<TimetableSlot> generated = timetableService.generateTimetableForClass(grade, section, room);
        return ResponseEntity.ok(ApiResponse.ok("Conflict-free timetable generated algorithmically", generated));
    }

    @PostMapping("/slots")
    public ResponseEntity<ApiResponse<TimetableSlot>> saveSlot(@RequestBody TimetableSlot slot) {
        return ResponseEntity.ok(ApiResponse.ok("Timetable slot saved", timetableService.saveSlot(slot)));
    }

    @DeleteMapping("/class")
    public ResponseEntity<ApiResponse<Void>> clearClassSchedule(
            @RequestParam String grade,
            @RequestParam String section) {
        timetableService.clearTimetableForClass(grade, section);
        return ResponseEntity.ok(ApiResponse.ok("Timetable cleared for " + grade + " " + section, null));
    }
}
