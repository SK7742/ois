package com.oxford.csms.domain.academic.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "attendance_records", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "attendance_date"})
})
public class AttendanceRecord extends BaseEntity {

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "roll_no")
    private Integer rollNo;

    @Column(name = "admission_no", nullable = false)
    private String admissionNo;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String section;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(length = 500)
    private String remarks;

    @Column(name = "recorded_by", nullable = false)
    private String recordedBy;

    @Column(name = "notification_dispatched", nullable = false)
    private boolean notificationDispatched = false;

    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        LATE,
        EXCUSED
    }

    public AttendanceRecord() {}

    public AttendanceRecord(Long studentId, String studentName, Integer rollNo, String admissionNo,
                            String grade, String section, LocalDate attendanceDate,
                            AttendanceStatus status, String remarks, String recordedBy, boolean notificationDispatched) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.admissionNo = admissionNo;
        this.grade = grade;
        this.section = section;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.remarks = remarks;
        this.recordedBy = recordedBy;
        this.notificationDispatched = notificationDispatched;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Integer getRollNo() { return rollNo; }
    public void setRollNo(Integer rollNo) { this.rollNo = rollNo; }

    public String getAdmissionNo() { return admissionNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public boolean isNotificationDispatched() { return notificationDispatched; }
    public void setNotificationDispatched(boolean notificationDispatched) { this.notificationDispatched = notificationDispatched; }
}
