package com.oxford.csms.domain.academic.repository;

import com.oxford.csms.domain.academic.model.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {
    List<TimetableSlot> findByGradeAndSection(String grade, String section);
    List<TimetableSlot> findByTeacherId(Long teacherId);
    boolean existsByGradeAndSectionAndDayOfWeekAndPeriodNumber(String grade, String section, String dayOfWeek, Integer periodNumber);
    boolean existsByTeacherIdAndDayOfWeekAndPeriodNumber(Long teacherId, String dayOfWeek, Integer periodNumber);
    void deleteByGradeAndSection(String grade, String section);
}
