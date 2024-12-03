package com.ecyce.karma.domain.review.dto;

import com.ecyce.karma.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailDto {
    private String reviewerName;
    private String userImage;
    private LocalDate reviewDate;
    private String content;
    private Integer rating;
    private List<ReviewImageResponseDto> reviewImages;

    public static ReviewDetailDto from(Review review) {
        List<ReviewImageResponseDto> images = review.getReviewImages().stream()
                .map(image -> new ReviewImageResponseDto(image.getReviewImageId(), image.getImageUrl()))
                .toList();

        return new ReviewDetailDto(
                review.getUser().getNickname(),
                review.getUser().getProfileImage(),
                review.getCreatedAt().toLocalDate(),
                review.getContent(),
                review.getRating(),
                images
        );
    }
}
