package com.cwm.studentmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cwm.studentmanagement.model.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	
	boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

}
