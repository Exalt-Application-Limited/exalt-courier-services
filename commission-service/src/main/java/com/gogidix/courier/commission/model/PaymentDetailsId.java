package com.gogidix.courier.commission.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsId implements Serializable {
    
    private String paymentId;
    private String commissionEntryId;
}
