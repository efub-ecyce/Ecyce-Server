package com.ecyce.karma.global.events;

import com.ecyce.karma.domain.notice.dto.NoticeResponseDto;
import com.ecyce.karma.domain.notice.repository.EmitterRepository;
import com.ecyce.karma.domain.notice.service.NoticeService;
import com.ecyce.karma.domain.order.entity.OrderStatusChangedEvent;
import com.ecyce.karma.domain.order.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final EmitterRepository emitterRepository;
    private final NoticeService noticeService;

    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        Long userId = event.user().getUserId();

        // 알림 데이터 생성 - 주문 상태 변경된 주문에 대한 정보
        NoticeResponseDto notice = noticeService.createNotice(event.orders() , event.user());

        // 클라이언트에게 알림 전송
        SseEmitter emitter = emitterRepository.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .name("order-status-changed")
                        .data(notice));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitterRepository.deleteById(userId);
            }
        }
    }
}