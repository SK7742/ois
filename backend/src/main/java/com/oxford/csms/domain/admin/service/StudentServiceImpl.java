package com.oxford.csms.domain.admin.service;

import com.oxford.csms.common.ResourceNotFoundException;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Long id, Student details) {
        Student existing = getStudentById(id);
        existing.setFirstName(details.getFirstName());
        existing.setLastName(details.getLastName());
        existing.setCurrentClass(details.getCurrentClass());
        existing.setSection(details.getSection());
        existing.setRollNo(details.getRollNo());
        existing.setGuardianName(details.getGuardianName());
        existing.setGuardianPhone(details.getGuardianPhone());
        existing.setGuardianEmail(details.getGuardianEmail());
        existing.setBloodGroup(details.getBloodGroup());
        existing.setResidentialAddress(details.getResidentialAddress());
        existing.setBusRouteId(details.getBusRouteId());
        existing.setAttendanceRate(details.getAttendanceRate());
        existing.setFeeStatus(details.getFeeStatus());
        existing.setNotesSummary(details.getNotesSummary());
        existing.setActive(details.isActive());
        return studentRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Student getStudentByAdmissionNo(String admissionNo) {
        return studentRepository.findByAdmissionNo(admissionNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with Admission No: " + admissionNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getStudentsByClass(String currentClass) {
        return studentRepository.findByCurrentClass(currentClass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getStudentsByClassAndSection(String currentClass, String section) {
        return studentRepository.findByCurrentClassAndSection(currentClass, section);
    }

    @Override
    public void deleteStudent(Long id) {
        Student s = getStudentById(id);
        s.setActive(false);
        studentRepository.save(s);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getStudentCountByClass() {
        List<Object[]> rows = studentRepository.countStudentsByClass();
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put((String) row[0], (Long) row[1]);
        }
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalActiveStudents() {
        return studentRepository.countByActiveTrue();
    }
}
