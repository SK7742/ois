package com.oxford.csms.domain.academic.service;

import com.oxford.csms.domain.academic.model.TimetableSlot;

import java.util.List;

public interface TimetableService {
    List<TimetableSlot> getTimetableForClass(String grade, String section);
    List<TimetableSlot> getTimetableForTeacher(Long teacherId);
    List<TimetableSlot> generateTimetableForClass(String grade, String section, String roomNumber);
    TimetableSlot saveSlot(TimetableSlot slot);
    void clearTimetableForClass(String grade, String section);
}
