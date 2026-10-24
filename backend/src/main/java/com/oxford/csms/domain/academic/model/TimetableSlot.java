package com.oxford.csms.domain.academic.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "timetable_slots", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"grade", "section", "dayOfWeek", "periodNumber"}),
    @UniqueConstraint(columnNames = {"teacherId", "dayOfWeek", "periodNumber"})
})
public class TimetableSlot extends BaseEntity {

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private String dayOfWeek; // "MONDAY", "TUESDAY", ...

    @Column(nullable = false)
    private Integer periodNumber; // 1 to 7

    private String periodTime; // "08:30 - 09:15"

    @Column(nullable = false)
    private String subject;

    private Long teacherId;

    private String teacherName;

    private String roomNumber;

    public TimetableSlot() {}

    public TimetableSlot(String grade, String section, String dayOfWeek, Integer periodNumber,
                         String periodTime, String subject, Long teacherId, String teacherName, String roomNumber) {
        this.grade = grade;
        this.section = section;
        this.dayOfWeek = dayOfWeek;
        this.periodNumber = periodNumber;
        this.periodTime = periodTime;
        this.subject = subject;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.roomNumber = roomNumber;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(Integer periodNumber) {
        this.periodNumber = periodNumber;
    }

    public String getPeriodTime() {
        return periodTime;
    }

    public void setPeriodTime(String periodTime) {
        this.periodTime = periodTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
