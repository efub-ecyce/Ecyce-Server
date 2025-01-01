package com.ecyce.karma.domain.auth.controller;

import com.ecyce.karma.domain.auth.jwt.JwtService;
import com.ecyce.karma.domain.auth.jwt.JwtTokens;
import com.ecyce.karma.domain.auth.service.OAuthLoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/login/oauth2")
@Slf4j
public class OAuthController {

    private final OAuthLoginService oAuthLoginService;
    private final JwtService jwtService;

    // 인가 코드 파라미터로 받아서 ~ jwt 토큰 생성해 반환하는 메서드
    @ResponseBody
    @GetMapping("/kakao")
    public ResponseEntity<JwtTokens> kakaoCallback(@RequestParam(name="code") String code) {
       log.info("kakaoCallback 메서드 호출됨");
        return ResponseEntity.ok(oAuthLoginService.login(code));
    }

    // 리프레시 토큰으로 액세스 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<JwtTokens> reissueAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(jwtService.reissueAccessToken(request));
    }

    /* 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);

        if (token != null) {
            Long userId = jwtService.extractUserId(token);
            jwtService.logout(userId);
            return ResponseEntity.ok("로그아웃 되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("로그아웃 실패: 토큰이 없습니다.");
        }
    }

}
