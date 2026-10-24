package com.oxford.csms.domain.academic.repository;

import com.oxford.csms.domain.academic.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByGradeAndSectionAndAttendanceDateOrderByRollNoAsc(String grade, String section, LocalDate attendanceDate);

    Optional<AttendanceRecord> findByStudentIdAndAttendanceDate(Long studentId, LocalDate attendanceDate);

    List<AttendanceRecord> findByStudentIdOrderByAttendanceDateDesc(Long studentId);

    long countByGradeAndSectionAndAttendanceDate(String grade, String section, LocalDate attendanceDate);

    long countByGradeAndSectionAndAttendanceDateAndStatus(String grade, String section, LocalDate attendanceDate, AttendanceRecord.AttendanceStatus status);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, AttendanceRecord.AttendanceStatus status);
}
