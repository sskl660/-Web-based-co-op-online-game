package com.playssafy.playssafy.service;

import com.playssafy.playssafy.dto.speaking.Speaking;
import com.playssafy.playssafy.dto.ssafymind.Quiz;
import com.playssafy.playssafy.dto.ssafymind.Team;
import com.playssafy.playssafy.dto.waitroom.InitGame;
import com.playssafy.playssafy.dto.waitroom.Participant;
import com.playssafy.playssafy.repository.SpeakingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpeakingService {
    @Autowired
    private SpeakingRepository speakingRepository;

    // 0. 게임방 생성 메서드
    public void createSpeaking(InitGame initGame) {
        if(!speakingRepository.findById(initGame.getRoomId()).isEmpty())
            return;
        Speaking speaking = new Speaking();

        // 방 ID 초기화
        speaking.setRoomId(initGame.getRoomId());
        // 방장 정보 추가
        speaking.setHost(initGame.getHost());

        // 팀 진행 순서 초기화
        boolean[] exist = initGame.getExist();
        // 현재 팀이 존재한다면, 팀 넣기
        List<Integer> teams = new ArrayList<>();
        for (int i = 1; i < exist.length; i++) {
            if (exist[i])
                teams.add(i);
        }
        // 팀 섞기(100번)
        for (int i = 0; i < 100; i++) {
            // 임의의 두 인덱스를 지정하고
            int a = (int) (Math.random() * teams.size());
            int b = (int) (Math.random() * teams.size());
            // 교환
            int temp = teams.get(a);
            teams.set(a, teams.get(b));
            teams.set(b, temp);
        }

        // 최종 결정된 순서 넣기(문제 개수 만큼)
        for(int i = 0; i < initGame.getQuizCnt(); i++) {
            for (Integer team : teams) {
                speaking.getTeamOrder().add(team);
            }
        }
        // 현재 팀 개수 초기화
        speaking.setCurTeamCnt(teams.size());

        // 테스트 문제 리스트 ////////
        for(int i = 0; i < 20; i++) {
            speaking.getQuizzes().add(new Quiz(1, "아미타불", "아미타불"));
            speaking.getQuizzes().add(new Quiz(1, "원시천존", "원시천존"));
            speaking.getQuizzes().add(new Quiz(1, "무량수불", "무량수불"));
        }
        // 팀 초기화////////
        speaking.getTeams().get(1).getMembers().add(new Participant(initGame.getRoomId(), "1", "김태현1", 1));
        speaking.getTeams().get(1).getMembers().add(new Participant(initGame.getRoomId(), "2", "김태현2", 1));
        speaking.getTeams().get(1).getMembers().add(new Participant(initGame.getRoomId(), "3", "김태현3", 1));
        speaking.getTeams().get(1).getMembers().add(new Participant(initGame.getRoomId(), "4", "김태현4", 1));
        speaking.getTeams().get(2).getMembers().add(new Participant(initGame.getRoomId(), "1", "이장섭1", 2));
        speaking.getTeams().get(2).getMembers().add(new Participant(initGame.getRoomId(), "2", "이장섭2", 2));
        speaking.getTeams().get(2).getMembers().add(new Participant(initGame.getRoomId(), "3", "이장섭3", 2));
        speaking.getTeams().get(2).getMembers().add(new Participant(initGame.getRoomId(), "4", "이장섭4", 2));


        speakingRepository.save(speaking);
    }

    // 1. 게임방 입장 메서드
    public synchronized Speaking enter(Participant participant) {
        Speaking speaking = speakingRepository.findById(participant.getRoomId()).get();
        Team team = speaking.getTeams().get(participant.getTeamNo());
        if (!team.getMembers().contains(participant))
            team.getMembers().add(participant);

        // 변경 완료
        return speakingRepository.save(speaking);
    }
}
