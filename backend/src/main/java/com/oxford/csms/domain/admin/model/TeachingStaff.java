package com.oxford.csms.domain.admin.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "teaching_staff")
public class TeachingStaff extends Employee {

    private String subjectSpecialization;
    private Integer weeklyTargetPeriods = 24;
    private boolean classTeacher = false;
    private String assignedClass;

    public TeachingStaff() {}

    public TeachingStaff(String employeeCode, String firstName, String lastName, String email,
                         String phone, String department, String designation,
                         String qualification, LocalDate joiningDate,
                         BigDecimal basicSalary, BigDecimal allowances,
                         String subjectSpecialization, Integer weeklyTargetPeriods,
                         boolean classTeacher, String assignedClass) {
        super(employeeCode, firstName, lastName, email, phone, department, designation, qualification, joiningDate, basicSalary, allowances);
        this.subjectSpecialization = subjectSpecialization;
        this.weeklyTargetPeriods = weeklyTargetPeriods;
        this.classTeacher = classTeacher;
        this.assignedClass = assignedClass;
    }

    @Override
    public String getStaffCategory() {
        return "TEACHING";
    }

    public String getSubjectSpecialization() {
        return subjectSpecialization;
    }

    public void setSubjectSpecialization(String subjectSpecialization) {
        this.subjectSpecialization = subjectSpecialization;
    }

    public Integer getWeeklyTargetPeriods() {
        return weeklyTargetPeriods;
    }

    public void setWeeklyTargetPeriods(Integer weeklyTargetPeriods) {
        this.weeklyTargetPeriods = weeklyTargetPeriods;
    }

    public boolean isClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(boolean classTeacher) {
        this.classTeacher = classTeacher;
    }

    public String getAssignedClass() {
        return assignedClass;
    }

    public void setAssignedClass(String assignedClass) {
        this.assignedClass = assignedClass;
    }
}
