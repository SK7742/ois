package com.oxford.csms.domain.academic.service;

import com.oxford.csms.domain.academic.model.AttendanceRecord;
import com.oxford.csms.web.dto.AttendanceDtos;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    List<AttendanceRecord> recordBatchAttendance(AttendanceDtos.BatchRequest request);

    List<AttendanceRecord> getClassAttendance(String grade, String section, LocalDate date);

    AttendanceDtos.Summary getClassAttendanceSummary(String grade, String section, LocalDate date);

    List<AttendanceRecord> getStudentAttendanceHistory(Long studentId);
}
