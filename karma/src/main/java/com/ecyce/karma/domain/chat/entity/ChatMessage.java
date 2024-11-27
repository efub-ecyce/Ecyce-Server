package com.ecyce.karma.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Document(collection = "chatMessage")
public class ChatMessage {
    @Id
    private String id;
    private Long roomId;
    private Long userId;
    private String content;
    private MessageType type; // 메시지 타입 (CHAT, JOIN, LEAVE)
    private LocalDateTime timestamp;

    @Builder
    public ChatMessage(String id, Long roomId, Long userId, String content, MessageType type, LocalDateTime timestamp) {
        this.id = id;
        this.roomId = roomId;
        this.userId = userId;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }
}