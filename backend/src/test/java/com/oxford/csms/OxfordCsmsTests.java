package com.oxford.csms;

import com.oxford.csms.domain.academic.model.TimetableSlot;
import com.oxford.csms.domain.academic.service.AutoTimetableEngine;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.finance.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OxfordCsmsTests {

    @Test
    @DisplayName("AutoTimetableEngine produces conflict-free schedule")
    void testAutoTimetableScheduler() {
        AutoTimetableEngine engine = new AutoTimetableEngine();

        List<AutoTimetableEngine.SubjectRequirement> reqs = new ArrayList<>();
        reqs.add(new AutoTimetableEngine.SubjectRequirement("Mathematics", 6, 1L, "Dr. Satish Sharma"));
        reqs.add(new AutoTimetableEngine.SubjectRequirement("Science", 6, 2L, "Mrs. Pooja Verma"));
        reqs.add(new AutoTimetableEngine.SubjectRequirement("English", 6, 3L, "Mr. Rakesh Jha"));
        reqs.add(new AutoTimetableEngine.SubjectRequirement("Computer Science", 6, 4L, "Er. Sundram Kumar"));

        List<TimetableSlot> schedule = engine.generateSchedule("Class 10", "A", "Room 101", reqs, List.of());

        assertNotNull(schedule);
        assertFalse(schedule.isEmpty());

        // Verify no period slot is duplicated for this class
        Set<String> classPeriodKeys = new HashSet<>();
        for (TimetableSlot slot : schedule) {
            String key = slot.getDayOfWeek() + "_" + slot.getPeriodNumber();
            assertFalse(classPeriodKeys.contains(key), "Duplicate class slot found at " + key);
            classPeriodKeys.add(key);
        }
    }

    @Test
    @DisplayName("FeeCalculationService OCP Strategy - Merit 25% and Staff Ward 75%")
    void testFeeCalculationStrategies() {
        List<ScholarshipStrategy> strategies = List.of(
                new MeritScholarshipStrategy(),
                new SiblingDiscountStrategy(),
                new EwsConcessionStrategy(),
                new StaffWardStrategy()
        );
        FeeCalculationService feeService = new FeeCalculationService(strategies);
        Student student = new Student();

        BigDecimal gross = new BigDecimal("10000.00");

        // Merit: 25% discount -> 2500, net = 7500
        FeeCalculationService.FeeCalculationResult meritRes = feeService.calculateNetFee(gross, student, "MERIT");
        assertEquals(new BigDecimal("2500.00"), meritRes.getDiscountAmount());
        assertEquals(new BigDecimal("7500.00"), meritRes.getNetPayable());

        // Staff Ward: 75% discount -> 7500, net = 2500
        FeeCalculationService.FeeCalculationResult staffRes = feeService.calculateNetFee(gross, student, "STAFF_WARD");
        assertEquals(new BigDecimal("7500.00"), staffRes.getDiscountAmount());
        assertEquals(new BigDecimal("2500.00"), staffRes.getNetPayable());

        // None: 0 discount -> 0, net = 10000
        FeeCalculationService.FeeCalculationResult noneRes = feeService.calculateNetFee(gross, student, "NONE");
        assertEquals(BigDecimal.ZERO, noneRes.getDiscountAmount());
        assertEquals(gross, noneRes.getNetPayable());
    }
}
