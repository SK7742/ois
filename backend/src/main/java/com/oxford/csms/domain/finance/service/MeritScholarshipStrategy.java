package com.oxford.csms.domain.finance.service;

import com.oxford.csms.domain.admin.model.Student;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class MeritScholarshipStrategy implements ScholarshipStrategy {

    @Override
    public boolean supports(String scholarshipType) {
        return "MERIT".equalsIgnoreCase(scholarshipType);
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal grossAmount, Student student) {
        if (grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // 25% Merit discount
        return grossAmount.multiply(new BigDecimal("0.25")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "Merit Academic Excellence (25% Waiver)";
    }
}
