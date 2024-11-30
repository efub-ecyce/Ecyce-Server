package com.ecyce.karma.domain.bookmark.dto;

import com.ecyce.karma.domain.bookmark.entity.Bookmark;
import com.ecyce.karma.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkDto {
    private Long bookmarkId;
    private String productName;
    private int price;
    private int duration;
    private String productThumbnail;

    public BookmarkDto(Long bookmarkId, String productName, int price, int duration, String productThumbnail) {
        this.bookmarkId = bookmarkId;
        this.productName = productName;
        this.price = price;
        this.duration = duration;
        this.productThumbnail = productThumbnail;
    }

    public static BookmarkDto from(Bookmark bookmark) {
        Product product = bookmark.getProduct();
        String productThumbnail = null;
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            productThumbnail = product.getProductImages().get(0).getProductImgUrl();
        }
        return new BookmarkDto(
                bookmark.getBookmarkId(),
                bookmark.getProduct().getProductName(),
                bookmark.getProduct().getPrice(),
                bookmark.getProduct().getDuration(),
                productThumbnail
        );
    }
}

