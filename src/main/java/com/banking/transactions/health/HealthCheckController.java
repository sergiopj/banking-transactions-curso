package com.banking.transactions.health;

import java.util.Map;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public Map<String, Integer> healthCheck() {
        return Map.of("status", 200);
    }

}
