package com.rdoliveira.auth.security.config;

import com.rdoliveira.auth.security.filter.JwtUsernameAndPasswordAuthenticationFilter;
import com.rdoliveira.core.property.JwtConfiguration;
import com.rdoliveira.token.security.config.SecurityTokenConfig;
import com.rdoliveira.token.security.token.creator.TokenCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author rodrigodonizettio
 */
@EnableWebSecurity
public class SecurityCredentialsConfig extends SecurityTokenConfig {
  private final UserDetailsService userDetailsService;
  private final TokenCreator tokenCreator;

  public SecurityCredentialsConfig(JwtConfiguration jwtConfiguration, @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
      TokenCreator tokenCreator) {
    super(jwtConfiguration);
    this.userDetailsService = userDetailsService;
    this.tokenCreator = tokenCreator;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
              .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfiguration, tokenCreator));
    super.configure(http);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  private BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
