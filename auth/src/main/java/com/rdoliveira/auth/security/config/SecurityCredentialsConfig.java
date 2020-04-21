package com.rdoliveira.auth.security.config;

import com.rdoliveira.auth.security.filter.JwtUsernameAndPasswordAuthenticationFilter;
import com.rdoliveira.core.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletResponse;

/**
 * @author rodrigodonizettio
 */
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityCredentialsConfig extends WebSecurityConfigurerAdapter {
  private final UserDetailsService userDetailsService;
  private final JwtConfiguration jwtConfiguration;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            .csrf().disable()
            .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
            .and()
              .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
              .exceptionHandling().authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
              .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfiguration))
            .authorizeRequests()
              .antMatchers(jwtConfiguration.getLoginUrl()).permitAll()
              .antMatchers("/course/admin/**").hasRole("ADMIN")
              .anyRequest().authenticated();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  private BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
