package com.oxford.csms.domain.finance.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "route_stops")
public class RouteStop extends BaseEntity {

    @Column(name = "route_id")
    private Long routeId;

    @Column(nullable = false)
    private String stopName;

    private String morningPickupTime; // "07:15 AM"

    private String eveningDropTime;   // "03:45 PM"

    private Integer sequenceOrder;

    public RouteStop() {}

    public RouteStop(String stopName, String morningPickupTime, String eveningDropTime, Integer sequenceOrder) {
        this.stopName = stopName;
        this.morningPickupTime = morningPickupTime;
        this.eveningDropTime = eveningDropTime;
        this.sequenceOrder = sequenceOrder;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getMorningPickupTime() {
        return morningPickupTime;
    }

    public void setMorningPickupTime(String morningPickupTime) {
        this.morningPickupTime = morningPickupTime;
    }

    public String getEveningDropTime() {
        return eveningDropTime;
    }

    public void setEveningDropTime(String eveningDropTime) {
        this.eveningDropTime = eveningDropTime;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }
}
