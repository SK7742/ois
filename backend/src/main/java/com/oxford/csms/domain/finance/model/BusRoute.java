package com.oxford.csms.domain.finance.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bus_routes")
public class BusRoute extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String routeName; // e.g., "Route 1: Raxaul Station - Harpur Campus"

    @Column(nullable = false)
    private String startPoint;

    @Column(nullable = false)
    private String endPoint;

    private Long vehicleId;

    private Long driverId;

    @Column(nullable = false)
    private BigDecimal monthlyFare = BigDecimal.ZERO;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    @OrderBy("sequenceOrder ASC")
    private List<RouteStop> stops = new ArrayList<>();

    private boolean active = true;

    public BusRoute() {}

    public BusRoute(String routeName, String startPoint, String endPoint, Long vehicleId, Long driverId, BigDecimal monthlyFare) {
        this.routeName = routeName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.monthlyFare = monthlyFare;
        this.active = true;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public BigDecimal getMonthlyFare() {
        return monthlyFare;
    }

    public void setMonthlyFare(BigDecimal monthlyFare) {
        this.monthlyFare = monthlyFare;
    }

    public List<RouteStop> getStops() {
        return stops;
    }

    public void setStops(List<RouteStop> stops) {
        this.stops = stops;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
