package com.playssafy.playssafy.socketcontroller;

import com.playssafy.playssafy.dto.speaking.Answer;
import com.playssafy.playssafy.dto.speaking.SpeakMessage;
import com.playssafy.playssafy.dto.speaking.Speaking;
import com.playssafy.playssafy.dto.waitroom.Participant;
import com.playssafy.playssafy.service.SpeakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SpeakingController {
    private final SimpMessagingTemplate template;
    private final SpeakingService speakingService; // 또박또박 말해요 서비스

    // 1. 또박또박 말해요 입장
    @MessageMapping(value = "/speaking/enter")
    public void enter(Participant participant) {
        // 유저 입장 후 해당 게임 방 정보 얻기
        Speaking speaking = speakingService.enter(participant);
        // 게임 방 정보 소켓으로 반환
        template.convertAndSend("/speaking/" + participant.getRoomId(), speaking);
    }

    // 2. 또박또박 말해요 퇴장
    @MessageMapping(value = "/speaking/exit")
    public void exit(Participant participant) {
        // 유저 입장 후 해당 게임 방 정보 얻기
        Speaking speaking = speakingService.exit(participant);
        // 방장이 퇴장한 경우 종료 메세지 뿌려주기
        if (speaking == null) {
            template.convertAndSend("/speaking/" + participant.getRoomId(), "exit");
            return;
        }
        // 게임 방 정보 소켓으로 반환
        template.convertAndSend("/speaking/" + participant.getRoomId(), speaking);
    }

    // 3. 또박또박 말해요 문장 데이터 교환
    @MessageMapping(value = "/speaking/answer")
    public void answer(@DestinationVariable String roomId, Answer answer) {
        template.convertAndSend("/speaking/" + roomId, answer);
        // 말을 서버에 저장
        speakingService.speak(roomId, point);
  }
}
