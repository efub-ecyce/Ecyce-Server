package com.ecyce.karma.domain.notice.controller;

import com.ecyce.karma.domain.auth.customAnnotation.AuthUser;
import com.ecyce.karma.domain.notice.dto.NoticeResponseDto;
import com.ecyce.karma.domain.notice.service.NoticeService;
import com.ecyce.karma.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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



     /* 클라이언트에서  구독 */
    @GetMapping(value = "/subscribe" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthUser User user){
        return noticeService.subscribe(user);
    }
    

    /* 서버에서 클라이언트로 알림 */
    @PostMapping("/send-notice")
    public void sendData(@AuthUser User user){
        noticeService.notify(user);
    }

}
