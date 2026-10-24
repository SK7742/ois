package com.oxford.csms.domain.finance.service;

import com.oxford.csms.domain.finance.model.BusRoute;
import com.oxford.csms.domain.finance.model.Driver;
import com.oxford.csms.domain.finance.model.StudentTransit;
import com.oxford.csms.domain.finance.model.Vehicle;

import java.util.List;

public interface TransportationService {
    Vehicle registerVehicle(Vehicle vehicle);
    List<Vehicle> getAllVehicles();

    Driver registerDriver(Driver driver);
    List<Driver> getAllDrivers();

    BusRoute createRoute(BusRoute route);
    List<BusRoute> getAllRoutes();
    BusRoute getRouteById(Long id);

    StudentTransit assignStudentToTransit(Long studentId, Long routeId, Long stopId);
    List<StudentTransit> getAllTransits();
    StudentTransit getTransitForStudent(Long studentId);
}
