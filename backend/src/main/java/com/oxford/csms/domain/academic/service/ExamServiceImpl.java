package com.oxford.csms.domain.academic.service;

import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.academic.model.ExamSubject;
import com.oxford.csms.domain.academic.model.ExamTerm;
import com.oxford.csms.domain.academic.model.StudentGrade;
import com.oxford.csms.domain.academic.repository.ExamSubjectRepository;
import com.oxford.csms.domain.academic.repository.ExamTermRepository;
import com.oxford.csms.domain.academic.repository.StudentGradeRepository;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.repository.StudentRepository;
import com.oxford.csms.web.dto.ReportCardDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamTermRepository examTermRepository;
    private final ExamSubjectRepository examSubjectRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final StudentRepository studentRepository;

    public ExamServiceImpl(ExamTermRepository examTermRepository,
                           ExamSubjectRepository examSubjectRepository,
                           StudentGradeRepository studentGradeRepository,
                           StudentRepository studentRepository) {
        this.examTermRepository = examTermRepository;
        this.examSubjectRepository = examSubjectRepository;
        this.studentGradeRepository = studentGradeRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public ExamTerm createExamTerm(ExamTerm term) {
        return examTermRepository.save(term);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamTerm> getAllExamTerms() {
        return examTermRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamTerm getExamTermById(Long id) {
        return examTermRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam term not found with id: " + id));
    }

    @Override
    public ExamSubject addExamSubject(ExamSubject subject) {
        return examSubjectRepository.save(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamSubject> getSubjectsForTermAndGrade(Long examTermId, String grade) {
        return examSubjectRepository.findByExamTermIdAndGrade(examTermId, grade);
    }

    @Override
    public StudentGrade recordStudentGrade(StudentGrade grade) {
        Optional<StudentGrade> existing = studentGradeRepository.findByExamTermIdAndStudentIdAndSubjectName(
                grade.getExamTermId(), grade.getStudentId(), grade.getSubjectName());
        if (existing.isPresent()) {
            StudentGrade ex = existing.get();
            ex.setMarksObtained(grade.getMarksObtained());
            ex.setMaxMarks(grade.getMaxMarks());
            ex.setRemarks(grade.getRemarks());
            return studentGradeRepository.save(ex);
        }
        return studentGradeRepository.save(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGrade> getGradesForStudentInTerm(Long examTermId, Long studentId) {
        return studentGradeRepository.findByExamTermIdAndStudentId(examTermId, studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGrade> getGradesForClassInTerm(Long examTermId, String grade, String section) {
        return studentGradeRepository.findByExamTermIdAndGradeAndSection(examTermId, grade, section);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportCardDto generateReportCard(Long examTermId, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        ExamTerm term = getExamTermById(examTermId);
        List<StudentGrade> grades = studentGradeRepository.findByExamTermIdAndStudentId(examTermId, studentId);

        ReportCardDto report = new ReportCardDto();
        report.setTermName(term.getTermName());
        report.setAcademicYear(term.getAcademicYear());
        report.setStudentName(student.getFullName());
        report.setAdmissionNo(student.getAdmissionNo());
        report.setRollNo(student.getRollNo());
        report.setGrade(student.getCurrentClass());
        report.setSection(student.getSection());
        report.setAttendancePercentage(student.getAttendanceRate());
        report.setSubjectGrades(grades);

        double totalObtained = 0.0;
        double totalMax = 0.0;
        for (StudentGrade g : grades) {
            totalObtained += (g.getMarksObtained() != null ? g.getMarksObtained() : 0.0);
            totalMax += (g.getMaxMarks() != null ? g.getMaxMarks() : 100.0);
        }

        report.setTotalMarksObtained(totalObtained);
        report.setTotalMaxMarks(totalMax);

        double pct = (totalMax > 0) ? (totalObtained / totalMax) * 100.0 : 0.0;
        report.setPercentage(Math.round(pct * 100.0) / 100.0);
        report.setOverallGrade(StudentGrade.computeGradeLetter(totalObtained, totalMax));
        report.setOverallResult(pct >= 33.0 ? "PROMOTED / PASSED" : "NEEDS REMEDIAL ATTENTION");
        report.setRemarks(pct >= 80.0 ? "Outstanding academic consistency. Oxford International School is proud of your effort." :
                pct >= 60.0 ? "Good performance. Keep up active class participation." : "Satisfactory progress with room for improvement.");

        return report;
    }
}
