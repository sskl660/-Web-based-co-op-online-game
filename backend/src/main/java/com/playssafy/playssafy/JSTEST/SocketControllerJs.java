package com.playssafy.playssafy.JSTEST;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class SocketControllerJs {
    // /receive를 메시지를 받을 endpoint로 설정합니다.
    @MessageMapping("/receive")

    // /send로 메시지를 반환합니다.
    @SendTo("/send")

    // SocketHandler는 1) /receive에서 메시지를 받고, /send로 메시지를 보내줍니다.
    // 정의한 SocketVO를 1) 인자값, 2) 반환값으로 사용합니다.
    public SocketVoJs SocketHandler(SocketVoJs socketVoJs) {
        // vo에서 getter로 userName을 가져옵니다.
        String userName = socketVoJs.getUserName();
        // vo에서 setter로 content를 가져옵니다.
        String content = socketVoJs.getContent();

        // 생성자로 반환값을 생성합니다.
        SocketVoJs result = new SocketVoJs(userName, content);
        // 반환
        return result;
    }

    //메세지 핸들러 메소드, 클라이언트에게 메세지를 수신하고 다른 클라이언트에게 브로드캐스트함
    //메세지메퍼 어노테이션에 /num으로 시작한 클라이언트 메세지가 라우팅 됨
    //여기선 /num/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    @SendTo("/cre/public")
    public SocketVoJs sendMessage(@Payload SocketVoJs socketVoJs) {
        return socketVoJs;
    }

    //여기선 /num/chat.addUser
    @MessageMapping("/chat.addUser")
    @SendTo("/cre/public")
    public SocketVoJs addUser(@Payload SocketVoJs socketVoJs, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", socketVoJs.getUserName());
        return socketVoJs;
    }
}
