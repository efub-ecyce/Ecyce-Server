package com.ecyce.karma.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 유효하지 않습니다."),
    FAIL_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "로그인 후 이용 가능합니다."), // 로그인X 유저의 요청 OR 토큰 불일치
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."), // 엑세스 토큰 만료
    FAIL_AUTHORIZATION(HttpStatus.FORBIDDEN, "권한이 없는 요청입니다."), // 권한 없는 요청
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR"),// 예상치 못한 에러

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),

    // product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND , "해당하는 제품을 찾을 수 없습니다."),
    PRODUCT_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 옵션을 찾을 수 없습니다."),
    INVALID_ACCESS(HttpStatus.BAD_REQUEST , "해당 사용자는 제품 정보를 수정할 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST , "잘못된 요청입니다."),

    // bookmark
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "북마크가 존재하지 않습니다. 북마크를 등록한 후 취소할 수 있습니다."),

    // orders
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 주문에 접근 권한이 없습니다."),
    INVALID_ORDER_STATE(HttpStatus.BAD_REQUEST, "주문 상태를 변경할 수 없습니다."),
    INVOICE_NUMBER_REQUIRED(HttpStatus.BAD_REQUEST, "송장 번호는 필수입니다."),
    ORDER_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 구매가 확정된 주문입니다."),

    // review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다."),
    REVIEW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "리뷰는 해당 주문의 구매자만 작성하거나 삭제할 수 있습니다."),
    INVALID_REVIEW_STATE(HttpStatus.BAD_REQUEST, "리뷰는 '구매확정' 상태인 경우에만 작성할 수 있습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 주문에 대해 이미 리뷰가 작성되었습니다."),

    // s3
    S3_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "S3에서 파일을 찾을 수 없습니다."),
    S3_BUCKET_MISMATCH(HttpStatus.BAD_REQUEST, "S3 버킷 이름이 일치하지 않습니다."),
    S3_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드 중 오류가 발생했습니다."),
    S3_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제 중 오류가 발생했습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "지원되지 않는 파일 확장자입니다."),
    DUPLICATE_FILE(HttpStatus.BAD_REQUEST, "중복된 파일입니다."),
  
    // chat
    INVALID_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "헤더가 없거나 형식이 유효하지 않습니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "토큰의 userId와 메시지 송신자의 userId가 일치하지 않습니다."),
    INVALID_MESSAGE_PAYLOAD(HttpStatus.BAD_REQUEST, "메시지 검증 중 오류 발생");

    private final HttpStatus status;
    private final String message;
}
