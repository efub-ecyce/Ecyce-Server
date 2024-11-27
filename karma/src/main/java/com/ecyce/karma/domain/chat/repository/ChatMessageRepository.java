package com.ecyce.karma.domain.chat.repository;

import com.ecyce.karma.domain.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(Long roomId);
    ChatMessage findTopByRoomIdOrderByTimestampDesc(Long roomId);
}
