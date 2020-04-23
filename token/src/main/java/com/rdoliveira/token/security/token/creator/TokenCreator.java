package com.rdoliveira.token.security.token.creator;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author rodrigodonizettio
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenCreator {
  private final JwtConfiguration jwtConfiguration;

  @SneakyThrows
  public SignedJWT createSignedJwt(Authentication auth) {
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

  @SneakyThrows
  private KeyPair generateKeyPair() {
    log.info("Generating RSA 2048 bits key");
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    return generator.genKeyPair();
  }

  public String encryptToken(SignedJWT signedJWT) throws JOSEException {
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
