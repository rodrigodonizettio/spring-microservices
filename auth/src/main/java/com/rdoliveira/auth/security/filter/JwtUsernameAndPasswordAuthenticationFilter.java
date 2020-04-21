package com.rdoliveira.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rdoliveira.core.model.ApplicationUser;
import com.rdoliveira.core.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author rodrigodonizettio
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter  extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final JwtConfiguration jwtConfiguration;

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
    SignedJWT signedJwt = createSignedJwt(auth);
    String encryptedToken = encryptToken(signedJwt);
    log.info("Token was sucessfully generated. Adding it to the Response Header");
    response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN, " + jwtConfiguration.getHeader().getName());
    response.addHeader(jwtConfiguration.getHeader().getName(), jwtConfiguration.getHeader().getPrefix() + encryptedToken);
  }

  @SneakyThrows
  private SignedJWT createSignedJwt(Authentication auth) {
    log.info("Starting to create the Signed JWT");
    ApplicationUser applicationUser = (ApplicationUser) auth.getPrincipal();
    JWTClaimsSet jwtClaimSet = createJwtClaimSet(auth, applicationUser);
    KeyPair rsaKey = generateKeyPair();
    log.info("Building JWT from RSA Key");
    JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKey.getPublic()).keyID(UUID.randomUUID().toString()).build();
    SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
            .jwk(jwk)
            .type(JOSEObjectType.JWT)
            .build(), jwtClaimSet);
    log.info("Signing the Token with the Private RSA Key");
    RSASSASigner signer = new RSASSASigner(rsaKey.getPrivate());
    signedJWT.sign(signer);
    log.info("Serialized Token '{}'", signedJWT.serialize());

    return signedJWT;
  }

  @SneakyThrows
  private KeyPair generateKeyPair() {
    log.info("Generating RSA 2048 bits key");
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    return generator.genKeyPair();
  }

  private JWTClaimsSet createJwtClaimSet(Authentication auth, ApplicationUser applicationUser) {
    log.info("Creating the JwtClaimSet Object for '{}' ", applicationUser);
    return new JWTClaimsSet.Builder()
            .subject(applicationUser.getUsername())
            .claim("authorities", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .issuer("http://com.rdoliveira")
            .issueTime(new Date())
            .expirationTime(new Date(System.currentTimeMillis() + (jwtConfiguration.getExpiration() * 1000)))
            .build();
  }

  private String encryptToken(SignedJWT signedJWT) throws JOSEException {
    log.info("Starting EncryptToken method");
    DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());
    JWEObject jweObject = new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
            .contentType("JWT")
            .build(), new Payload(signedJWT));
    log.info("Encrypting Token with System's Private Key");
    jweObject.encrypt(directEncrypter);
    log.info("Token was encrypted!");

    return jweObject.serialize();


  }
}
