package com.rdoliveira.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author rodrigodonizettio
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationUser implements AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;
  @NotNull(message = "The field 'username' is required")
  @Column(nullable = false)
  private String username;
  @NotNull(message = "The field 'password' is required")
  @Column(nullable = false)
  @ToString.Exclude
  private String password;
  @NotNull(message = "The field 'role' is required")
  @Column(nullable = false)
  @Builder.Default
  private String role = "USER";

  public ApplicationUser(@NotNull ApplicationUser applicationUser) {
    this.id = applicationUser.getId();
    this.username = applicationUser.getUsername();
    this.password = applicationUser.getPassword();
    this.role = applicationUser.getRole();
  }
}
