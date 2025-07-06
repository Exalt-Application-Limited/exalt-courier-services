package com.gogidix.courier.commission.repository;

import com.gogidix.courier.commission.model.PaymentDetails;
import com.gogidix.courier.commission.model.PaymentDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, PaymentDetailsId> {
    
    List<PaymentDetails> findByPaymentId(String paymentId);
    
    List<PaymentDetails> findByCommissionEntryId(String commissionEntryId);
}
