package com.oxford.csms.domain.academic.service;

import com.oxford.csms.domain.academic.model.TimetableSlot;
import com.oxford.csms.domain.academic.repository.TimetableSlotRepository;
import com.oxford.csms.domain.admin.model.TeachingStaff;
import com.oxford.csms.domain.admin.repository.TeachingStaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TimetableServiceImpl implements TimetableService {

    private final TimetableSlotRepository timetableSlotRepository;
    private final TeachingStaffRepository teachingStaffRepository;
    private final AutoTimetableEngine autoTimetableEngine;

    public TimetableServiceImpl(TimetableSlotRepository timetableSlotRepository,
                                TeachingStaffRepository teachingStaffRepository,
                                AutoTimetableEngine autoTimetableEngine) {
        this.timetableSlotRepository = timetableSlotRepository;
        this.teachingStaffRepository = teachingStaffRepository;
        this.autoTimetableEngine = autoTimetableEngine;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableSlot> getTimetableForClass(String grade, String section) {
        return timetableSlotRepository.findByGradeAndSection(grade, section);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableSlot> getTimetableForTeacher(Long teacherId) {
        return timetableSlotRepository.findByTeacherId(teacherId);
    }

    @Override
    public List<TimetableSlot> generateTimetableForClass(String grade, String section, String roomNumber) {
        // Clear existing slots for this class & section
        timetableSlotRepository.deleteByGradeAndSection(grade, section);

        List<TeachingStaff> teachers = teachingStaffRepository.findAll();
        List<AutoTimetableEngine.SubjectRequirement> requirements = new ArrayList<>();

        if (teachers.isEmpty()) {
            // Default subjects
            requirements.add(new AutoTimetableEngine.SubjectRequirement("Mathematics", 6, 1L, "Dr. S. Sharma"));
            requirements.add(new AutoTimetableEngine.SubjectRequirement("Science", 6, 2L, "Mrs. P. Verma"));
            requirements.add(new AutoTimetableEngine.SubjectRequirement("English", 6, 3L, "Mr. R. Jha"));
            requirements.add(new AutoTimetableEngine.SubjectRequirement("Social Science", 5, 4L, "Mr. A. Mishra"));
            requirements.add(new AutoTimetableEngine.SubjectRequirement("Hindi", 5, 5L, "Mrs. K. Pandey"));
            requirements.add(new AutoTimetableEngine.SubjectRequirement("Computer Science", 4, 6L, "Er. Sundram Kumar"));
            requirements.add(new AutoTimetableEngine.SubjectRequirement("Physical Education", 4, 7L, "Mr. V. Yadav"));
            requirements.add(new AutoTimetableEngine.SubjectRequirement("Art & Culture", 4, 8L, "Ms. A. Singh"));
        } else {
            // Map teachers to subjects
            for (TeachingStaff teacher : teachers) {
                String subj = teacher.getSubjectSpecialization() != null ? teacher.getSubjectSpecialization() : "General Studies";
                requirements.add(new AutoTimetableEngine.SubjectRequirement(subj, 5, teacher.getId(), teacher.getFullName()));
            }
            if (requirements.size() < 6) {
                requirements.add(new AutoTimetableEngine.SubjectRequirement("Sports & PE", 4, 99L, "Coach Anand"));
                requirements.add(new AutoTimetableEngine.SubjectRequirement("Library", 3, 98L, "Librarian"));
            }
        }

        List<TimetableSlot> existingAll = timetableSlotRepository.findAll();
        String room = (roomNumber != null && !roomNumber.isBlank()) ? roomNumber : "Room " + grade.replaceAll("[^0-9]", "") + section;

        List<TimetableSlot> slots = autoTimetableEngine.generateSchedule(grade, section, room, requirements, existingAll);
        return timetableSlotRepository.saveAll(slots);
    }

    @Override
    public TimetableSlot saveSlot(TimetableSlot slot) {
        return timetableSlotRepository.save(slot);
    }

    @Override
    public void clearTimetableForClass(String grade, String section) {
        timetableSlotRepository.deleteByGradeAndSection(grade, section);
    }
}
