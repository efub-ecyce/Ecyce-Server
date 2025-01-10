package com.ecyce.karma.domain.order.entity;

import com.ecyce.karma.domain.pay.entity.Pay;
import com.ecyce.karma.domain.pay.entity.PayStatus;
import com.ecyce.karma.domain.product.entity.Product;
import com.ecyce.karma.domain.product.entity.ProductOption;
import com.ecyce.karma.domain.user.entity.User;
import com.ecyce.karma.global.entity.BaseTimeEntity;
import com.ecyce.karma.global.exception.CustomException;
import com.ecyce.karma.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId")
    private Long orderId;

    @Column(nullable = false)
    private String request;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState orderState;

    @Column(nullable = false)
    private Integer orderCount;

    @Column
    private String deliveryCompany; // 택배회사

    @Column
    private String invoiceNumber; // 송장번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellerId", updatable = false, nullable = false)
    private User sellerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyerId", updatable = false, nullable = false)
    private User buyerUser;

    @ManyToOne
    @JoinColumn(name = "productId" ,updatable = false, nullable = false)
    private Product product;

    @OneToOne(mappedBy = "orders" , cascade = CascadeType.ALL, orphanRemoval = true)
    private Pay pay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productOptionId", nullable = false)
    private ProductOption productOption;


    public Orders(String request , User sellerUser , User buyerUser , Product product, ProductOption productOption, Integer orderCount, Integer payAmount){
        this.request = request; // 유저 요구사항
        this.orderState = OrderState.접수완료; // 주문 진행 과정
        this.sellerUser = sellerUser; // 판매자
        this.buyerUser = buyerUser; // 구매자
        this.product = product; // 구매할 상품
        this.productOption = productOption; // 상품 옵션
        this.orderCount = orderCount; // 상품 개수
        // 결제 정보 생성
        this.pay = Pay.builder()
                      .orders(this)
                      .payAmount(payAmount)
                      .payStatus(PayStatus.결제성공) // 추후 결제 관련 로직 생성 필요
                      .build();
    }

    public static Orders createOrder(String request, User seller, User buyer, Product product, ProductOption productOption, Integer orderCount) {
        Integer payAmount = (product.getPrice() + productOption.getOptionPrice()) * orderCount + product.getDeliveryFee(); // 상품 가격 계산
        return new Orders(request, seller, buyer, product, productOption, orderCount, payAmount);
    }

    // 1. 주문 수락 또는 거절
    public void acceptOrReject(boolean accepted) {
        if (orderState != OrderState.접수완료) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATE, String.format("현재 상태는 '%s'이며, 접수 완료 상태에서만 수락하거나 거절할 수 있습니다.", orderState));
        }
        this.orderState = accepted ? OrderState.제작대기 : OrderState.주문거절;
    }

    // 2. 제작 시작
    public void startProduction() {
        if (orderState != OrderState.제작대기) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATE, String.format("현재 상태는 '%s'이며, 제작 대기 상태에서만 제작을 시작할 수 있습니다.", orderState));
        }
        this.orderState = OrderState.제작중;
    }

    // 3. 제작 완료
    public void completeProduction() {
        if (orderState != OrderState.제작중) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATE, String.format("현재 상태는 '%s'이며, 제작 중 상태에서만 제작을 완료할 수 있습니다.", orderState));
        }
        this.orderState = OrderState.제작완료;
    }

    // 4. 배송 시작 (송장번호 추가)
    public void startShipping(String deliveryCompany, String invoiceNumber) {
        if (orderState != OrderState.제작완료) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATE, String.format("현재 상태는 '%s'이며, 제작 완료 상태에서만 배송을 시작할 수 있습니다.", orderState));
        }
        if (invoiceNumber == null || invoiceNumber.isBlank()) {
            throw new CustomException(ErrorCode.INVOICE_NUMBER_REQUIRED);
        }
        this.deliveryCompany = deliveryCompany;
        this.invoiceNumber = invoiceNumber;
        this.orderState = OrderState.배송중;
    }

    // 5. 구매 확정
    public void confirmOrder() {
        if (orderState != OrderState.배송중) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATE, String.format("현재 상태는 '%s'이며, 배송 중 상태에서만 구매를 확정할 수 있습니다.", orderState));
        }
        this.orderState = OrderState.구매확정;
    }

    // 주문 취소
    public void cancelOrder() {
        if (orderState != OrderState.접수완료 && orderState != OrderState.제작대기) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATE, String.format("현재 상태는 '%s'이며, 접수 완료 및 제작 대기 상태에서만 주문을 취소할 수 있습니다.", orderState));
        }
        this.orderState = OrderState.주문취소;
    }

    /* 주문 상태 변경 */
    public void changeOrderState(OrderState newStatus){
        this.orderState = newStatus;
    }
}
