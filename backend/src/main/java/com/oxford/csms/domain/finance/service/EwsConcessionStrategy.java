package com.oxford.csms.domain.finance.service;

import com.oxford.csms.domain.admin.model.Student;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class EwsConcessionStrategy implements ScholarshipStrategy {

    @Override
    public boolean supports(String scholarshipType) {
        return "EWS".equalsIgnoreCase(scholarshipType);
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal grossAmount, Student student) {
        if (grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // 50% EWS discount
        return grossAmount.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "EWS Affirmative Action (50% Concession)";
    }
}
