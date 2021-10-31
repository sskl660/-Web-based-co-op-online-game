package com.playssafy.playssafy.JSTEST.gorany;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class ChatController {
    @GetMapping("/chat")//왜또 겟 매퍼야
    public  void chatGET(){
        log.info("@chatCtrler, chat get()");
    }

}
