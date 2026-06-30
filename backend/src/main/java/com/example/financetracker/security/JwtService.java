package com.example.financetracker.security;

import com.example.financetracker.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final ObjectMapper mapper;
    private final byte[] secret;
    private final long expirationMs;

    public JwtService(ObjectMapper mapper, @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.mapper = mapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMs = expirationMs;
    }

    public String generate(User user) {
        try {
            long now = Instant.now().getEpochSecond();
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", user.getEmail());
            payload.put("role", user.getRole());
            payload.put("iat", now);
            payload.put("exp", now + expirationMs / 1000);
            String header = encode(mapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")));
            String claims = encode(mapper.writeValueAsBytes(payload));
            String unsigned = header + "." + claims;
            return unsigned + "." + encode(sign(unsigned));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not issue authentication token", ex);
        }
    }

    public String subject(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3)
                throw new IllegalArgumentException("Malformed token");
            byte[] actual = Base64.getUrlDecoder().decode(parts[2]);
            if (!java.security.MessageDigest.isEqual(sign(parts[0] + "." + parts[1]), actual))
                throw new IllegalArgumentException("Invalid signature");
            Map<String, Object> claims = mapper.readValue(Base64.getUrlDecoder().decode(parts[1]),
                    new TypeReference<>() {
                    });
            long exp = ((Number) claims.get("exp")).longValue();
            if (Instant.now().getEpochSecond() >= exp)
                throw new IllegalArgumentException("Expired token");
            return (String) claims.get("sub");
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid token", ex);
        }
    }

    private byte[] sign(String content) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}
