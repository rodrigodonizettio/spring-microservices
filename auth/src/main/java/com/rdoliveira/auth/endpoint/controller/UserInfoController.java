package com.rdoliveira.auth.endpoint.controller;

import com.rdoliveira.core.model.ApplicationUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author rodrigodonizettio
 */
@RestController
@RequestMapping("user")
public class UserInfoController {
  @GetMapping(path = "info", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApplicationUser> getUserInfo(Principal principal) {
    ApplicationUser applicationUser = (ApplicationUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    return new ResponseEntity<>(applicationUser, HttpStatus.OK);
  }
}
