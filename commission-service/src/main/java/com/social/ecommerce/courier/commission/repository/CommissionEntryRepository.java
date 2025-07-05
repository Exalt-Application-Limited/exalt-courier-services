package com.gogidix.courierservices.commission.$1;

import com.gogidix.courier.commission.model.CommissionEntry;
import com.gogidix.courier.commission.model.CommissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommissionEntryRepository extends JpaRepository<CommissionEntry, String> {
    
    List<CommissionEntry> findByPartnerId(String partnerId);
    
    List<CommissionEntry> findByStatus(CommissionStatus status);
    
    List<CommissionEntry> findByPartnerIdAndStatus(String partnerId, CommissionStatus status);
    
    List<CommissionEntry> findByOrderId(String orderId);
    
    @Query("SELECT e FROM CommissionEntry e WHERE e.transactionDate BETWEEN :startDate AND :endDate")
    List<CommissionEntry> findByTransactionDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM CommissionEntry e WHERE e.partnerId = :partnerId " +
           "AND e.transactionDate BETWEEN :startDate AND :endDate")
    List<CommissionEntry> findByPartnerIdAndTransactionDateBetween(
            @Param("partnerId") String partnerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM CommissionEntry e WHERE e.status = :status " +
           "AND e.transactionDate BETWEEN :startDate AND :endDate")
    List<CommissionEntry> findByStatusAndTransactionDateBetween(
            @Param("status") CommissionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM CommissionEntry e WHERE e.paymentId IS NULL AND e.status = :status")
    List<CommissionEntry> findUnpaidCommissions(@Param("status") CommissionStatus status);
    
    @Query("SELECT e FROM CommissionEntry e WHERE e.partnerId = :partnerId AND e.paymentId IS NULL AND e.status = :status")
    List<CommissionEntry> findUnpaidCommissionsByPartner(
            @Param("partnerId") String partnerId,
            @Param("status") CommissionStatus status);
    
    @Query("SELECT e FROM CommissionEntry e WHERE e.transactionDate BETWEEN :startDate AND :endDate AND e.status = :status")
    List<CommissionEntry> findByTransactionDateBetweenAndStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") CommissionStatus status);
}
