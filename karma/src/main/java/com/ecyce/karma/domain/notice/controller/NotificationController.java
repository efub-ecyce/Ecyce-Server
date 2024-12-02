package com.ecyce.karma.domain.notice.controller;

import com.ecyce.karma.domain.auth.customAnnotation.AuthUser;
import com.ecyce.karma.domain.notice.dto.NoticeResponseDto;
import com.ecyce.karma.domain.notice.service.NoticeService;
import com.ecyce.karma.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NotificationController {

    private final NoticeService noticeService;

    /* 알림 조회 */
     @GetMapping
     public ResponseEntity<List<NoticeResponseDto>> getAllNotice(@AuthUser User user){
         List<NoticeResponseDto> noticeResponses = noticeService.getAllNotices(user);

         return ResponseEntity.ok(noticeResponses);
     }

}
