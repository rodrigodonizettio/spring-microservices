package com.rdoliveira.gateway.security.config;

import com.rdoliveira.core.property.JwtConfiguration;
import com.rdoliveira.gateway.security.filter.GatewayJwtAuthorizationFilter;
import com.rdoliveira.token.security.config.SecurityTokenConfig;
import com.rdoliveira.token.security.token.converter.TokenConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author rodrigodonizettio
 */
@EnableWebSecurity
public class SecurityConfig extends SecurityTokenConfig {
  private final TokenConverter tokenConverter;

  public SecurityConfig(JwtConfiguration jwtConfiguration, TokenConverter tokenConverter) {
    super(jwtConfiguration);
    this.tokenConverter = tokenConverter;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.addFilterAfter(new GatewayJwtAuthorizationFilter(jwtConfiguration, tokenConverter), UsernamePasswordAuthenticationFilter.class);
    super.configure(http);
  }
}
