package com.gogidix.courier.billing.repository;

import com.gogidix.courier.billing.model.PricingTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Pricing Tier operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface PricingTierRepository extends JpaRepository<PricingTier, UUID> {

    /**
     * Find pricing tier by name.
     */
    Optional<PricingTier> findByTierName(String tierName);

    /**
     * Find all active pricing tiers.
     */
    List<PricingTier> findByActiveOrderByMinMonthlyVolumeAsc(Boolean active);

    /**
     * Find pricing tier for volume.
     */
    @Query("SELECT pt FROM PricingTier pt WHERE pt.active = true AND pt.minMonthlyVolume <= :volume AND (pt.maxMonthlyVolume IS NULL OR pt.maxMonthlyVolume >= :volume) AND pt.effectiveFrom <= :currentDate AND (pt.effectiveUntil IS NULL OR pt.effectiveUntil >= :currentDate) ORDER BY pt.minMonthlyVolume DESC")
    Optional<PricingTier> findTierForVolume(@Param("volume") Integer volume, @Param("currentDate") LocalDateTime currentDate);

    /**
     * Find all effective pricing tiers.
     */
    @Query("SELECT pt FROM PricingTier pt WHERE pt.active = true AND pt.effectiveFrom <= :currentDate AND (pt.effectiveUntil IS NULL OR pt.effectiveUntil >= :currentDate) ORDER BY pt.minMonthlyVolume ASC")
    List<PricingTier> findEffectivePricingTiers(@Param("currentDate") LocalDateTime currentDate);
}