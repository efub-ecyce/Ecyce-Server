package com.ecyce.karma.domain.chat.service;

import com.ecyce.karma.domain.chat.dto.ChatRoomDetailDto;
import com.ecyce.karma.domain.chat.dto.ChatRoomListDto;
import com.ecyce.karma.domain.chat.dto.ChatRoomResponseDto;
import com.ecyce.karma.domain.chat.entity.ChatMessage;
import com.ecyce.karma.domain.chat.entity.ChatRoom;
import com.ecyce.karma.domain.chat.repository.ChatMessageRepository;
import com.ecyce.karma.domain.chat.repository.ChatRoomRepository;
import com.ecyce.karma.domain.user.entity.User;
import com.ecyce.karma.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    /* 채팅방 생성 */
    public ChatRoomResponseDto createChatRoom(User creator, String otherUserName, boolean isOtherUserBuyer) {
        User otherUser = userRepository.findByNickname(otherUserName)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_OTHER_USER_NAME"));

        // buyer와 seller 설정
        User buyer = isOtherUserBuyer ? otherUser : creator;
        User seller = isOtherUserBuyer ? creator : otherUser;

        // 기존 채팅방 여부 확인
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByBuyerAndSeller(buyer, seller);
        if (existingChatRoom.isPresent()) {
            // 기존 채팅방 반환
            return ChatRoomResponseDto.from(existingChatRoom.get(), false);
        }

        // 새 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .buyer(buyer)
                .seller(seller)
                .name(otherUser.getNickname())
                .build();
        chatRoomRepository.save(chatRoom);

        return ChatRoomResponseDto.from(chatRoom, true);
    }

    /* 전체 채팅방 목록: 사용자가 buyer 또는 seller로 참여한 채팅방 */
    public List<ChatRoomListDto> findAllChatRooms(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByBuyerOrSeller(user, user);
        return mapToChatRoomListDto(chatRooms, user);
    }

    /* 판매 채팅방 목록: 사용자가 seller로 참여한 채팅방 */
    public List<ChatRoomListDto> findSellingChatRooms(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findBySeller(user); // 판매자 기준
        return mapToChatRoomListDto(chatRooms, user);
    }

    /* 구매 채팅방 목록: 사용자가 buyer로 참여한 채팅방 */
    public List<ChatRoomListDto> findBuyingChatRooms(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByBuyer(user); // 구매자 기준
        return mapToChatRoomListDto(chatRooms, user);
    }

    /* 특정 채팅방의 상세 정보 조회 */
    public List<ChatRoomDetailDto> findChatRoomDetails(Long roomId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_ROOM_ID"));
        if (!chatRoom.getBuyer().equals(user) && !chatRoom.getSeller().equals(user)) {
            throw new AccessDeniedException("USER_NOT_AUTHORIZED_FOR_CHATROOM");
        }
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
        return messages.stream()
                .map(message -> {
                    // MySQL에서 닉네임 조회
                    String nickname = userRepository.findById(message.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"))
                            .getNickname();

                    return ChatRoomDetailDto.from(message, nickname);
                })
                .collect(Collectors.toList());
    }

    /* ChatRoom을 ChatRoomListDto로 변환 */
    private List<ChatRoomListDto> mapToChatRoomListDto(List<ChatRoom> chatRooms, User currentUser) {
        return chatRooms.stream()
                .map(chatRoom -> {
                    ChatMessage latestMessage = chatMessageRepository.findTopByRoomIdOrderByTimestampDesc(chatRoom.getRoomId());
                    return ChatRoomListDto.from(chatRoom, latestMessage, currentUser);
                })
                .collect(Collectors.toList());
    }
}