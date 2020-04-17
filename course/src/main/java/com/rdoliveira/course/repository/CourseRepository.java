package com.rdoliveira.course.repository;

import com.rdoliveira.course.model.Course;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author rodrigodonizettio
 */
public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {
}
