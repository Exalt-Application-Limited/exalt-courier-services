package com.exalt.courierservices.commission.$1;

import com.exalt.courier.commission.model.PartnerPayment;
import com.exalt.courier.commission.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartnerPaymentRepository extends JpaRepository<PartnerPayment, String> {
    
    List<PartnerPayment> findByPartnerId(String partnerId);
    
    List<PartnerPayment> findByStatus(PaymentStatus status);
    
    List<PartnerPayment> findByPartnerIdAndStatus(String partnerId, PaymentStatus status);
    
    @Query("SELECT p FROM PartnerPayment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<PartnerPayment> findByPaymentDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM PartnerPayment p WHERE p.periodStart >= :startDate AND p.periodEnd <= :endDate")
    List<PartnerPayment> findByPeriodBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM PartnerPayment p WHERE p.partnerId = :partnerId " +
           "AND p.periodStart >= :startDate AND p.periodEnd <= :endDate")
    List<PartnerPayment> findByPartnerIdAndPeriodBetween(
            @Param("partnerId") String partnerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM PartnerPayment p WHERE p.status = :status " +
           "AND p.paymentDate IS NULL")
    List<PartnerPayment> findPendingPayments(@Param("status") PaymentStatus status);
}
