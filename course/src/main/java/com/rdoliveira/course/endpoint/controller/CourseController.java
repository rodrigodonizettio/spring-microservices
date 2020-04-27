package com.rdoliveira.course.endpoint.controller;

import com.rdoliveira.core.model.Course;
import com.rdoliveira.course.endpoint.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rodrigodonizettio
 */
@RestController
@RequestMapping("v1/admin/course")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "Endpoints to manage Course Service")
public class CourseController {
  private final CourseService courseService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "GET All Available Courses in DB", response = Course[].class)
  public ResponseEntity<Iterable<Course>> list(Pageable pageable) {
    return new ResponseEntity<>(courseService.list(pageable), HttpStatus.OK);
  }
}
