package ji.vegan.backend.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.Password;
import ji.vegan.backend.bean.user.UserInfo;
import ji.vegan.backend.config.CustomConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final CustomConfig customConfig;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserInfo userInfo, ZonedDateTime issuedAt, ZonedDateTime expiration, Map<String, Object> extraClaims) {
        return generateToken(extraClaims, userInfo.getUserId(), issuedAt, expiration);
    }

    public String generateToken(String subject, ZonedDateTime issuedAt, ZonedDateTime expiration, Map<String, Object> extraClaims) {
        return generateToken(extraClaims, subject, issuedAt, expiration);
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    public boolean isRefreshTokenValid(String refreshToken, String accessToken) {
        Claims claims = extractAllClaims(refreshToken);
        String refreshTokenToAccessToken = (String) claims.get("accessToken");

        return accessToken.equals(refreshTokenToAccessToken);
    }

    public boolean isTokenValid(String token, UserInfo userInfo) {
        final String userName = extractUserName(token);
        return (userName.equals(userInfo.getUserId())) && !isTokenExpired(token);
    }

    public String createSignKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] hmacKey = new byte[32];
        secureRandom.nextBytes(hmacKey);

        return Encoders.BASE64.encode(hmacKey);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, String subject, ZonedDateTime issuedAt, ZonedDateTime expiration) {
        Password key = Keys.password("abcd".toCharArray());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(Date.from(issuedAt.toInstant()))
                .expiration(Date.from(expiration.toInstant()))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(customConfig.getSignKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
