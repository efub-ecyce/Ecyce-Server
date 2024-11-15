package com.ecyce.karma.domain.product.dto.response;

import com.ecyce.karma.domain.product.entity.Product;
import com.ecyce.karma.domain.product.entity.ProductState;

public record ProductSimpleResponse(
        Long productId,
        Long userId,
        String nickname,
        String productName,
        int price,
        int duration,
        ProductState productState,
        boolean isMarked
        // String thumbnail; // 나중에 썸네일 추가해야함 해당 product의 사진 중 첫번째 사진 반환
) {

    public static ProductSimpleResponse from(Product product , boolean isMarked){
        return new ProductSimpleResponse(
                product.getProductId(),
                product.getUser().getUserId(),
                product.getUser().getNickname(),
                product.getProductName(),
                product.getPrice(),
                product.getDuration(),
                product.getProductState(),
                isMarked
        );
    }
}
