package com.playssafy.playssafy.dto.ssazipjump;

import com.playssafy.playssafy.dto.ssafymind.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

/**
 * 점핑 게임, 참가자 정보 관리 DTO new
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@RedisHash("ssazipJump")
public class SsazipJump {

    @Id
    //@Id 어노테이션은 JPA와 동일한 역할을 수행합니다. "member:{id}"의 위치에 자동으로 generate 값이 들어갑니다.
    //용어 사전 -> 팀끼리 3번 붙는 것은 게임, 게임 스코어 라 하며, 8강 4강 등은 라운드라 명한다.
    // 3판 2선승을 위한 변수 gameScore1, gameScore2
    //  teams -> 그 라운드(강)을 담는 변수, 홀수시 봇(부전승 용 상대)
    //  팀원이 없다면 상대 팀은 승리한다.
    private String roomId; // 방 ID
    private String host; // 방장 정보
    private int gameScore1;// 1팀의 선승 값
    private int gameScore2;// 2팀의 선승 값
    private List<Team> teamsBase; // 팀원 구성 초기 정보, 입장 시 정보 Participant 형태
    private List<Integer> teamOrder; // 팀 진행 순서 => 봇은 teamNo =-2
    private List<Integer> teamOrderNext; // 현 라운드에서 승리하여 다음라운드로 올라간 팀
    private List<Integer> finalScore; // 최종 점수(배열 형태) {1등 팀 번호, 2등 , 3등, 3등}
    private int remainRound;//현재 라운드 계산 값
    private int teamIdx1; // 현재 진행 중 팀 1의 팀 인덱스
    private int teamIdx2;

    //from JumpInfo class
    private boolean[] jumpArr1; // 점프값 어레이
    private boolean[] jumpArr2;
    private boolean reloadflag;
    private String participantId; // 참가자의 고유 ID
    private String participantName; // 참가자의 이름
    private int teamNo; // 참가자 소속 팀 번호
    private boolean obstacleflag;//장애물 시작 플레그
    private Integer[] xbArr;//장애물 x위치 어레이
    private Integer[] xbArrType;//장애물 타입 어레이
    private int type;//데이터 정보 타입
    //참가 여부 어레이
    private boolean[] beUserPresent1;
    private boolean[] beUserPresent2;//참가 여부 어레이
    private boolean bGameStopFlag;//충돌 시 게임 중단 플레그


    public SsazipJump() {
        // 최대 10개 팀에 대응하는 팀 정보를 저장할 리스트 선언해두기
        int teamSize = 11;
        teamsBase = new ArrayList<>();
        for (int i = 0; i < teamSize; i++) {
            teamsBase.add(new Team());
            teamsBase.get(i).setTeamNo(i);
        }
        // 동적 리스트 초기화
        teamOrder = new ArrayList<>();
        teamOrderNext = new ArrayList<>();
        finalScore = new ArrayList<>();
    }


}
