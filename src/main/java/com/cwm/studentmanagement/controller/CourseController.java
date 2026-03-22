package com.cwm.studentmanagement.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cwm.studentmanagement.dto.CourseDTO;
import com.cwm.studentmanagement.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<Page<CourseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(courseService.getCourses(page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseDTO>> all() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CourseDTO dto) {
        if (courseService.existsByCourseCode(dto.getCourseCode())) {
            return ResponseEntity.badRequest().body("Course code already exists");
        }
        return ResponseEntity.ok(courseService.createCourse(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody CourseDTO dto) {
        if (courseService.existsByCourseCodeAndIdNot(dto.getCourseCode(), id)) {
            return ResponseEntity.badRequest().body("Course code already exists");
        }
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }
}