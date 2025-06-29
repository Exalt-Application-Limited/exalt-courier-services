package com.exalt.courierservices.payout.$1;

import com.exalt.courier.payout.model.Payout;
import com.exalt.courier.payout.service.PayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payouts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PayoutController {
    
    @Autowired
    private PayoutService payoutService;
    
    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<Payout>> getPayoutHistory(@PathVariable String courierId) {
        List<Payout> payouts = payoutService.getPayoutHistory(courierId);
        return ResponseEntity.ok(payouts);
    }
    
    @GetMapping("/{payoutId}")
    public ResponseEntity<Payout> getPayoutDetails(@PathVariable String payoutId) {
        Payout payout = payoutService.getPayoutDetails(payoutId);
        return ResponseEntity.ok(payout);
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<String> triggerPayoutCalculation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        payoutService.calculatePayouts(startDate, endDate);
        return ResponseEntity.ok("Payout calculation triggered successfully for period: " + 
                startDate + " to " + endDate);
    }
    
    @PostMapping("/process")
    public ResponseEntity<String> triggerPayoutProcessing() {
        payoutService.processPendingPayouts();
        return ResponseEntity.ok("Payout processing triggered successfully");
    }
}

