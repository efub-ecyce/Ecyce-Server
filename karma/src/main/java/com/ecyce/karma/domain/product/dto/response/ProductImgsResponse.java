package com.ecyce.karma.domain.product.dto.response;

import com.ecyce.karma.domain.product.entity.ProductImage;

public record ProductImgsResponse(

        Long imgId,
        String productImgUrl

) {

    public static ProductImgsResponse from(ProductImage productImage){
        return new ProductImgsResponse(
                productImage.getProductImgId(),
                productImage.getProductImgUrl()
        );
    }
}
