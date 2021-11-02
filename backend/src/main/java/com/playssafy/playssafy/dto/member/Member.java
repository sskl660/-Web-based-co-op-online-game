package com.playssafy.playssafy.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Data
@Builder
@AllArgsConstructor
@RedisHash("member")
//@RedisHash 어노테이션을 통해 설정한 값을 Redis의 key 값 prefix로 사용합니다.
public class Member implements Serializable {

    private static final long serialVersionUID = 664865927712847110L;

    @Id
    //@Id 어노테이션은 JPA와 동일한 역할을 수행합니다. "member:{id}"의 위치에 자동으로 generate 값이 들어갑니다.
    private Long id;

    @Indexed
    private String userID;
    private String userName;
    private boolean masterflag;


    public Member(String userId, String userName, boolean masterflag) {
        this.userID = userId;
        this.userName = userName;
        this.masterflag = masterflag;
    }
}
