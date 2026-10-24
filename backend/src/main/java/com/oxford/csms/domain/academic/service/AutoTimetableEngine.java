package com.oxford.csms.domain.academic.service;

import com.oxford.csms.domain.academic.model.TimetableSlot;
import com.oxford.csms.domain.admin.model.TeachingStaff;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AutoTimetableEngine {

    private static final String[] DAYS = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    private static final String[] PERIOD_TIMES = {
            "08:30 - 09:15",
            "09:15 - 10:00",
            "10:15 - 11:00",
            "11:00 - 11:45",
            "12:15 - 01:00",
            "01:00 - 01:45",
            "01:45 - 02:30"
    };

    public static class SubjectRequirement {
        private final String subjectName;
        private final int periodsPerWeek;
        private final Long preferredTeacherId;
        private final String preferredTeacherName;

        public SubjectRequirement(String subjectName, int periodsPerWeek, Long preferredTeacherId, String preferredTeacherName) {
            this.subjectName = subjectName;
            this.periodsPerWeek = periodsPerWeek;
            this.preferredTeacherId = preferredTeacherId;
            this.preferredTeacherName = preferredTeacherName;
        }

        public String getSubjectName() { return subjectName; }
        public int getPeriodsPerWeek() { return periodsPerWeek; }
        public Long getPreferredTeacherId() { return preferredTeacherId; }
        public String getPreferredTeacherName() { return preferredTeacherName; }
    }

    public List<TimetableSlot> generateSchedule(String grade, String section, String roomNumber,
                                                List<SubjectRequirement> requirements,
                                                List<TimetableSlot> existingSchoolSchedule) {

        // Track global teacher busy slots: (teacherId + "_" + day + "_" + period)
        Set<String> teacherBusy = new HashSet<>();
        for (TimetableSlot slot : existingSchoolSchedule) {
            if (slot.getTeacherId() != null) {
                teacherBusy.add(slot.getTeacherId() + "_" + slot.getDayOfWeek() + "_" + slot.getPeriodNumber());
            }
        }

        // Build flat list of subject tokens to distribute
        List<SubjectRequirement> tokenPool = new ArrayList<>();
        for (SubjectRequirement req : requirements) {
            for (int i = 0; i < req.getPeriodsPerWeek(); i++) {
                tokenPool.add(req);
            }
        }
        Collections.shuffle(tokenPool, new Random(42)); // deterministic pseudo-random

        List<TimetableSlot> generated = new ArrayList<>();
        int tokenIdx = 0;

        for (String day : DAYS) {
            int periodsForDay = "SATURDAY".equals(day) ? 5 : 7;
            Map<String, Integer> daySubjectCount = new HashMap<>();

            for (int p = 1; p <= periodsForDay; p++) {
                String timeStr = PERIOD_TIMES[p - 1];

                // Find suitable subject from tokenPool without teacher clash
                SubjectRequirement chosen = null;
                int searchAttempts = 0;
                while (searchAttempts < tokenPool.size() && tokenIdx < tokenPool.size()) {
                    SubjectRequirement candidate = tokenPool.get(tokenIdx % tokenPool.size());
                    String teacherKey = candidate.getPreferredTeacherId() + "_" + day + "_" + p;

                    // Avoid > 2 periods of same subject in one day & avoid teacher busy
                    int currentDayCount = daySubjectCount.getOrDefault(candidate.getSubjectName(), 0);
                    if (!teacherBusy.contains(teacherKey) && currentDayCount < 2) {
                        chosen = candidate;
                        daySubjectCount.put(candidate.getSubjectName(), currentDayCount + 1);
                        teacherBusy.add(teacherKey);
                        tokenIdx++;
                        break;
                    }
                    tokenIdx++;
                    searchAttempts++;
                }

                if (chosen == null && !requirements.isEmpty()) {
                    // Fallback to first available requirement that doesn't clash
                    for (SubjectRequirement req : requirements) {
                        String teacherKey = req.getPreferredTeacherId() + "_" + day + "_" + p;
                        if (!teacherBusy.contains(teacherKey)) {
                            chosen = req;
                            teacherBusy.add(teacherKey);
                            break;
                        }
                    }
                    if (chosen == null) {
                        chosen = requirements.get(0);
                    }
                }

                if (chosen != null) {
                    TimetableSlot slot = new TimetableSlot(
                            grade,
                            section,
                            day,
                            p,
                            timeStr,
                            chosen.getSubjectName(),
                            chosen.getPreferredTeacherId(),
                            chosen.getPreferredTeacherName(),
                            roomNumber
                    );
                    generated.add(slot);
                }
            }
        }

        return generated;
    }
}
