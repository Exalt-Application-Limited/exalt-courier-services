package com.exalt.courier.courier.branch.repository;

import com.microsocial.courier.branch.model.Courier;
import com.microsocial.courier.branch.model.CourierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    
    List<Courier> findByStatus(CourierStatus status);
    
    List<Courier> findByActive(boolean active);
    
    @Query("SELECT c FROM Courier c WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Courier> findByName(@Param("name") String name);
    
    List<Courier> findByVehicleType(String vehicleType);
}
