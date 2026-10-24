package com.oxford.csms.web.dto;

import com.oxford.csms.domain.academic.model.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;

public class AttendanceDtos {

    public static class Entry {
        private Long studentId;
        private AttendanceRecord.AttendanceStatus status;
        private String remarks;
        private Boolean sendAlert = true;

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public AttendanceRecord.AttendanceStatus getStatus() { return status; }
        public void setStatus(AttendanceRecord.AttendanceStatus status) { this.status = status; }

        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }

        public Boolean getSendAlert() { return sendAlert != null ? sendAlert : true; }
        public void setSendAlert(Boolean sendAlert) { this.sendAlert = sendAlert; }
    }

    public static class BatchRequest {
        private String grade;
        private String section;
        private LocalDate date;
        private String recordedBy;
        private List<Entry> entries;

        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }

        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }

        public LocalDate getDate() { return date != null ? date : LocalDate.now(); }
        public void setDate(LocalDate date) { this.date = date; }

        public String getRecordedBy() { return recordedBy; }
        public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

        public List<Entry> getEntries() { return entries; }
        public void setEntries(List<Entry> entries) { this.entries = entries; }
    }

    public static class Summary {
        private String grade;
        private String section;
        private LocalDate date;
        private long totalStudents;
        private long presentCount;
        private long absentCount;
        private long lateCount;
        private long excusedCount;
        private double attendancePercentage;

        public Summary() {}

        public Summary(String grade, String section, LocalDate date, long totalStudents,
                       long presentCount, long absentCount, long lateCount, long excusedCount,
                       double attendancePercentage) {
            this.grade = grade;
            this.section = section;
            this.date = date;
            this.totalStudents = totalStudents;
            this.presentCount = presentCount;
            this.absentCount = absentCount;
            this.lateCount = lateCount;
            this.excusedCount = excusedCount;
            this.attendancePercentage = attendancePercentage;
        }

        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }

        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

        public long getTotalRecorded() { return totalStudents; }
        public void setTotalRecorded(long totalRecorded) { this.totalStudents = totalRecorded; }

        public long getPresentCount() { return presentCount; }
        public void setPresentCount(long presentCount) { this.presentCount = presentCount; }

        public long getAbsentCount() { return absentCount; }
        public void setAbsentCount(long absentCount) { this.absentCount = absentCount; }

        public long getLateCount() { return lateCount; }
        public void setLateCount(long lateCount) { this.lateCount = lateCount; }

        public long getExcusedCount() { return excusedCount; }
        public void setExcusedCount(long excusedCount) { this.excusedCount = excusedCount; }

        public double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
}
