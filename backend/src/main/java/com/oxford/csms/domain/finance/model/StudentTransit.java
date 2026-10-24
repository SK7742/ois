package com.oxford.csms.domain.finance.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_transit")
public class StudentTransit extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long studentId;

    @Column(nullable = false)
    private Long routeId;

    @Column(nullable = false)
    private Long stopId;

    private boolean active = true;

    public StudentTransit() {}

    public StudentTransit(Long studentId, Long routeId, Long stopId) {
        this.studentId = studentId;
        this.routeId = routeId;
        this.stopId = stopId;
        this.active = true;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getStopId() {
        return stopId;
    }

    public void setStopId(Long stopId) {
        this.stopId = stopId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
