package com.oxford.csms.domain.admin.service;

import com.oxford.csms.domain.admin.model.Employee;
import com.oxford.csms.domain.admin.model.NonTeachingStaff;
import com.oxford.csms.domain.admin.model.TeachingStaff;

import java.util.List;

public interface EmployeeService {
    TeachingStaff registerTeachingStaff(TeachingStaff staff);
    NonTeachingStaff registerNonTeachingStaff(NonTeachingStaff staff);
    Employee getEmployeeById(Long id);
    Employee getEmployeeByCode(String code);
    List<Employee> getAllEmployees();
    List<TeachingStaff> getAllTeachingStaff();
    List<NonTeachingStaff> getAllNonTeachingStaff();
    Employee updateEmployee(Long id, Employee employee);
    void deactivateEmployee(Long id);
    long getTotalActiveStaff();
}
