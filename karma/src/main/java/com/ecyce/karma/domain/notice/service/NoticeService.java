package com.ecyce.karma.domain.notice.service;

import com.ecyce.karma.domain.notice.dto.NoticeResponseDto;
import com.ecyce.karma.domain.order.entity.OrderState;
import com.ecyce.karma.domain.order.entity.Orders;
import com.ecyce.karma.domain.order.repository.OrdersRepository;
import com.ecyce.karma.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final OrdersRepository ordersRepository;

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
                .filter(order -> OrderState.접수완료.equals(order.getOrderState()))
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
}
