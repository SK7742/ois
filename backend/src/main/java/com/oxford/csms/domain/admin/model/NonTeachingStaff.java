package com.oxford.csms.domain.admin.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "non_teaching_staff")
public class NonTeachingStaff extends Employee {

    @Enumerated(EnumType.STRING)
    private OperationalDomain operationalDomain;

    private String shiftTiming;

    public enum OperationalDomain {
        ADMINISTRATION,
        TRANSPORTATION,
        LAB_SUPPORT,
        SECURITY,
        MAINTENANCE,
        ACCOUNTING
    }

    public NonTeachingStaff() {}

    public NonTeachingStaff(String employeeCode, String firstName, String lastName, String email,
                            String phone, String department, String designation,
                            String qualification, LocalDate joiningDate,
                            BigDecimal basicSalary, BigDecimal allowances,
                            OperationalDomain operationalDomain, String shiftTiming) {
        super(employeeCode, firstName, lastName, email, phone, department, designation, qualification, joiningDate, basicSalary, allowances);
        this.operationalDomain = operationalDomain;
        this.shiftTiming = shiftTiming;
    }

    @Override
    public String getStaffCategory() {
        return "NON_TEACHING";
    }

    public OperationalDomain getOperationalDomain() {
        return operationalDomain;
    }

    public void setOperationalDomain(OperationalDomain operationalDomain) {
        this.operationalDomain = operationalDomain;
    }

    public String getShiftTiming() {
        return shiftTiming;
    }

    public void setShiftTiming(String shiftTiming) {
        this.shiftTiming = shiftTiming;
    }
}
