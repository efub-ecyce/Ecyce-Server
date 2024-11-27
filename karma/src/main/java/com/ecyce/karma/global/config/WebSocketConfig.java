package com.ecyce.karma.global.config;

import com.ecyce.karma.global.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // /ws으로 들어오는 경우
                .setAllowedOrigins("https://jiangxy.github.io", "http://localhost:3000", "https://api.ecyce-karma.n-e.kr", "ws://localhost:8080", "wss://api.ecyce-karma.n-e.kr"),
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메모리 기반 메시지 브로커가 해당 api 구독 클라이언트에게 메시지 전달
        registry.enableSimpleBroker("/sub"); //구독 요청(어디서 받는지)
        registry.setApplicationDestinationPrefixes("/pub"); //메시지 발행 시 사용(어디로 보낼지)
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
