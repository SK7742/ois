package com.oxford.csms.domain.academic.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity_clubs")
public class ActivityClub extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String clubName;

    @Column(nullable = false)
    private String category; // SPORTS, ROBOTICS, SCIENCE, CULTURAL, DEBATE

    private String inchargeTeacherName;

    private String meetingSchedule; // "Every Wednesday & Friday 03:30 PM"

    private Integer capacity = 40;

    private String description;

    public ActivityClub() {}

    public ActivityClub(String clubName, String category, String inchargeTeacherName, String meetingSchedule, Integer capacity, String description) {
        this.clubName = clubName;
        this.category = category;
        this.inchargeTeacherName = inchargeTeacherName;
        this.meetingSchedule = meetingSchedule;
        this.capacity = capacity;
        this.description = description;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInchargeTeacherName() {
        return inchargeTeacherName;
    }

    public void setInchargeTeacherName(String inchargeTeacherName) {
        this.inchargeTeacherName = inchargeTeacherName;
    }

    public String getMeetingSchedule() {
        return meetingSchedule;
    }

    public void setMeetingSchedule(String meetingSchedule) {
        this.meetingSchedule = meetingSchedule;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
