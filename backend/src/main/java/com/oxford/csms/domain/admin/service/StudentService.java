package com.oxford.csms.domain.admin.service;

import com.oxford.csms.domain.admin.model.Student;

import java.util.List;
import java.util.Map;

public interface StudentService {
    Student createStudent(Student student);
    Student updateStudent(Long id, Student studentDetails);
    Student getStudentById(Long id);
    Student getStudentByAdmissionNo(String admissionNo);
    List<Student> getAllStudents();
    List<Student> getStudentsByClass(String currentClass);
    List<Student> getStudentsByClassAndSection(String currentClass, String section);
    void deleteStudent(Long id);
    Map<String, Long> getStudentCountByClass();
    long getTotalActiveStudents();
}
