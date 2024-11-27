package com.ecyce.karma.domain.chat.dto;

import com.ecyce.karma.domain.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomDetailDto {
    private String chatId;
    private Long userId;
    private String nickname;
    private String content;
    private LocalDateTime timestamp;

    public static ChatRoomDetailDto from(ChatMessage chatMessage, String nickname) {
        return new ChatRoomDetailDto(
                chatMessage.getId(),
                chatMessage.getUserId(),
                nickname,
                chatMessage.getContent(),
                chatMessage.getTimestamp()
        );
    }
}
