package com.ecyce.karma.domain.review.dto;

import com.ecyce.karma.domain.product.entity.Product;
import com.ecyce.karma.domain.product.entity.ProductOption;
import com.ecyce.karma.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
    private Long reviewId;
    private String reviewerName;
    private Long productId;
    private String productName;
    private String productThumbnail;
    private String productOption;
    private String content;
    private Integer rating;
    private List<ReviewImageResponseDto> reviewImages;

    public static ReviewResponseDto from(Review review) {
        Product product = review.getOrders().getProduct();
        ProductOption productOption = review.getOrders().getProductOption();
        String productThumbnail = null;
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            productThumbnail = product.getProductImages().get(0).getProductImgUrl();
        }


        List<ReviewImageResponseDto> images = review.getReviewImages().stream()
                .distinct()
                .map(image -> new ReviewImageResponseDto(image.getReviewImageId(), image.getImageUrl()))
                .toList();

        return new ReviewResponseDto(
                review.getReviewId(),
                review.getUser().getNickname(),
                product.getProductId(),
                product.getProductName(),
                productThumbnail,
                productOption.getOptionName(),
                review.getContent(),
                review.getRating(),
                images
        );
    }
}
