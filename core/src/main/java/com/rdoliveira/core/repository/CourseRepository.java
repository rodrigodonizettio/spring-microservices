package com.rdoliveira.core.repository;

import com.rdoliveira.core.model.Course;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author rodrigodonizettio
 */
public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {
}
