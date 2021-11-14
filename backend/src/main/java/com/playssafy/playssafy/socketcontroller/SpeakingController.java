package com.playssafy.playssafy.socketcontroller;

import com.playssafy.playssafy.dto.speaking.Speaking;
import com.playssafy.playssafy.dto.waitroom.Participant;
import com.playssafy.playssafy.service.SpeakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SpeakingController {
    private final SimpMessagingTemplate template;
    private final SpeakingService speakingService;

    // 1. 또박또박 말해요 입장
    @MessageMapping(value = "/speaking/enter")
    public void enter(Participant participant) {
        // 유저 입장 후 해당 게임 방 정보 얻기
        Speaking speaking = speakingService.enter(participant);
        // 게임 방 정보 소켓으로 반환
        template.convertAndSend("/speaking/" + participant.getRoomId(), speaking);
    }

}
