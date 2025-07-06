package com.gogidix.courier.commission.repository;

import com.gogidix.courier.commission.model.CommissionRule;
import com.gogidix.courier.commission.model.CommissionStatus;
import com.gogidix.courier.commission.model.PartnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommissionRuleRepository extends JpaRepository<CommissionRule, String> {
    
    List<CommissionRule> findByPartnerType(PartnerType partnerType);
    
    List<CommissionRule> findByStatus(CommissionStatus status);
    
    @Query("SELECT r FROM CommissionRule r WHERE r.partnerType = :partnerType AND r.status = :status " +
           "AND (r.startDate <= :date) AND (r.endDate IS NULL OR r.endDate >= :date) " +
           "ORDER BY r.priority DESC")
    List<CommissionRule> findActiveRulesByPartnerTypeAndDate(
            @Param("partnerType") PartnerType partnerType,
            @Param("status") CommissionStatus status,
            @Param("date") LocalDate date);
    
    @Query("SELECT r FROM CommissionRule r WHERE r.status = :status " +
           "AND (r.startDate <= :date) AND (r.endDate IS NULL OR r.endDate >= :date)")
    List<CommissionRule> findActiveRulesByDate(
            @Param("status") CommissionStatus status,
            @Param("date") LocalDate date);
}
