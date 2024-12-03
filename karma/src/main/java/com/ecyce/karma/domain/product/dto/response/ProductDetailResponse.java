package com.ecyce.karma.domain.product.dto.response;

import com.ecyce.karma.domain.product.entity.Product;
import com.ecyce.karma.domain.product.entity.ProductState;

import java.util.List;
import java.util.stream.Collectors;

public record ProductDetailResponse(
        Long productId, // 상품 Id
        Long userId, // 판매자 Id
        String sellerNickname,

        String sellerProfileImg,
        String productName, // 상품 이름
        boolean isMarked,
        int price, // 가격
        String content, // 제품 소개
        int duration, // 소요 기간
        double rating, // 평점
        ProductState productState, // 제품 공개 여부
        Integer deliveryFee, // 배송비
        String materialInfo, // 소재의 정보
        String materialExample,
        String buyerNotice, // 구매자 안내사항

        List<OptionResponse> options,
        List<ProductImgsResponse> imgs

) {
    public static ProductDetailResponse from(Product product , boolean isMarked){
        return new ProductDetailResponse(
                product.getProductId(),
                product.getUser().getUserId(),
                product.getUser().getNickname(),
                product.getUser().getProfileImage(),
                product.getProductName(),
                isMarked,
                product.getPrice(),
                product.getContent(),
                product.getDuration(),
                product.getRating(),
                product.getProductState(),
                product.getDeliveryFee(),
                product.getMaterialInfo(),
                product.getMaterialExample(),
                product.getBuyerNotice(),
                product.getOptions().stream()
                        .map(OptionResponse:: from )
                        .collect(Collectors.toList()),
                product.getProductImages().stream()
                        .map(ProductImgsResponse::from)
                        .collect(Collectors.toList())
        );
    }

    /* 제품 등록용 */
    public static ProductDetailResponse of(Product product , List<OptionResponse> options){
        return new ProductDetailResponse(
                product.getProductId(),
                product.getUser().getUserId(),
                product.getUser().getNickname(),
                product.getUser().getProfileImage(),
                product.getProductName(),
                false,
                product.getPrice(),
                product.getContent(),
                product.getDuration(),
                product.getRating(),
                product.getProductState(),
                product.getDeliveryFee(),
                product.getMaterialInfo(),
                product.getMaterialExample(),
                product.getBuyerNotice(),
                options,
                product.getProductImages().stream()
                        .map(ProductImgsResponse::from)
                        .collect(Collectors.toList())
        );
    }


}