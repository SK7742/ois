package com.oxford.csms.domain.finance.service;

import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.repository.StudentRepository;
import com.oxford.csms.domain.finance.model.BusRoute;
import com.oxford.csms.domain.finance.model.Driver;
import com.oxford.csms.domain.finance.model.StudentTransit;
import com.oxford.csms.domain.finance.model.Vehicle;
import com.oxford.csms.domain.finance.repository.BusRouteRepository;
import com.oxford.csms.domain.finance.repository.DriverRepository;
import com.oxford.csms.domain.finance.repository.StudentTransitRepository;
import com.oxford.csms.domain.finance.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransportationServiceImpl implements TransportationService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final BusRouteRepository busRouteRepository;
    private final StudentTransitRepository studentTransitRepository;
    private final StudentRepository studentRepository;

    public TransportationServiceImpl(VehicleRepository vehicleRepository,
                                     DriverRepository driverRepository,
                                     BusRouteRepository busRouteRepository,
                                     StudentTransitRepository studentTransitRepository,
                                     StudentRepository studentRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.busRouteRepository = busRouteRepository;
        this.studentTransitRepository = studentTransitRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Vehicle registerVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Driver registerDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public BusRoute createRoute(BusRoute route) {
        if (route.getStops() != null) {
            for (int i = 0; i < route.getStops().size(); i++) {
                route.getStops().get(i).setSequenceOrder(i + 1);
            }
        }
        return busRouteRepository.save(route);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusRoute> getAllRoutes() {
        return busRouteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BusRoute getRouteById(Long id) {
        return busRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus route not found with ID: " + id));
    }

    @Override
    public StudentTransit assignStudentToTransit(Long studentId, Long routeId, Long stopId) {
        // verify student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        // verify route exists
        BusRoute route = getRouteById(routeId);

        StudentTransit transit = studentTransitRepository.findByStudentId(studentId)
                .orElse(new StudentTransit(studentId, routeId, stopId));

        transit.setRouteId(routeId);
        transit.setStopId(stopId);
        transit.setActive(true);

        student.setBusRouteId(routeId);
        studentRepository.save(student);

        return studentTransitRepository.save(transit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentTransit> getAllTransits() {
        return studentTransitRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentTransit getTransitForStudent(Long studentId) {
        return studentTransitRepository.findByStudentId(studentId).orElse(null);
    }
}
