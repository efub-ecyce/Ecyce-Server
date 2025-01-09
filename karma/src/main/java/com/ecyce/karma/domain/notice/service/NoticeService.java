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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final OrdersRepository ordersRepository;
    private final EmitterRepository emitterRepository;

    private static final long DEFAULT_TIMEOUT = 60L*1000*60;



    /* 사용자의 모든 알림 조회 */
    public List<NoticeResponseDto> getAllNotices(User user) {
        List <Orders> findAllAsSeller =  ordersRepository.findAllBySellerUserId(user.getUserId());
        List<Orders> findAllAsBuyer = ordersRepository.findAllByBuyerUserId(user.getUserId());

        List<NoticeResponseDto> sellerNotices = findAllAsSeller.stream()
                .filter(order -> OrderState.접수완료.equals(order.getOrderState()))
                        .map(order -> NoticeResponseDto.toSeller(order , user))
                        .collect(Collectors.toList());

        List<NoticeResponseDto> refuseBuyerNotices = findAllAsBuyer.stream()
                .filter(order -> OrderState.주문거절.equals(order.getOrderState()))
                .map( order -> NoticeResponseDto.refuseOrders(order , user))
                .collect(Collectors.toList());

        List<NoticeResponseDto> acceptBuyerNotices = findAllAsBuyer.stream()
                .filter(order -> OrderState.제작대기.equals(order.getOrderState()))
                .map( order -> NoticeResponseDto.acceptOrders(order , user))
                .collect(Collectors.toList());

        List<NoticeResponseDto> deliveryNotices = findAllAsBuyer.stream()
                .filter(order ->OrderState.배송중.equals(order.getOrderState()))
                .map(order -> NoticeResponseDto.beginDelivery(order , user))
                .collect(Collectors.toList());

        List<NoticeResponseDto> allNotices = new ArrayList<>();
        allNotices.addAll(sellerNotices);
        allNotices.addAll(refuseBuyerNotices);
        allNotices.addAll(acceptBuyerNotices);
        allNotices.addAll(deliveryNotices);

        return allNotices;
    }

    /* 클라이언트가 구독을 위해 호출하는 메서드
    *
    * @param id - 구독ㅎ는 클라이언트의 사용자 아이디
    * @param SseEmitter - 서버에서 보낸 이벤트 Emitter
    * */
    public SseEmitter subscribe(User user) {
        Long userId = user.getUserId();
        SseEmitter emitter = createEmitter(userId);
        log.info("subscribe 메서드 호출");

        sendToClient(userId, "EventStream created. [userId =" + userId + "]");
        return emitter;
    }

    /* 서버의 이벤트를 클라이언트에게 보내는 메서드
    * 다른 서비스 로직에서 이 메서드를 사용해 데이터를 Object event에 넣고 전송하면 된다
    *
    * @param id - 메세지를 전송할 사용자의 id
    * @param event - 전송할 이벤트 객체
    * */
    public void notify(User user, String data) {
        Long userId = user.getUserId();
        sendToClient(userId , data);
    }

    /*
    * 클라이언트에게 데이터를 전송
    *
    * @param id - 데이터를 받을 사용자의 아이디
    * @param data - 전송할 데이터
    * */
    private void sendToClient(Long id , String data){
        SseEmitter emitter = emitterRepository.get(id);
        if(emitter != null){
            try{
                emitter.send(SseEmitter.event().id(String.valueOf(id)).name("sse").data(data));
            }catch (IOException exception){
                emitterRepository.deleteById(id);
                emitter.completeWithError(exception);
            }
        }
    }

    /* 사용자 아이디를 기반으로 이벤트 Emitter를 생성
    *
    * @param id - 사용자 id
    * @param SseEmitter - 생성된 이벤트 Emitter
    *
    * */
    private SseEmitter createEmitter(Long id){

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        log.info("emiiter create 호출");
        emitterRepository.save(id , emitter);

        //Emmiter가 완료될 때 ( 모든 데이터가 성공적으로 전송된 상태) Emiiter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        //Emmiter가 타임아웃되었을 때 (지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        return emitter;
    }
}
