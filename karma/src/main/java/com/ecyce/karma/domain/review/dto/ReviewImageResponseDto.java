package com.ecyce.karma.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewImageResponseDto {
    private Long imageId;
    private String imageUrl;
}
