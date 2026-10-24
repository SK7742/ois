package com.oxford.csms.domain.admin.service;

import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.admin.model.Employee;
import com.oxford.csms.domain.admin.model.NonTeachingStaff;
import com.oxford.csms.domain.admin.model.TeachingStaff;
import com.oxford.csms.domain.admin.repository.EmployeeRepository;
import com.oxford.csms.domain.admin.repository.NonTeachingStaffRepository;
import com.oxford.csms.domain.admin.repository.TeachingStaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TeachingStaffRepository teachingStaffRepository;
    private final NonTeachingStaffRepository nonTeachingStaffRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               TeachingStaffRepository teachingStaffRepository,
                               NonTeachingStaffRepository nonTeachingStaffRepository) {
        this.employeeRepository = employeeRepository;
        this.teachingStaffRepository = teachingStaffRepository;
        this.nonTeachingStaffRepository = nonTeachingStaffRepository;
    }

    @Override
    public TeachingStaff registerTeachingStaff(TeachingStaff staff) {
        if (staff.getEmployeeCode() == null || staff.getEmployeeCode().isBlank()) {
            long count = employeeRepository.count() + 1;
            staff.setEmployeeCode(String.format("EMP-T-%03d", count));
        }
        return teachingStaffRepository.save(staff);
    }

    @Override
    public NonTeachingStaff registerNonTeachingStaff(NonTeachingStaff staff) {
        if (staff.getEmployeeCode() == null || staff.getEmployeeCode().isBlank()) {
            long count = employeeRepository.count() + 1;
            staff.setEmployeeCode(String.format("EMP-NT-%03d", count));
        }
        return nonTeachingStaffRepository.save(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeByCode(String code) {
        return employeeRepository.findByEmployeeCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with code: " + code));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeachingStaff> getAllTeachingStaff() {
        return teachingStaffRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NonTeachingStaff> getAllNonTeachingStaff() {
        return nonTeachingStaffRepository.findAll();
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = getEmployeeById(id);
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setEmail(employee.getEmail());
        existing.setPhone(employee.getPhone());
        existing.setDepartment(employee.getDepartment());
        existing.setDesignation(employee.getDesignation());
        existing.setQualification(employee.getQualification());
        existing.setBasicSalary(employee.getBasicSalary());
        existing.setAllowances(employee.getAllowances());
        existing.setActive(employee.isActive());
        return employeeRepository.save(existing);
    }

    @Override
    public void deactivateEmployee(Long id) {
        Employee e = getEmployeeById(id);
        e.setActive(false);
        employeeRepository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalActiveStaff() {
        return employeeRepository.countByActiveTrue();
    }
}
