package aurora.carevisionapiserver.global.auth.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.response.exception.GeneralException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes); // HMAC-SHA 키 생성
    }

    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            Date now = new Date();
            Date expiredDate = claims.getBody().getExpiration();
            return expiredDate.after(now);
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorStatus.AUTH_EXPIRED_TOKEN);
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.AUTH_INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new GeneralException(ErrorStatus.UNSUPPORTED_TOKEN);
        }
    }

    public Jws<Claims> getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
    }

    public String getId(String token) {
        return getClaims(token).getBody().get("username", String.class);
    }

    public String createJwt(String category, String username, Long expiredMs) {
        Claims claims = Jwts.claims();
        claims.put("username", username);
        claims.put("category", category);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(this.getSigningKey())
                .compact();
    }
}
