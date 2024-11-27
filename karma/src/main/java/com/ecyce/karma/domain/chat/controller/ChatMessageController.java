package com.ecyce.karma.domain.chat.controller;

import com.ecyce.karma.domain.auth.customAnnotation.AuthUser;
import com.ecyce.karma.domain.chat.dto.ChatListResponseDto;
import com.ecyce.karma.domain.chat.dto.ChatMessageDto;
import com.ecyce.karma.domain.chat.entity.ChatMessage;
import com.ecyce.karma.domain.chat.service.ChatMessageService;
import com.ecyce.karma.domain.user.entity.User;
import com.ecyce.karma.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class ChatMessageController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    /* 채팅 주고 받기 및 저장 */
    @MessageMapping("/chat/message") //pub/chat/message로 메시지를 보냄
    public void message(ChatMessageDto chatMessageDto) {
        ChatMessage savedMessage = chatMessageService.saveMessage(chatMessageDto);
        String nickname = userRepository.findById(savedMessage.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"))
                .getNickname();
        ChatMessageDto responseDto = ChatMessageDto.from(savedMessage, nickname);
        messagingTemplate.convertAndSend("/sub/chat/room/" + responseDto.getRoomId(), responseDto); // /sub/chat/room/{roomId}를 구독하는 이에게 보냄
    }

    /* 채팅방의 채팅 내역 조회 */
    @GetMapping("/chat/messages/{roomId}")
    public ResponseEntity<ChatListResponseDto> getMessagesByRoomId(@PathVariable("roomId") Long roomId, @AuthUser User user) {
        ChatListResponseDto response = chatMessageService.findMessagesByRoomId(roomId, user);
        return ResponseEntity.ok(response);
    }
}