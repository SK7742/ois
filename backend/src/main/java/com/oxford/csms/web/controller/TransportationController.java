package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.finance.model.BusRoute;
import com.oxford.csms.domain.finance.model.Driver;
import com.oxford.csms.domain.finance.model.StudentTransit;
import com.oxford.csms.domain.finance.model.Vehicle;
import com.oxford.csms.domain.finance.service.TransportationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance/transportation")
public class TransportationController {

    private final TransportationService transportationService;

    public TransportationController(TransportationService transportationService) {
        this.transportationService = transportationService;
    }

    @PostMapping("/vehicles")
    public ResponseEntity<ApiResponse<Vehicle>> registerVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(ApiResponse.ok("Vehicle registered", transportationService.registerVehicle(vehicle)));
    }

    @GetMapping("/vehicles")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getAllVehicles() {
        return ResponseEntity.ok(ApiResponse.ok(transportationService.getAllVehicles()));
    }

    @PostMapping("/drivers")
    public ResponseEntity<ApiResponse<Driver>> registerDriver(@RequestBody Driver driver) {
        return ResponseEntity.ok(ApiResponse.ok("Driver registered", transportationService.registerDriver(driver)));
    }

    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers() {
        return ResponseEntity.ok(ApiResponse.ok(transportationService.getAllDrivers()));
    }

    @PostMapping("/routes")
    public ResponseEntity<ApiResponse<BusRoute>> createRoute(@RequestBody BusRoute route) {
        return ResponseEntity.ok(ApiResponse.ok("Bus route mapped", transportationService.createRoute(route)));
    }

    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<List<BusRoute>>> getAllRoutes() {
        return ResponseEntity.ok(ApiResponse.ok(transportationService.getAllRoutes()));
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<ApiResponse<BusRoute>> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(transportationService.getRouteById(id)));
    }

    @PostMapping("/transit/assign")
    public ResponseEntity<ApiResponse<StudentTransit>> assignStudent(@RequestBody Map<String, Long> payload) {
        Long studentId = payload.get("studentId");
        Long routeId = payload.get("routeId");
        Long stopId = payload.get("stopId");
        return ResponseEntity.ok(ApiResponse.ok("Student assigned to transit route",
                transportationService.assignStudentToTransit(studentId, routeId, stopId)));
    }

    @GetMapping("/transit")
    public ResponseEntity<ApiResponse<List<StudentTransit>>> getAllTransits() {
        return ResponseEntity.ok(ApiResponse.ok(transportationService.getAllTransits()));
    }

    @GetMapping("/transit/student/{studentId}")
    public ResponseEntity<ApiResponse<StudentTransit>> getStudentTransit(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(transportationService.getTransitForStudent(studentId)));
    }
}
