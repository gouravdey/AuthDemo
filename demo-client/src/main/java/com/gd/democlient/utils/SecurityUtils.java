package com.gd.democlient.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.netty.buffer.ByteBuf;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class SecurityUtils {

    public static String createClientAssertion(String clientId, String tokenEndpoint) throws Exception {

        Key privateKey = getPrivateKey();

        long currentTimeStamp = System.currentTimeMillis();
        Date issuedAtDate = new Date(currentTimeStamp);
        Date expirationDate = new Date(currentTimeStamp + 3600 * 1000);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer(clientId)
                .setAudience(tokenEndpoint)
                .setSubject(clientId)
                .setExpiration(expirationDate)
                .setIssuedAt(issuedAtDate)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private static Key getPrivateKey() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {

        String keystorePath = "demo-client/src/main/resources/cert/keystore-self.jks";
        String keystorePassword = "storepass";
        String alias = "gd-client-2";
        String keyPassword = "keypass";

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
        return keyStore.getKey(alias, keyPassword.toCharArray());
    }

    public static MultiValueMap<String, String> createTokenConfig(String clientId, String tokenEndpoint) throws Exception {
        MultiValueMap<String, String> tokenConfig = new LinkedMultiValueMap<>();

        tokenConfig.add("grant_type", "client_credentials");
        tokenConfig.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        tokenConfig.add("client_assertion", createClientAssertion(clientId, tokenEndpoint));

        return tokenConfig;
    }
}
