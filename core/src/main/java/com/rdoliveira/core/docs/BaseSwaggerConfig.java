package com.rdoliveira.core.docs;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author rodrigodonizettio
 */
public class BaseSwaggerConfig {
  private final String basePackage;

  public BaseSwaggerConfig(String basePackage) {
    this.basePackage = basePackage;
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.basePackage(basePackage))
      .build()
      .apiInfo(metaData());
  }

  private ApiInfo metaData() {
    return new ApiInfoBuilder()
      .title("Rodrigo Donizetti de Oliveira - Swagger Title Example")
      .description("Rodrigo Donizetti de Oliveira - Swagger Description Example")
      .version("0.0.1")
      .contact(new Contact("Rodrigo Donizetti de Oliveira", "https://www.linkedin.com/in/rodrigodonizettideoliveira", "rodrigodonizettio@gmail.com"))
      .license("DevDojo Spring Microservices Course")
      .licenseUrl("http://devdojo.academy")
      .build();
  }
}
