package com.ecyce.karma.global.handler;

import com.ecyce.karma.domain.auth.jwt.JwtService;
import com.ecyce.karma.global.exception.CustomException;
import com.ecyce.karma.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // websocket 연결, 채팅 메시지 전송 시 토큰 검증
        if (accessor.getCommand() == StompCommand.CONNECT || accessor.getCommand() == StompCommand.SEND) {
            String accessToken = accessor.getFirstNativeHeader("Authorization");

            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                log.error("Invalid Authorization Header");
                throw new CustomException(ErrorCode.INVALID_AUTHORIZATION_HEADER);
            }

            accessToken = accessToken.substring(7); // Bearer 제거 목적
            String validation = jwtService.validateToken(accessToken); // 토큰 유효성 검증

            if (!"IS_VALID".equals(validation)) {
                handleInvalidToken(validation);
            }

            Long userId = jwtService.extractUserId(accessToken);

            if (accessor.getCommand() == StompCommand.SEND) {
                validateMessageUserId(message, userId);
            }

            log.info("인증된 userId: {}", userId);
        }
        return message;
    }

    // 채팅 메시지의 userId와 토큰 userId 일치 여부 확인
    private void validateMessageUserId(Message<?> message, Long userId) {
        try {
            String payload = new String((byte[]) message.getPayload());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);

            Long messageUserId = jsonNode.get("userId").asLong();

            if (!userId.equals(messageUserId)) {
                log.error("Unauthorized: 토큰의 userId와 메시지 송신자의 userId가 일치하지 않습니다.");
                throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
            }
        } catch (Exception e) {
            log.error("메시지 검증 중 오류 발생: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_MESSAGE_PAYLOAD);
        }
    }


    private void handleInvalidToken(String validation) {
        switch (validation) {
            case "TOKEN_EXPIRED":
                log.error("토큰이 만료되었습니다.");
                throw new CustomException(ErrorCode.TOKEN_EXPIRED);
            case "INVALID_TOKEN":
                log.error("유효하지 않은 토큰입니다.");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            case "FAIL_AUTHENTICATION":
            default:
                log.error("토큰 인증에 실패했습니다.");
                throw new CustomException(ErrorCode.FAIL_AUTHENTICATION);
        }
    }
}