package com.rdoliveira.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author rodrigodonizettio
 */

@SpringBootApplication
@EntityScan({"com.rdoliveira.core.model"})
@EnableJpaRepositories({"com.rdoliveira.core.repository"})
public class CourseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseApplication.class, args);
	}

}
