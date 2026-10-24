package com.oxford.csms.domain.finance.service;

import com.oxford.csms.domain.admin.model.Student;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FeeCalculationService {

    private final List<ScholarshipStrategy> scholarshipStrategies;

    public FeeCalculationService(List<ScholarshipStrategy> scholarshipStrategies) {
        this.scholarshipStrategies = scholarshipStrategies;
    }

    public FeeCalculationResult calculateNetFee(BigDecimal grossAmount, Student student, String scholarshipType) {
        if (grossAmount == null) {
            grossAmount = BigDecimal.ZERO;
        }

        if (scholarshipType == null || scholarshipType.isBlank() || "NONE".equalsIgnoreCase(scholarshipType)) {
            return new FeeCalculationResult(grossAmount, BigDecimal.ZERO, grossAmount, "None");
        }

        for (ScholarshipStrategy strategy : scholarshipStrategies) {
            if (strategy.supports(scholarshipType)) {
                BigDecimal discount = strategy.calculateDiscount(grossAmount, student);
                BigDecimal net = grossAmount.subtract(discount).max(BigDecimal.ZERO);
                return new FeeCalculationResult(grossAmount, discount, net, strategy.getStrategyName());
            }
        }

        return new FeeCalculationResult(grossAmount, BigDecimal.ZERO, grossAmount, "Standard Rate");
    }

    public static class FeeCalculationResult {
        private final BigDecimal grossAmount;
        private final BigDecimal discountAmount;
        private final BigDecimal netPayable;
        private final String scholarshipApplied;

        public FeeCalculationResult(BigDecimal grossAmount, BigDecimal discountAmount, BigDecimal netPayable, String scholarshipApplied) {
            this.grossAmount = grossAmount;
            this.discountAmount = discountAmount;
            this.netPayable = netPayable;
            this.scholarshipApplied = scholarshipApplied;
        }

        public BigDecimal getGrossAmount() {
            return grossAmount;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public BigDecimal getNetPayable() {
            return netPayable;
        }

        public String getScholarshipApplied() {
            return scholarshipApplied;
        }
    }
}
