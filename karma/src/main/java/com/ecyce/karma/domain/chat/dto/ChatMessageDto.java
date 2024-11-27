package com.ecyce.karma.domain.chat.dto;

import com.ecyce.karma.domain.chat.entity.ChatMessage;
import com.ecyce.karma.domain.chat.entity.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private String chatId;
    private Long roomId;
    private Long userId;
    private String nickname;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;

    // 엔티티를 DTO로 변환
    public static ChatMessageDto from(ChatMessage chatMessage, String nickname) {
        return ChatMessageDto.builder()
                .chatId(chatMessage.getId())
                .roomId(chatMessage.getRoomId())
                .userId(chatMessage.getUserId())
                .nickname(nickname)
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .timestamp(chatMessage.getTimestamp())
                .build();
    }
}
