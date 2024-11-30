package com.ecyce.karma.domain.chat.dto;

import com.ecyce.karma.domain.chat.entity.ChatMessage;
import com.ecyce.karma.domain.chat.entity.ChatRoom;
import com.ecyce.karma.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomListDto {
    private Long roomId;
    private String roomName;
    private String latestMessage;
    private LocalDateTime latestMessageTimestamp;
    private String profileImage;

    public static ChatRoomListDto from(ChatRoom chatRoom, ChatMessage latestMessage, User currentUser) {
        // 상대방 이름으로 방 이름 설정
        User otherUser = chatRoom.getBuyer().equals(currentUser) ? chatRoom.getSeller() : chatRoom.getBuyer();
        String otherUserName = otherUser.getNickname();
        String otherUserProfileImage = otherUser.getProfileImage();

        String content = (latestMessage != null) ? latestMessage.getContent() : "채팅 내역이 없습니다.";
        LocalDateTime timestamp = (latestMessage != null) ? latestMessage.getTimestamp() : null;


        return new ChatRoomListDto(
                chatRoom.getRoomId(),
                otherUserName,
                content,
                timestamp,
                otherUserProfileImage
        );
    }
}
