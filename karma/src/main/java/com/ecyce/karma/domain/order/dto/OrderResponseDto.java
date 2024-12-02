package com.ecyce.karma.domain.order.dto;

import com.ecyce.karma.domain.order.entity.Orders;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    /**주문 상세 정보 조회*/
    // 주문 정보
    private Long orderId;
    private String productThumbnail;
    private String productName;
    private String productOption;
    private Integer orderCount;
    private String orderState;
    private String request;
    private LocalDateTime createdAt;

    // 안내 사항
    private String materialInfo;
    private String buyerNotice;

    // 판매자 정보
    private Long sellerId;
    private String sellerNickname;
    private String sellerName;
    private String sellerPhone;
    private String sellerAddress;

    // 구매자 정보
    private Long buyerId;
    private String buyerNickname;
    private String buyerName;
    private String buyerPhone;
    private String buyerAddress;

    // 배송 정보
    private String deliveryCompany;
    private String invoiceNumber;

    // 결제 정보
    private Integer price;
    private Integer deliveryFee;
    private Integer totalPrice;

    public static OrderResponseDto from(Orders order) {
        // 상품 이미지 가져오기
        String productThumbnail = null;
        if (order.getProduct().getProductImages() != null && !order.getProduct().getProductImages().isEmpty()) {
            productThumbnail = order.getProduct().getProductImages().get(0).getProductImgUrl();
        }
        return new OrderResponseDto(
                // 주문 정보
                order.getOrderId(),
                productThumbnail,
                order.getProduct().getProductName(),
                order.getProductOption().getOptionName(),
                order.getOrderCount(),
                order.getOrderState().name(),
                order.getRequest(),
                order.getCreatedAt(),
                // 안내 사항
                order.getProduct().getMaterialInfo(),
                order.getProduct().getBuyerNotice(),
                // 판매자 정보
                order.getSellerUser().getUserId(),
                order.getSellerUser().getNickname(),
                order.getSellerUser().getName(),
                order.getSellerUser().getPhoneNumber(),
                order.getSellerUser().getAddress().toString(),
                // 구매자 정보
                order.getBuyerUser().getUserId(),
                order.getBuyerUser().getNickname(),
                order.getBuyerUser().getName(),
                order.getBuyerUser().getPhoneNumber(),
                order.getBuyerUser().getAddress().toString(),
                // 배송 정보
                order.getDeliveryCompany() != null ? order.getDeliveryCompany() : "택배사 미발행",
                order.getInvoiceNumber() != null ? order.getInvoiceNumber() : "송장번호 미발행",
                // 결제 정보
                (order.getProduct()
                     .getPrice()+order.getProductOption().getOptionPrice())* order.getOrderCount(),
                order.getProduct().getDeliveryFee(),
                order.getPay().getPayAmount()
        );
    }
}
