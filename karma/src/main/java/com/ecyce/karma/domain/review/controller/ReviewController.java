package com.ecyce.karma.domain.review.controller;

import com.ecyce.karma.domain.auth.customAnnotation.AuthUser;
import com.ecyce.karma.domain.review.dto.ReviewDetailDto;
import com.ecyce.karma.domain.review.dto.ReviewRequestDto;
import com.ecyce.karma.domain.review.dto.ReviewResponseDto;
import com.ecyce.karma.domain.review.service.ReviewService;
import com.ecyce.karma.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /* 리뷰 생성 */
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@AuthUser User user,
                                                          @RequestPart("reviewRequestDto") ReviewRequestDto requestDto,
                                                          @RequestPart(value = "reviewImages", required = false) List<MultipartFile> reviewImages) {
        ReviewResponseDto responseDto = reviewService.create(user, requestDto, reviewImages);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /* 후기 상세 조회 */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDetailDto> getReview(@PathVariable("reviewId") Long reviewId) {
        ReviewDetailDto responseDto = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(responseDto);
    }

    /* 후기 삭제 */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@AuthUser User user, @PathVariable("reviewId") Long reviewId) {
        reviewService.delete(user, reviewId);
        return ResponseEntity.ok("후기가 성공적으로 삭제되었습니다.");
    }

}
