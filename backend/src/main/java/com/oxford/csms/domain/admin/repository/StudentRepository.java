package com.oxford.csms.domain.admin.repository;

import com.oxford.csms.domain.admin.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByAdmissionNo(String admissionNo);
    List<Student> findByCurrentClass(String currentClass);
    List<Student> findByCurrentClassAndSection(String currentClass, String section);
    List<Student> findByFeeStatus(Student.StudentFeeStatus feeStatus);
    long countByActiveTrue();

    @Query("SELECT s.currentClass, count(s) FROM Student s WHERE s.active = true GROUP BY s.currentClass")
    List<Object[]> countStudentsByClass();
}
