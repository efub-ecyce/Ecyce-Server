package com.ecyce.karma.domain.chat.service;

import com.ecyce.karma.domain.chat.dto.ChatListResponseDto;
import com.ecyce.karma.domain.chat.dto.ChatMessageDto;
import com.ecyce.karma.domain.chat.entity.ChatMessage;
import com.ecyce.karma.domain.chat.entity.ChatRoom;
import com.ecyce.karma.domain.chat.repository.ChatMessageRepository;
import com.ecyce.karma.domain.chat.repository.ChatRoomRepository;
import com.ecyce.karma.domain.user.entity.User;
import com.ecyce.karma.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /* 메시지 저장 */
    public ChatMessage saveMessage(ChatMessageDto dto) {
        chatRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("INVALID_ROOM_ID"));

        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(dto.getRoomId())
                .userId(dto.getUserId())
                .content(dto.getContent())
                .type(dto.getType())
                .timestamp(LocalDateTime.now())
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    /* 메시지 조회 */
    @Transactional(readOnly = true)
    public ChatListResponseDto findMessagesByRoomId(Long roomId, User currentUser) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_ROOM_ID"));

        // 상대방 이름으로 방 이름 설정
        String roomName = chatRoom.getBuyer().equals(currentUser) ? chatRoom.getSeller().getNickname() : chatRoom.getBuyer().getNickname();

        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
        List<ChatMessageDto> messageDtos = messages.stream()
                .map(message -> {
                    // MySQL에서 닉네임 조회
                    String nickname = userRepository.findById(message.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"))
                            .getNickname();
                    return ChatMessageDto.from(message, nickname);
                })
                .collect(Collectors.toList());

        return ChatListResponseDto.of(chatRoom.getRoomId(), roomName, messageDtos);
    }
}
