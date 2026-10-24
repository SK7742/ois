package com.oxford.csms.web.controller;

import com.oxford.csms.common.ApiResponse;
import com.oxford.csms.domain.admin.model.Student;
import com.oxford.csms.domain.admin.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Student>> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(ApiResponse.ok("Student created", studentService.createStudent(student)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents(
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String section) {
        if (grade != null && section != null) {
            return ResponseEntity.ok(ApiResponse.ok(studentService.getStudentsByClassAndSection(grade, section)));
        } else if (grade != null) {
            return ResponseEntity.ok(ApiResponse.ok(studentService.getStudentsByClass(grade)));
        }
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAllStudents()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getStudentById(id)));
    }

    @GetMapping("/admission/{admissionNo}")
    public ResponseEntity<ApiResponse<Student>> getStudentByAdmissionNo(@PathVariable String admissionNo) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getStudentByAdmissionNo(admissionNo)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return ResponseEntity.ok(ApiResponse.ok("Student updated", studentService.updateStudent(id, student)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.ok("Student marked inactive", null));
    }

    @PatchMapping("/{id}/attendance")
    public ResponseEntity<ApiResponse<Student>> updateAttendance(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        Student student = studentService.getStudentById(id);
        if (payload.containsKey("attendanceRate")) {
            student.setAttendanceRate(Double.parseDouble(payload.get("attendanceRate").toString()));
        }
        if (payload.containsKey("delta")) {
            double delta = Double.parseDouble(payload.get("delta").toString());
            double updated = Math.min(100.0, Math.max(0.0, student.getAttendanceRate() + delta));
            student.setAttendanceRate(Math.round(updated * 10.0) / 10.0);
        }
        Student saved = studentService.updateStudent(id, student);
        return ResponseEntity.ok(ApiResponse.ok("Student attendance updated", saved));
    }

    @PatchMapping("/{id}/notes")
    public ResponseEntity<ApiResponse<Student>> updateNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        Student student = studentService.getStudentById(id);
        if (payload.containsKey("notesSummary")) {
            student.setNotesSummary(payload.get("notesSummary"));
        }
        Student saved = studentService.updateStudent(id, student);
        return ResponseEntity.ok(ApiResponse.ok("Student notes updated", saved));
    }

    @GetMapping("/stats/class-distribution")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getClassDistribution() {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getStudentCountByClass()));
    }
}
