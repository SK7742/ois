package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.admin.model.Employee;
import com.oxford.csms.domain.admin.model.NonTeachingStaff;
import com.oxford.csms.domain.admin.model.TeachingStaff;
import com.oxford.csms.domain.admin.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/teaching")
    public ResponseEntity<ApiResponse<TeachingStaff>> registerTeachingStaff(@RequestBody TeachingStaff staff) {
        return ResponseEntity.ok(ApiResponse.ok("Teaching staff registered", employeeService.registerTeachingStaff(staff)));
    }

    @PostMapping("/non-teaching")
    public ResponseEntity<ApiResponse<NonTeachingStaff>> registerNonTeachingStaff(@RequestBody NonTeachingStaff staff) {
        return ResponseEntity.ok(ApiResponse.ok("Non-teaching staff registered", employeeService.registerNonTeachingStaff(staff)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees() {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getAllEmployees()));
    }

    @GetMapping("/teaching")
    public ResponseEntity<ApiResponse<List<TeachingStaff>>> getAllTeachingStaff() {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getAllTeachingStaff()));
    }

    @GetMapping("/non-teaching")
    public ResponseEntity<ApiResponse<List<NonTeachingStaff>>> getAllNonTeachingStaff() {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getAllNonTeachingStaff()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(employeeService.getEmployeeById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return ResponseEntity.ok(ApiResponse.ok("Employee updated", employeeService.updateEmployee(id, employee)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateEmployee(@PathVariable Long id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee deactivated", null));
    }
}
