package com.gogidix.courier.branch.service.impl;

import com.gogidix.courier.branch.model.Courier;
import com.gogidix.courier.branch.model.CourierStatus;
import com.gogidix.courier.branch.repository.CourierRepository;
import com.gogidix.courier.branch.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourierServiceImpl implements CourierService {

    private final CourierRepository courierRepository;

    @Autowired
    public CourierServiceImpl(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Courier> getAllCouriers() {
        return courierRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Courier> getCourierById(Long id) {
        return courierRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Courier> findCouriersByStatus(CourierStatus status) {
        return courierRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Courier> findActiveCouriers(boolean active) {
        return courierRepository.findByActive(active);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Courier> findCouriersByName(String name) {
        return courierRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Courier> findCouriersByVehicleType(String vehicleType) {
        return courierRepository.findByVehicleType(vehicleType);
    }

    @Override
    public Courier saveCourier(Courier courier) {
        LocalDateTime now = LocalDateTime.now();
        
        if (courier.getId() == null) {
            courier.setCreatedAt(now);
        }
        
        courier.setUpdatedAt(now);
        return courierRepository.save(courier);
    }

    @Override
    public void deleteCourier(Long id) {
        courierRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return courierRepository.existsById(id);
    }

    @Override
    public Courier updateCourierStatus(Long id, CourierStatus status) {
        Courier courier = courierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found with id: " + id));
        
        courier.setStatus(status);
        
        if (status == CourierStatus.AVAILABLE) {
            courier.setLastActiveTimestamp(LocalDateTime.now());
        }
        
        courier.setUpdatedAt(LocalDateTime.now());
        return courierRepository.save(courier);
    }
}
