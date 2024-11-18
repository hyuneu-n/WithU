package com.danpoong.withu.family.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class InviteTokenService {

    // 안전한 키 생성
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // 24시간 (밀리초 단위)
    private static final long EXPIRATION_TIME = 86400000L;

    public String generateInviteToken(Long groupId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        // 그룹 ID를 담은 JWT 토큰 생성
        return Jwts.builder()
                .setSubject(String.valueOf(groupId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public Long validateInviteToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("초대 토큰이 만료되었습니다.", e);
        } catch (JwtException e) {
            throw new RuntimeException("유효하지 않은 초대 토큰입니다.", e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("토큰에서 올바른 그룹 ID를 가져올 수 없습니다.", e);
        }
    }
}
