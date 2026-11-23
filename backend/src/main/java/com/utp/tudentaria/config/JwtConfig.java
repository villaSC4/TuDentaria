package com.utp.tudentaria.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

@Configuration
public class JwtConfig {
    @Bean
    SecretKey jwtKey(@Value("${app.jwt.secret:}") String configured) {
        byte[] bytes;
        if (configured.isBlank()) {
            // Desarrollo: clave efímera. Los tokens caducan al reiniciar el backend.
            bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
        } else {
            bytes = configured.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < 32) throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 bytes.");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey jwtKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtKey).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("tudentaria"));
        return decoder;
    }
}
