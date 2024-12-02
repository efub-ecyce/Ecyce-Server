package com.ecyce.karma.domain.notice.dto;

import com.ecyce.karma.domain.order.entity.OrderState;
import com.ecyce.karma.domain.order.entity.Orders;
import com.ecyce.karma.domain.user.entity.User;

import java.time.LocalDateTime;

public record NoticeResponseDto(
        Long userId,
        Long orderId ,
        String productName,
        OrderState orderState,
        String noticeContent,
        LocalDateTime updatedAt

) {


    /* 알림 조회하는 사람이 판매자 , 주문 수락 요청*/
    public static NoticeResponseDto toSeller(Orders orders , User user){
        return new NoticeResponseDto(
                user.getUserId(),
                orders.getOrderId(),
                orders.getProduct().getProductName(),
                orders.getOrderState(),
                "주문이 들어왔습니다.",
                orders.getUpdatedAt()
        );

    }

    /* 알림 조회하는 사람이 판매자 ,주문 배송 시작 알림 */
    public static NoticeResponseDto beginDelivery(Orders orders , User user){
        return new NoticeResponseDto(
                user.getUserId(),
                orders.getOrderId(),
                orders.getProduct().getProductName(),
                orders.getOrderState(),
                "주문하신 제품의 배송이 시작되었습니다.",
                orders.getUpdatedAt()
        );
    }

    /* 알림 조회하는 사람이 구매자 , 거절 알림*/
    public static NoticeResponseDto refuseOrders(Orders orders , User user){
        return new NoticeResponseDto(
                user.getUserId(),
                orders.getOrderId(),
                orders.getProduct().getProductName(),
                orders.getOrderState(),
                "판매자가 주문을 거절했습니다.",
                orders.getUpdatedAt()
        );
    }

    /* 알림 조회하는 사람이 구매자 , 수락 알림 */
    public static NoticeResponseDto acceptOrders(Orders orders , User user){
        return new NoticeResponseDto(
                user.getUserId(),
                orders.getOrderId(),
                orders.getProduct().getProductName(),
                orders.getOrderState(),
                "판매자가 주문을 수락했습니다.",
                orders.getUpdatedAt()
        );
    }




}
