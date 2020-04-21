package com.rdoliveira.core.repository;

import com.rdoliveira.core.model.ApplicationUser;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author rodrigodonizettio
 */
public interface ApplicationUserRepository extends PagingAndSortingRepository<ApplicationUser, Long> {
  ApplicationUser findByUsername(String username);
}
