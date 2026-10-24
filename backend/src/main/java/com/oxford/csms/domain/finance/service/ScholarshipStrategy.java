package com.oxford.csms.domain.finance.service;

import com.oxford.csms.domain.admin.model.Student;
import java.math.BigDecimal;

public interface ScholarshipStrategy {
    boolean supports(String scholarshipType);
    BigDecimal calculateDiscount(BigDecimal grossAmount, Student student);
    String getStrategyName();
}
