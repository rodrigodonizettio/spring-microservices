package com.rdoliveira.auth.docs;

import com.rdoliveira.core.docs.BaseSwaggerConfig;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author rodrigodonizettio
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {
  public SwaggerConfig() {
    super("com.rdoliveira.auth.endpoint.controller");
  }

}
