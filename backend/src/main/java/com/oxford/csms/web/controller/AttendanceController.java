package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.academic.model.AttendanceRecord;
import com.oxford.csms.domain.academic.service.AttendanceService;
import com.oxford.csms.web.dto.AttendanceDtos;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/academic/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> recordBatchAttendance(
            @RequestBody AttendanceDtos.BatchRequest request) {
        List<AttendanceRecord> saved = attendanceService.recordBatchAttendance(request);
        return ResponseEntity.ok(ApiResponse.ok("Classroom attendance successfully recorded", saved));
    }

    @GetMapping("/class")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getClassAttendance(
            @RequestParam String grade,
            @RequestParam String section,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceRecord> list = attendanceService.getClassAttendance(grade, section, date);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AttendanceDtos.Summary>> getClassAttendanceSummary(
            @RequestParam String grade,
            @RequestParam String section,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AttendanceDtos.Summary summary = attendanceService.getClassAttendanceSummary(grade, section, date);
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getStudentAttendanceHistory(
            @PathVariable Long studentId) {
        List<AttendanceRecord> history = attendanceService.getStudentAttendanceHistory(studentId);
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
