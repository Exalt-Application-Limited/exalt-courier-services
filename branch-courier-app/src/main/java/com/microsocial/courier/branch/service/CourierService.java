package com.gogidix.courier.branch.service;

import com.gogidix.courier.branch.model.Courier;
import com.gogidix.courier.branch.model.CourierStatus;

import java.util.List;
import java.util.Optional;

public interface CourierService {
    List<Courier> getAllCouriers();
    Optional<Courier> getCourierById(Long id);
    List<Courier> findCouriersByStatus(CourierStatus status);
    List<Courier> findActiveCouriers(boolean active);
    List<Courier> findCouriersByName(String name);
    List<Courier> findCouriersByVehicleType(String vehicleType);
    Courier saveCourier(Courier courier);
    void deleteCourier(Long id);
    boolean existsById(Long id);
    Courier updateCourierStatus(Long id, CourierStatus status);
}
