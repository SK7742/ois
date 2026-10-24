package com.oxford.csms.domain.finance.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String vehicleNumber; // e.g. "BR-05-PA-1234"

    @Column(nullable = false)
    private String model; // e.g. "Tata Marcopolo 40-Seater"

    @Column(nullable = false)
    private Integer seatingCapacity;

    private String insurancePolicyNo;

    private String fitnessCertExpiry;

    private boolean active = true;

    public Vehicle() {}

    public Vehicle(String vehicleNumber, String model, Integer seatingCapacity) {
        this.vehicleNumber = vehicleNumber;
        this.model = model;
        this.seatingCapacity = seatingCapacity;
        this.active = true;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getInsurancePolicyNo() {
        return insurancePolicyNo;
    }

    public void setInsurancePolicyNo(String insurancePolicyNo) {
        this.insurancePolicyNo = insurancePolicyNo;
    }

    public String getFitnessCertExpiry() {
        return fitnessCertExpiry;
    }

    public void setFitnessCertExpiry(String fitnessCertExpiry) {
        this.fitnessCertExpiry = fitnessCertExpiry;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
