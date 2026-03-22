package com.cwm.studentmanagement.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cwm.studentmanagement.dto.EnrollmentDTO;
import com.cwm.studentmanagement.dto.EnrollmentSummaryDTO;
import com.cwm.studentmanagement.service.EnrollmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public ResponseEntity<Page<EnrollmentSummaryDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(enrollmentService.getEnrolledStudents(page, size));
    }

    @GetMapping("/{studentId}/details")
    public ResponseEntity<EnrollmentSummaryDTO> details(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.findEnrolledStudentCourseDetails(studentId));
    }

    @PostMapping
    public ResponseEntity<?> enroll(@Valid @RequestBody EnrollmentDTO dto) {
        enrollmentService.enrollStudentToCourses(dto);
        return ResponseEntity.ok().build();
    }
}