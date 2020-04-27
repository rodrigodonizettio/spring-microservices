package com.rdoliveira.auth.endpoint.controller;

import com.rdoliveira.core.model.ApplicationUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "Endpoints to manage User Information Service")
public class UserInfoController {
  @GetMapping(path = "info", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "GET All Available User Information in DB that is related in the Auth. Token", response = ApplicationUser.class)
  public ResponseEntity<ApplicationUser> getUserInfo(Principal principal) {
    ApplicationUser applicationUser = (ApplicationUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    return new ResponseEntity<>(applicationUser, HttpStatus.OK);
  }
}
