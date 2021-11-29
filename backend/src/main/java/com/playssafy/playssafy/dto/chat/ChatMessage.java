package com.playssafy.playssafy.dto.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String roomId; // 속한 채팅방 ID
    private String writer; // 작성자
    private String message; // 메세지
    //방장여부
}