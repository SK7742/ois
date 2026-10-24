package com.oxford.csms.domain.finance.model;

import com.oxford.csms.common.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fee_structures")
public class FeeStructure extends BaseEntity {

    @Column(nullable = false)
    private String grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeCategory category;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String frequency; // MONTHLY, QUARTERLY, ANNUAL

    private String description;

    public FeeStructure() {}

    public FeeStructure(String grade, FeeCategory category, BigDecimal amount, String frequency, String description) {
        this.grade = grade;
        this.category = category;
        this.amount = amount;
        this.frequency = frequency;
        this.description = description;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public FeeCategory getCategory() {
        return category;
    }

    public void setCategory(FeeCategory category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
