package com.gogidix.courierservices.commission.$1;

import com.gogidix.courier.commission.model.Partner;
import com.gogidix.courier.commission.model.PartnerStatus;
import com.gogidix.courier.commission.model.PartnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, String> {
    
    List<Partner> findByPartnerType(PartnerType partnerType);
    
    List<Partner> findByStatus(PartnerStatus status);
    
    List<Partner> findByPartnerTypeAndStatus(PartnerType partnerType, PartnerStatus status);
    
    List<Partner> findByNameContainingIgnoreCase(String namePattern);
}
