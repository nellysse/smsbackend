package com.cwm.studentmanagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cwm.studentmanagement.dto.CourseDTO;
import com.cwm.studentmanagement.model.Courses;
import com.cwm.studentmanagement.repository.CourseRepository;
import com.cwm.studentmanagement.service.CourseService;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final ModelMapper mapper;

    CourseServiceImpl(CourseRepository courseRepository, ModelMapper mapper) {
        this.courseRepository = courseRepository;
        this.mapper = mapper;

        TypeMap<CourseDTO, Courses> updateMap =
                mapper.createTypeMap(CourseDTO.class, Courses.class);
        updateMap.addMappings(m -> {
            m.skip(Courses::setId);
            m.skip(Courses::setCreatedAt);
            m.skip(Courses::setEnrollments);
        });
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        log.info("creating course with code: {}", courseDTO.getCourseCode());
        Courses courses = mapper.map(courseDTO, Courses.class);
        courseRepository.save(courses);
        return mapper.map(courses, CourseDTO.class);
    }

    @Override
    public boolean existsByCourseCode(String code) {
        log.info("checking if code exists: {}", code);
        return courseRepository.existsByCourseCodeIgnoreCase(code);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> getCourses(int page, int size) {
        log.info("list of course from: {}", page);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Direction.DESC, "id"));
        return courseRepository.findByActiveTrue(pageRequest)
                .map(course -> mapper.map(course, CourseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        Courses course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No course found with id: " + id));
        return mapper.map(course, CourseDTO.class);
    }

    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Courses course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No course found with id: " + id));
        // Uses the configured TypeMap that skips id / createdAt / enrollments
        mapper.map(courseDTO, course);
        Courses updated = courseRepository.save(course);
        return mapper.map(updated, CourseDTO.class);
    }

    @Override
    public boolean existsByCourseCodeAndIdNot(String code, Long id) {
        log.info("code from update page: {}, id: {}", code, id);
        return courseRepository.existsByCourseCodeIgnoreCaseAndIdNot(code, id);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findByActiveTrue(Sort.by("courseName")).stream()
                .map(course -> mapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }
}