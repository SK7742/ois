package com.oxford.csms.domain.academic.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "club_enrollments")
public class ClubEnrollment extends BaseEntity {

    @Column(nullable = false)
    private Long clubId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    private String roleInClub = "Member"; // Member, President, Secretary

    private LocalDate enrollmentDate = LocalDate.now();

    public ClubEnrollment() {}

    public ClubEnrollment(Long clubId, Long studentId, String studentName, String roleInClub) {
        this.clubId = clubId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.roleInClub = roleInClub != null ? roleInClub : "Member";
        this.enrollmentDate = LocalDate.now();
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRoleInClub() {
        return roleInClub;
    }

    public void setRoleInClub(String roleInClub) {
        this.roleInClub = roleInClub;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}
