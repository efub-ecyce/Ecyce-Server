package com.ecyce.karma.domain.order.service;

import com.ecyce.karma.domain.order.dto.OrderCreateRequestDto;
import com.ecyce.karma.domain.order.dto.OrderCreateResponseDto;
import com.ecyce.karma.domain.order.dto.OrderOverviewResponseDto;
import com.ecyce.karma.domain.order.dto.OrderResponseDto;
import com.ecyce.karma.domain.order.entity.OrderState;
import com.ecyce.karma.domain.order.entity.OrderStatusChangedEvent;
import com.ecyce.karma.domain.order.entity.Orders;
import com.ecyce.karma.domain.order.repository.OrdersRepository;
import com.ecyce.karma.domain.product.entity.Product;
import com.ecyce.karma.domain.product.entity.ProductOption;
import com.ecyce.karma.domain.product.repository.ProductOptionRepository;
import com.ecyce.karma.domain.product.repository.ProductRepository;
import com.ecyce.karma.domain.user.entity.User;
import com.ecyce.karma.domain.user.repository.UserRepository;
import com.ecyce.karma.global.exception.CustomException;
import com.ecyce.karma.global.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdersService {
    private final OrdersRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 주문 생성
    public OrderCreateResponseDto createOrder(OrderCreateRequestDto requestDto, User buyer) {
        Product product = productRepository.findById(requestDto.getProductId())
                                           .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        ProductOption productOption = productOptionRepository.findById(requestDto.getProductOptionId())
                                                       .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_OPTION_NOT_FOUND));
        User seller = product.getUser();
        Orders order = Orders.createOrder(requestDto.getRequest(), seller, buyer, product, productOption,
                requestDto.getOrderCount());

        Orders savedOrder = orderRepository.save(order);
        // 이벤트 호출
        updateOrderStatus(savedOrder.getOrderId(),  OrderState.접수완료);

        return OrderCreateResponseDto.from(savedOrder);
    }

    // 주문 단건 조회 (판매자, 구매자 동시 접근 가능)
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId, User user) {
        // 주문 존재 여부 확인
        Orders order = orderRepository.findByOrderId(orderId)
                                      .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 권한 확인
        if (!order.getBuyerUser().getUserId().equals(user.getUserId()) &&
                !order.getSellerUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        return OrderResponseDto.from(order);
    }

    // 구매 내역 조회 (특정 구매자 기준)
    @Transactional(readOnly = true)
    public List<OrderOverviewResponseDto> getAllOrdersByBuyer(User user) {
        List<Orders> orders = orderRepository.findAllByBuyerUserId(user.getUserId());
//        if (orders.isEmpty()) {
//            throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
//        }
        return orders.stream()
                     .map(OrderOverviewResponseDto::fromEntity)
                     .collect(Collectors.toList());
    }

    // 판매 내역 조회 (특정 판매자 기준)
    @Transactional(readOnly = true)
    public List<OrderOverviewResponseDto> getAllOrdersBySeller(User user) {
        List<Orders> orders = orderRepository.findAllBySellerUserId(user.getUserId());
//        if (orders.isEmpty()) {
//            throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
//        }
        return orders.stream()
                     .map(OrderOverviewResponseDto::fromEntity)
                     .collect(Collectors.toList());
    }

    // 주문 수락 또는 거절
    public void acceptOrRejectOrder(Long orderId, User seller, boolean accepted) {
        Orders order = orderRepository.findByOrderId(orderId)
                                       .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getSellerUser().equals(seller)) {
            throw new CustomException(ErrorCode.ORDER_ACCESS_DENIED);
        }
        order.acceptOrReject(accepted);
        // 이벤트 호출
        if(accepted == false){
            updateOrderStatus(order.getOrderId(),  OrderState.주문거절);
        }
        else updateOrderStatus(order.getOrderId(),  OrderState.제작대기);
    }

    // 제작 시작
    public void startProduction(Long orderId, User seller) {
        Orders order = orderRepository.findByOrderId(orderId)
                                       .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getSellerUser().equals(seller)) {
            throw new CustomException(ErrorCode.ORDER_ACCESS_DENIED);
        }
        order.startProduction();
        // 이벤트 호출
        updateOrderStatus(order.getOrderId(),  OrderState.제작중);
    }

    // 제작 완료
    public void completeProduction(Long orderId, User seller) {
        Orders order = orderRepository.findByOrderId(orderId)
                                       .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getSellerUser().equals(seller)) {
            throw new CustomException(ErrorCode.ORDER_ACCESS_DENIED);
        }
        order.completeProduction();
        // 이벤트 호출
        updateOrderStatus(order.getOrderId(),  OrderState.제작완료);
    }

    // 배송 시작
    public void startShipping(Long orderId, User seller, String deliveryCompany,String invoiceNumber) {
        Orders order = orderRepository.findByOrderId(orderId)
                                       .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getSellerUser().equals(seller)) {
            throw new CustomException(ErrorCode.ORDER_ACCESS_DENIED);
        }
        order.startShipping(deliveryCompany,invoiceNumber);
        // 이벤트 호출
        updateOrderStatus(order.getOrderId(),  OrderState.배송중);
    }

    // 구매 확정
    public void confirmOrder(Long orderId, User buyer) {
        Orders order = orderRepository.findByOrderId(orderId)
                                       .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getBuyerUser().equals(buyer)) {
            throw new CustomException(ErrorCode.ORDER_ACCESS_DENIED);
        }
        order.confirmOrder();
    }

    // 주문 취소 (구매자, 판매자 모두)
    public void cancelOrder(Long orderId, Long userId) {
        Orders order = orderRepository.findByOrderId(orderId)
                                      .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getBuyerUser().getUserId().equals(userId) && !order.getSellerUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        order.cancelOrder();
        // 이벤트 호출
        updateOrderStatus(order.getOrderId(),  OrderState.주문취소);
    }

    /* 주문상태 변경 이벤트 리스너 */
    private void updateOrderStatus(Long orderId, OrderState newStatus) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        order.changeOrderState(newStatus);
        orderRepository.save(order);

        // 상태 변경 이벤트 발행
        eventPublisher.publishEvent(new OrderStatusChangedEvent(order.getBuyerUser(), order));
    }
}
