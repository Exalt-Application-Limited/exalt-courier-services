# Commission Service Documentation

## Overview
The Commission Service handles commission calculation and processing for courier deliveries within the Social E-commerce Ecosystem. It manages different commission structures, calculates fees for various delivery types, and processes commission payouts for drivers and courier partners.

## Components

### Core Components
- **CommissionApplication**: The main commission service providing calculation, tracking, and payout functionality.
- **SecurityConfig**: Security configuration for commission operations including authentication and access control.

### Feature Components
- **Commission Calculator**: Core engine for calculating commissions based on delivery type, distance, weight, and other factors.
- **Fee Management**: Management of different fee structures including fixed, percentage, and tiered commissions.
- **Payout Processing**: Automated commission payout processing and scheduling.
- **Performance Tracking**: Driver and courier performance metrics for commission optimization.

### Data Access Layer
- **Repository**: Common abstraction for commission data operations.
- **JpaRepository**: JPA implementation for commission, fee, and payout data access.

### Utility Services
- **Validator**: Input validation for commission calculations and payout data.
- **Logger**: Comprehensive logging for commission operations and financial transactions.

### Integration Components
- **RestClient**: HTTP client for integration with courier management and payment services.
- **MessageBroker**: Event publishing for commission events and payout notifications.

## Getting Started
To use the Commission Service, follow these steps:

1. Create a commission application that extends CommissionApplication
2. Configure security settings using SecurityConfig
3. Add required components (Commission Calculator, Fee Management, Payout Processing)
4. Use the data access layer for commission and payout operations
5. Integrate with courier management and payment processing services

## Examples

### Creating a Commission Application
```java
import com.exalt.courier.commission.core.CommissionApplication;
import com.exalt.courier.commission.core.SecurityConfig;
import com.exalt.courier.commission.components.calculator.CommissionCalculator;
import com.exalt.courier.commission.components.fee.FeeManagement;
import com.exalt.courier.commission.components.payout.PayoutProcessing;

@SpringBootApplication
public class CourierCommissionService extends CommissionApplication {
    private final SecurityConfig securityConfig;
    private final CommissionCalculator commissionCalculator;
    private final FeeManagement feeManagement;
    private final PayoutProcessing payoutProcessing;
    
    public CourierCommissionService() {
        super("Courier Commission Service", "Commission calculation and payout processing");
        
        this.securityConfig = new SecurityConfig();
        this.commissionCalculator = new CommissionCalculator("Commission Calculator", "Delivery commission calculation");
        this.feeManagement = new FeeManagement("Fee Management", "Commission structure management");
        this.payoutProcessing = new PayoutProcessing("Payout Processing", "Automated commission payouts");
    }
    
    // Add custom commission logic here
}
```

### Using Commission Calculator
```java
import com.exalt.courier.commission.service.CommissionCalculatorService;
import com.exalt.courier.commission.model.Delivery;
import com.exalt.courier.commission.model.CommissionStructure;

@Service
public class CommissionCalculatorService {
    private final CommissionRepository commissionRepository;
    private final FeeStructureRepository feeStructureRepository;
    
    public CommissionCalculatorService(CommissionRepository commissionRepository,
                                     FeeStructureRepository feeStructureRepository) {
        this.commissionRepository = commissionRepository;
        this.feeStructureRepository = feeStructureRepository;
    }
    
    public BigDecimal calculateDeliveryCommission(Delivery delivery, UUID driverId) {
        CommissionStructure structure = feeStructureRepository.findByDriverId(driverId)
                .orElse(feeStructureRepository.getDefaultStructure());
        
        BigDecimal baseCommission = calculateBaseCommission(delivery, structure);
        BigDecimal distanceBonus = calculateDistanceBonus(delivery.getDistance(), structure);
        BigDecimal performanceBonus = calculatePerformanceBonus(driverId, structure);
        BigDecimal urgencyBonus = calculateUrgencyBonus(delivery.getUrgencyLevel(), structure);
        
        return baseCommission.add(distanceBonus).add(performanceBonus).add(urgencyBonus);
    }
    
    private BigDecimal calculateBaseCommission(Delivery delivery, CommissionStructure structure) {
        switch (structure.getType()) {
            case FIXED:
                return structure.getFixedAmount();
            case PERCENTAGE:
                return delivery.getDeliveryFee().multiply(structure.getPercentage());
            case TIERED:
                return calculateTieredCommission(delivery, structure);
            default:
                throw new IllegalArgumentException("Unknown commission structure type");
        }
    }
    
    private BigDecimal calculateDistanceBonus(double distanceKm, CommissionStructure structure) {
        if (distanceKm > structure.getDistanceThreshold()) {
            return BigDecimal.valueOf(distanceKm - structure.getDistanceThreshold())
                    .multiply(structure.getDistanceBonusRate());
        }
        return BigDecimal.ZERO;
    }
}
```

### Using Payout Processing
```java
import com.exalt.courier.commission.service.PayoutService;
import com.exalt.courier.commission.model.CommissionPayout;

@Service
public class PayoutService {
    private final PayoutRepository payoutRepository;
    private final CommissionRepository commissionRepository;
    private final PaymentService paymentService;
    
    @Scheduled(cron = "0 0 2 * * MON") // Weekly payouts every Monday at 2 AM
    public void processWeeklyPayouts() {
        List<UUID> driversForPayout = commissionRepository.getDriversWithUnpaidCommissions();
        
        for (UUID driverId : driversForPayout) {
            try {
                processDriverPayout(driverId);
            } catch (Exception e) {
                logger.error("Failed to process payout for driver: " + driverId, e);
            }
        }
    }
    
    private void processDriverPayout(UUID driverId) {
        List<Commission> unpaidCommissions = commissionRepository.findUnpaidByDriverId(driverId);
        BigDecimal totalAmount = unpaidCommissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalAmount.compareTo(BigDecimal.valueOf(10)) < 0) {
            // Minimum payout threshold not met
            return;
        }
        
        CommissionPayout payout = new CommissionPayout();
        payout.setDriverId(driverId);
        payout.setAmount(totalAmount);
        payout.setCommissions(unpaidCommissions);
        payout.setStatus(PayoutStatus.PENDING);
        
        // Process payment
        PaymentResult result = paymentService.processDriverPayout(driverId, totalAmount);
        
        if (result.isSuccessful()) {
            payout.setStatus(PayoutStatus.COMPLETED);
            payout.setPaymentId(result.getPaymentId());
            
            // Mark commissions as paid
            unpaidCommissions.forEach(commission -> commission.setStatus(CommissionStatus.PAID));
            commissionRepository.saveAll(unpaidCommissions);
            
            // Send notification
            notificationService.sendPayoutNotification(driverId, totalAmount);
        } else {
            payout.setStatus(PayoutStatus.FAILED);
            payout.setFailureReason(result.getErrorMessage());
        }
        
        payoutRepository.save(payout);
    }
}
```

## Best Practices
1. **Security**: Always use SecurityConfig for financial transaction security
2. **Validation**: Use the Validator utility for all commission and payout data
3. **Logging**: Use the Logger utility for comprehensive financial operation tracking
4. **Error Handling**: Handle errors gracefully for financial operations
5. **Performance**: Use caching for commission structure calculations
6. **Audit Trail**: Maintain complete audit trail for all commission transactions
7. **Compliance**: Ensure compliance with financial regulations and tax requirements