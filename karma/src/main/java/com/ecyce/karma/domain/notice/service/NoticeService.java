package com.ecyce.karma.domain.notice.service;

import com.ecyce.karma.domain.notice.dto.NoticeResponseDto;
import com.ecyce.karma.domain.notice.repository.EmitterRepository;
import com.ecyce.karma.domain.order.entity.OrderState;
import com.ecyce.karma.domain.order.entity.Orders;
import com.ecyce.karma.domain.order.repository.OrdersRepository;
import com.ecyce.karma.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final OrdersRepository ordersRepository;
    private final EmitterRepository emitterRepository;

    private static final long DEFAULT_TIMEOUT = 60L*1000*60; // 1시간


    /* 사용자의 모든 알림 조회 */
    public List<NoticeResponseDto> getAllNotices(User user) {
        List <Orders> allOrders =  ordersRepository.findAllOrdersByUserId(user.getUserId());

        List<NoticeResponseDto> allNotices = allOrders.stream()
                .map(order -> createNotice(order, user))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return allNotices;
    }

    /* 알림 보낼  것 필터링*/
    public NoticeResponseDto createNotice(Orders order, User user) {
        if (OrderState.접수완료.equals(order.getOrderState()) && order.getSellerUser().getUserId().equals(user.getUserId())) {
            return NoticeResponseDto.toSeller(order, user);
        } else if (OrderState.주문거절.equals(order.getOrderState())) {
            return NoticeResponseDto.refuseOrders(order, user);
        } else if (OrderState.제작대기.equals(order.getOrderState())) {
            return NoticeResponseDto.acceptOrders(order, user);
        } else if (OrderState.배송중.equals(order.getOrderState())) {
            return NoticeResponseDto.beginDelivery(order, user);
        }
        return null;
    }

    /* 클라이언트가 구독을 위해 호출하는 메서드
    *
    * user - 구독하는 클라이언트 (처음에 연결 요청을 보내는 계정)
    * @param SseEmitter - 서버에서 보낸 이벤트 Emitter
    * */
    public SseEmitter subscribe(User user) {
        SseEmitter emitter = createEmitter(user.getUserId());
        sendToClient(user.getUserId(), "EventStream created. [userId =" + user.getUserId() + "]");
        return emitter;
    }

    /* 서버의 이벤트를 클라이언트에게 보내는 메서드
    * 다른 서비스 로직에서 이 메서드를 사용해 데이터를 Object event에 넣고 전송하면 된다
    *
    * user - 메세지를 전송할 사용자
    * @param event - 전송할 이벤트 객체
    * */
    public void notify(User user) {
        sendToClient(user.getUserId(), getAllNotices(user));
    }

    /*
    * 클라이언트에게 데이터를 전송
    *
    * @param userId - 데이터를 받을 사용자의 아이디
    * @param data - 전송할 데이터
    * */
    private void sendToClient(Long userId , Object data){
        SseEmitter emitter = emitterRepository.get(userId);
        if(emitter != null){
            try{
                emitter.send(SseEmitter.event().id(String.valueOf(userId)).name("sse_notice").data(data));
            }catch (IOException exception){
                emitterRepository.deleteById(userId);
                emitter.completeWithError(exception);
            }
        }
    }

    /* 사용자 아이디를 기반으로 이벤트 Emitter를 생성
    *
    * @param userId - 사용자 id
    * @param SseEmitter - 생성된 이벤트 Emitter
    *
    * */
    private SseEmitter createEmitter(Long userId){

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId , emitter);

        //Emitter가 완료될 때 ( 모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(userId));
        //Emitter가 타임아웃되었을 때 (지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(userId));

        return emitter;
    }



}
