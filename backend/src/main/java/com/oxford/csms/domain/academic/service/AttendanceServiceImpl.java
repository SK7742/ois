package com.oxford.csms.domain.academic.service;

import com.oxford.csms.domain.academic.model.AttendanceRecord;
import com.oxford.csms.domain.academic.repository.AttendanceRecordRepository;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.repository.StudentRepository;
import com.oxford.csms.domain.communication.service.WhatsAppNotificationService;
import com.oxford.csms.web.dto.AttendanceDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentRepository studentRepository;
    private final WhatsAppNotificationService whatsAppNotificationService;

    public AttendanceServiceImpl(AttendanceRecordRepository attendanceRecordRepository,
                                 StudentRepository studentRepository,
                                 WhatsAppNotificationService whatsAppNotificationService) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.studentRepository = studentRepository;
        this.whatsAppNotificationService = whatsAppNotificationService;
    }

    @Override
    public List<AttendanceRecord> recordBatchAttendance(AttendanceDtos.BatchRequest request) {
        String grade = request.getGrade();
        String section = request.getSection();
        LocalDate date = request.getDate() != null ? request.getDate() : LocalDate.now();
        String recorder = request.getRecordedBy() != null && !request.getRecordedBy().isBlank() ?
                request.getRecordedBy() : "Class Faculty";

        List<AttendanceRecord> savedRecords = new ArrayList<>();

        if (request.getEntries() != null) {
            for (AttendanceDtos.Entry entry : request.getEntries()) {
                Optional<Student> studentOpt = studentRepository.findById(entry.getStudentId());
                if (studentOpt.isEmpty()) continue;

                Student student = studentOpt.get();
                AttendanceRecord.AttendanceStatus status = entry.getStatus() != null ?
                        entry.getStatus() : AttendanceRecord.AttendanceStatus.PRESENT;

                // Remarks / Reason only apply if student is not present (ABSENT, LATE, or EXCUSED)
                String remarks = (status != AttendanceRecord.AttendanceStatus.PRESENT && entry.getRemarks() != null && !entry.getRemarks().isBlank()) ?
                        entry.getRemarks().trim() : null;

                boolean dispatchWhatsApp = false;
                if (status == AttendanceRecord.AttendanceStatus.ABSENT && Boolean.TRUE.equals(entry.getSendAlert())) {
                    try {
                        whatsAppNotificationService.sendAbsentAlert(student, date.toString());
                        dispatchWhatsApp = true;
                    } catch (Exception e) {
                        // Log and continue
                        System.err.println("Could not dispatch WhatsApp absence alert: " + e.getMessage());
                    }
                }

                Optional<AttendanceRecord> existingOpt = attendanceRecordRepository.findByStudentIdAndAttendanceDate(student.getId(), date);
                AttendanceRecord record;
                if (existingOpt.isPresent()) {
                    record = existingOpt.get();
                    record.setStatus(status);
                    record.setRemarks(remarks);
                    record.setRecordedBy(recorder);
                    if (dispatchWhatsApp) {
                        record.setNotificationDispatched(true);
                    }
                } else {
                    record = new AttendanceRecord(
                            student.getId(),
                            student.getFullName(),
                            student.getRollNo(),
                            student.getAdmissionNo(),
                            grade != null ? grade : student.getCurrentClass(),
                            section != null ? section : student.getSection(),
                            date,
                            status,
                            remarks,
                            recorder,
                            dispatchWhatsApp
                    );
                }

                AttendanceRecord saved = attendanceRecordRepository.save(record);
                savedRecords.add(saved);

                // Update cumulative student attendance rate
                updateStudentCumulativeAttendance(student);
            }
        }

        return savedRecords;
    }

    private void updateStudentCumulativeAttendance(Student student) {
        long totalSessions = attendanceRecordRepository.countByStudentId(student.getId());
        if (totalSessions > 0) {
            long present = attendanceRecordRepository.countByStudentIdAndStatus(student.getId(), AttendanceRecord.AttendanceStatus.PRESENT);
            long late = attendanceRecordRepository.countByStudentIdAndStatus(student.getId(), AttendanceRecord.AttendanceStatus.LATE);
            long excused = attendanceRecordRepository.countByStudentIdAndStatus(student.getId(), AttendanceRecord.AttendanceStatus.EXCUSED);

            double attendedWeight = present + (late * 0.9) + (excused * 1.0);
            double rate = (attendedWeight / totalSessions) * 100.0;
            student.setAttendanceRate(Math.min(100.0, Math.round(rate * 10.0) / 10.0));
            studentRepository.save(student);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getClassAttendance(String grade, String section, LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        return attendanceRecordRepository.findByGradeAndSectionAndAttendanceDateOrderByRollNoAsc(grade, section, queryDate);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDtos.Summary getClassAttendanceSummary(String grade, String section, LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        long totalRecorded = attendanceRecordRepository.countByGradeAndSectionAndAttendanceDate(grade, section, queryDate);
        long present = attendanceRecordRepository.countByGradeAndSectionAndAttendanceDateAndStatus(grade, section, queryDate, AttendanceRecord.AttendanceStatus.PRESENT);
        long absent = attendanceRecordRepository.countByGradeAndSectionAndAttendanceDateAndStatus(grade, section, queryDate, AttendanceRecord.AttendanceStatus.ABSENT);
        long late = attendanceRecordRepository.countByGradeAndSectionAndAttendanceDateAndStatus(grade, section, queryDate, AttendanceRecord.AttendanceStatus.LATE);
        long excused = attendanceRecordRepository.countByGradeAndSectionAndAttendanceDateAndStatus(grade, section, queryDate, AttendanceRecord.AttendanceStatus.EXCUSED);

        double pct = totalRecorded > 0 ? (double) (present + late) / totalRecorded * 100.0 : 0.0;
        pct = Math.round(pct * 10.0) / 10.0;

        return new AttendanceDtos.Summary(grade, section, queryDate, totalRecorded, present, absent, late, excused, pct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getStudentAttendanceHistory(Long studentId) {
        return attendanceRecordRepository.findByStudentIdOrderByAttendanceDateDesc(studentId);
    }
}
