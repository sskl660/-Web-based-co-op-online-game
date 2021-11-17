package com.playssafy.playssafy.dto.speaking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Talking {
    // 나중에 따로 빼줄 것
    private String sentence; // 말하는 중
    private String talker; // 말한 사람
}
