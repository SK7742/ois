package com.oxford.csms.domain.finance.service;

import com.oxford.csms.domain.admin.model.Student;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        if (scholarshipType != null && !scholarshipType.isBlank() && !"NONE".equalsIgnoreCase(scholarshipType)) {
            for (ScholarshipStrategy strategy : scholarshipStrategies) {
                if (strategy.supports(scholarshipType)) {
                    BigDecimal discount = strategy.calculateDiscount(grossAmount, student);
                    BigDecimal net = grossAmount.subtract(discount).max(BigDecimal.ZERO);
                    return new FeeCalculationResult(grossAmount, discount, net, strategy.getStrategyName());
                }
            }
        }

        // Check if student has individual fee concession configured
        if (student != null) {
            if (student.getConcessionPercentage() != null && student.getConcessionPercentage() > 0) {
                BigDecimal pct = BigDecimal.valueOf(student.getConcessionPercentage())
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal discount = grossAmount.multiply(pct).setScale(2, RoundingMode.HALF_UP);
                BigDecimal net = grossAmount.subtract(discount).max(BigDecimal.ZERO);
                String reason = student.getFeeCategory() + (student.getConcessionReason() != null && !student.getConcessionReason().isBlank() ? " (" + student.getConcessionReason() + ")" : " Concession");
                return new FeeCalculationResult(grossAmount, discount, net, reason);
            } else if (student.getConcessionAmount() != null && student.getConcessionAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = student.getConcessionAmount().min(grossAmount);
                BigDecimal net = grossAmount.subtract(discount).max(BigDecimal.ZERO);
                String reason = student.getFeeCategory() + (student.getConcessionReason() != null && !student.getConcessionReason().isBlank() ? " (" + student.getConcessionReason() + ")" : " Concession");
                return new FeeCalculationResult(grossAmount, discount, net, reason);
            }
        }

        return new FeeCalculationResult(grossAmount, BigDecimal.ZERO, grossAmount, "None");
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
