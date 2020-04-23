package com.rdoliveira.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import com.rdoliveira.core.model.ApplicationUser;
import com.rdoliveira.core.property.JwtConfiguration;
import com.rdoliveira.token.security.token.creator.TokenCreator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * @author rodrigodonizettio
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter  extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final JwtConfiguration jwtConfiguration;
  private final TokenCreator tokenCreator;

  @Override
  @SneakyThrows
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    log.info("Attempting Authentication . . .");
    ApplicationUser applicationUser = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);

    if(applicationUser == null) {
      throw new UsernameNotFoundException("Unable to retrieve the Username or Password");
    }
    log.info("Creating the authentication object for the user '{}' and calling UserDetailsServiceImpl loadByUsername", applicationUser.getUsername());
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(applicationUser.getUsername(), applicationUser.getPassword(), Collections.emptyList());
    usernamePasswordAuthenticationToken.setDetails(applicationUser);

    return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
  }

  @Override
  @SneakyThrows
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
    log.info("Authentication Successfully for the user '{}'. Generating the JWE Token", auth.getName());
    SignedJWT signedJwt = tokenCreator.createSignedJwt(auth);
    String encryptedToken = tokenCreator.encryptToken(signedJwt);
    log.info("Token was sucessfully generated. Adding it to the Response Header");
    response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN, " + jwtConfiguration.getHeader().getName());
    response.addHeader(jwtConfiguration.getHeader().getName(), jwtConfiguration.getHeader().getPrefix() + encryptedToken);
  }
}
