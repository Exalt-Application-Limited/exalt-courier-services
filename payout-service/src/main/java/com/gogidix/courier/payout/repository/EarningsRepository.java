package com.gogidix.courierservices.payout.$1;

import com.gogidix.courier.payout.model.Earning;
import com.gogidix.courier.payout.model.EarningStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EarningsRepository extends JpaRepository<Earning, String> {
    
    List<Earning> findByCourierId(String courierId);
    
    @Query("SELECT e FROM Earning e WHERE e.courierId = :courierId AND e.earnedAt BETWEEN :startDate AND :endDate")
    List<Earning> findByCourierIdAndEarnedAtBetween(
            @Param("courierId") String courierId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Earning e WHERE e.status = :status AND e.earnedAt BETWEEN :startDate AND :endDate")
    List<Earning> findByStatusAndEarnedAtBetween(
            @Param("status") EarningStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Earning e WHERE e.courierId = :courierId AND e.status = :status")
    List<Earning> findByCourierIdAndStatus(
            @Param("courierId") String courierId,
            @Param("status") EarningStatus status);
}
