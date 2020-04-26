package com.rdoliveira.token.security.token.converter;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.rdoliveira.core.property.JwtConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * @author rodrigodonizettio
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenConverter {
  private final JwtConfiguration jwtConfiguration;

  @SneakyThrows
  public String decryptToken(String encryptedToken) {
    log.info("Decrypting Token");
    JWEObject jweObject = JWEObject.parse(encryptedToken);
    DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
    jweObject.decrypt(directDecrypter);
    log.info("Token decrypted! Returning signed Token");
    return jweObject.getPayload().toSignedJWT().serialize();
  }

  @SneakyThrows
  public void validateTokenSignature(String signedToken) {
    log.info("Starting method validateTokenSignature");
    SignedJWT signedJWT = SignedJWT.parse(signedToken);
    log.info("Token parsed! Retrieving Public Key from Signed Token");
    RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
    log.info("Public Key retrieved! Validating signature");
    if(!signedJWT.verify(new RSASSAVerifier(publicKey))) {
      throw new AccessDeniedException("Invalid Token Signature");
    }
    log.info("The Token has a Valid Signature!");
  }
}
