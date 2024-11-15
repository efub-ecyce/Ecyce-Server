package com.ecyce.karma.domain.auth.jwt;

import com.ecyce.karma.global.exception.CustomException;
import com.ecyce.karma.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.io.Decoders;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME =1000*60*30; //30분
    private static final long REFRESH_TOKEN_EXPIRE_IN = 1000 * 60 * 60*24 * 14; // 14일


    private final Key key;

    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

    public JwtService(@Value("${spring.jwt.secret-key}") String secretKey , JwtRefreshTokenRepository jwtRefreshTokenRepository){
        this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /* JWT Token 생성 */
    public JwtTokens generateJwtToken(Long userId){
        long now = (new Date()).getTime();
        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_IN);
        String subject = userId.toString();
        String accessToken = generateToken(subject , accessTokenExpiredAt);
        String refreshToken = generateToken(subject , refreshTokenExpiredAt);

        /* 리프레시 토큰 저장 (DB)*/
        JwtRefreshToken jwtRefreshToken = JwtRefreshToken.builder()
                .refreshToken(refreshToken)
                .userId(userId)
                .expiresIn(REFRESH_TOKEN_EXPIRE_IN)
                .build();
        if(jwtRefreshTokenRepository.findByUserId(userId) == null){ // 신규 회원일 경우
            jwtRefreshTokenRepository.save(jwtRefreshToken);
            return JwtTokens.of(accessToken ,refreshToken ,BEARER_TYPE , ACCESS_TOKEN_EXPIRE_TIME , true);
        }
        else{ // 기존 회원인 경우
            updateRefreshToken(userId , refreshToken);
            return JwtTokens.of(accessToken ,refreshToken, BEARER_TYPE , ACCESS_TOKEN_EXPIRE_TIME , false);

        }
    }

    /* 토큰 생성 */
    public String generateToken(String subject , Date expiredAt){
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiredAt)
                .signWith(key , SignatureAlgorithm.HS256)
                .compact();
    }

    /* 토큰에서 subject(userId) 추출 */
    public String extractSubject(String accessToken){
        Claims claims = parseClaims(accessToken);
        return claims.getSubject();
    }

    /* subject 추출 후 Long 타입의 userId 반환 */
    public Long extractUserId(String accessToken){
        return Long.valueOf(extractSubject(accessToken));
    }

    /*클레임 파싱*/
    private Claims parseClaims(String accessToken){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    /* 액세스 토큰 검증 */
    public String validateToken(String accessToken) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
            if (!claims.getBody().getExpiration().before(new Date())){ // 만료일이 현재 날짜 이전인지 확인
                return "IS_VALID";
            }
        } catch (ExpiredJwtException e) {
            log.info(String.valueOf(ErrorCode.TOKEN_EXPIRED)); // 만료된 토큰
            return "TOKEN_EXPIRED";
        } catch (JwtException e) {
            log.info(String.valueOf(ErrorCode.INVALID_TOKEN)); // 유효하지 않는 토큰
            return "INVALID_TOKEN";
        } catch (Exception e) {
            log.info(String.valueOf(ErrorCode.FAIL_AUTHENTICATION)); // 토큰 검증 실패
            return "FAIL_AUTHENTICATION";
        }
        return "FAIL_AUTHENTICATION";
    }

    // 요청에서 토큰 추출하기
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    /* 리프레시 토큰으로 액세스 토큰 재발급 */
    @Transactional
    public JwtTokens reissueAccessToken(HttpServletRequest request) {
        String Token = getTokenFromRequest(request);
        Long userId = extractUserId(Token);
        // accessToken으로 찾은 유저 아이디로 refreshToken 찾아 가져오기
        JwtRefreshToken jwtRefreshToken = jwtRefreshTokenRepository.findByUserId(userId);
        String refreshToken = jwtRefreshToken.getRefreshToken();
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.FAIL_AUTHENTICATION);
        }
        // refreshToken 유효성 검사
        if(!validateRefreshToken(jwtRefreshToken)){
            log.debug("유효하지 않은 refreshToken 입니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        // 액세스 토큰과 리프레시 토큰 재발급
        return generateJwtToken(userId);
    }

    /* 리프레시 토큰 검증 */
    public boolean validateRefreshToken(JwtRefreshToken jwtRefreshToken) {

        try {
            return new Date(jwtRefreshToken.getExpiresIn()).before(new Date());
        } catch (ExpiredJwtException e) {
            log.info(String.valueOf(ErrorCode.TOKEN_EXPIRED)); // 토큰 만료
        } catch (JwtException e) {
            log.info(String.valueOf(ErrorCode.INVALID_TOKEN)); // 유효하지 않는 토큰
        } catch (Exception e) {
            log.info(String.valueOf(ErrorCode.FAIL_AUTHENTICATION)); // 토큰 검증 실패
        }
        return false;
    }

    /* 리프레시 토큰 업데이트 */
    public void updateRefreshToken(Long userId, String refreshToken){
        JwtRefreshToken jwtRefreshToken = jwtRefreshTokenRepository.findByUserId(userId);
        jwtRefreshToken.updateJwtRefreshToken(refreshToken);
        jwtRefreshTokenRepository.save(jwtRefreshToken);
    }

}



