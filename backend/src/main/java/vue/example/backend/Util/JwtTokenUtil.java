package vue.example.backend.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Base64;

@Component
public class JwtTokenUtil {

    private String secret = Base64.getEncoder()
            .encodeToString("T#hi$1s@A_S3cR3tK3y_2024^With_M@ny!D!ff3r3nt^&Char$*And$Longer#Key".getBytes()); // Base64
                                                                                                              // 인코딩된
                                                                                                              // 시크릿 키

    // JWT에서 사용자 이름 또는 이메일, 전화번호 추출
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // JWT에서 발행 날짜 추출
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    // JWT에서 유효 기간 추출
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // JWT에서 클레임 추출
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // JWT에서 모든 클레임을 가져옴
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(secret)) // Base64 디코딩
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT 유효 기간 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // JWT 생성 (이메일, 전화번호, 사용자 이름, ID 기반)
    public String generateToken(String emailOrPhone, String username, String id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("emailOrPhone", emailOrPhone); // 이메일 또는 전화번호 추가
        claims.put("username", username); // 사용자 이름 추가
        claims.put("id", id); // 사용자 ID 추가

        return doGenerateToken(claims, emailOrPhone); // 주제(Subject)로 emailOrPhone을 설정
    }

    // JWT 생성 로직 (내부 메서드)
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // 클레임 설정
                .setSubject(subject) // 주제 설정
                .setIssuedAt(new Date(System.currentTimeMillis())) // 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000)) // 12시간 유효
                .signWith(SignatureAlgorithm.HS512, Base64.getDecoder().decode(secret)) // 서명
                .compact();
    }

    // JWT 유효성 확인
    public Boolean validateToken(String token, String usernameOrEmail) {
        final String username = getUsernameFromToken(token);
        return (username.equals(usernameOrEmail) && !isTokenExpired(token));
    }

    // JWT에서 사용자 ID 추출 메서드 추가
    public int getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token); // 토큰에서 모든 클레임 가져오기
        return Integer.parseInt(claims.get("id").toString()); // 클레임에서 사용자 ID 추출
    }
}
