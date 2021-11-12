package com.playssafy.playssafy.service;

import com.playssafy.playssafy.dto.speaking.SpeakMessage;
import com.playssafy.playssafy.dto.speaking.Answer;
import com.playssafy.playssafy.dto.speaking.Speaking;
import com.playssafy.playssafy.dto.speaking.Team;
import com.playssafy.playssafy.dto.waitroom.InitGame;
import com.playssafy.playssafy.dto.waitroom.Participant;
import com.playssafy.playssafy.repository.SpeakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpeakingService {
    @Autowired
    private SpeakRepository speakRepository;

    // 0. 게임방 생성 메서드
    public void createSsafyMind(InitGame initGame) {
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

        // 최종 결정된 순서 넣기
        for (Integer i : teams) {
          speaking.getTeamOrder().add(i);
        }

        speakRepository.save(speaking);
    }

    // 1. 게임방 입장 메서드
    public synchronized Speaking enter(Participant participant) {
        Speaking speaking = speakRepository.findById(participant.getRoomId()).get();
        Team team = speaking.getTeams().get(participant.getTeamNo());
        if (!team.getMembers().contains(participant))
            team.getMembers().add(participant);

        // 변경 완료
        return speakRepository.save(speaking);
    }

    // 2. 게임방 퇴장 메서드
    public synchronized Speaking exit(Participant participant) {
        Speaking speaking = speakRepository.findById(participant.getRoomId()).get();

        // 방장이라면 방 자체를 삭제 후 종료
        if (speaking.getHost().equals(participant.getParticipantId())) {
            speakRepository.deleteById(participant.getRoomId());
            return null;
        }
        // 방장이 아니라면 유저 정보만 삭제
        Team team = speaking.getTeams().get(participant.getTeamNo());
        team.getMembers().remove(participant);

        // 변경 완료
        return speakRepository.save(speaking);
    }

    // 3. 중간 문장 데이터 전달
    public void speak(String roomId, Answer answer) {
        Speaking speaking = speakRepository.findById(roomId).get();

        // 문장 데이터 추가
        speaking.getAnswer().add(answer);
        speakRepository.save(speaking);
    }

    // 4. 정답 여부 확인
    public synchronized boolean answer(String roomId, SpeakMessage speakMessage) {
      Speaking speaking = speakRepository.findById(roomId).get();

      // 메세지 스택 저장
      speaking.getChat().add(speakMessage);
      speakRepository.save(speaking);
      // 리스트의 마지막 부분 부터 문제 회수
      int lastIndex = speaking.getQuizzes().size() - 1;
      if(speaking.getQuizzes().get(lastIndex).getAnswer().equals(speakMessage.getMessage())) {
          // 마지막 문제를 제거하고
          speaking.getQuizzes().remove(lastIndex);
          // 다음 팀으로 옮기고
          speaking.getTeamOrder().remove(0);
          // 그린 그림 정보 삭제하고
          speaking.getPoints().clear();
          System.out.println(speaking.getQuizzes());
          // 저장한 뒤
          speakRepository.save(speaking);
          // 정답
          return true;
      }
      // 오답
      return false;
  }


//    @Autowired//자동주입
//    private QuizService quizService;

//    //게임 생성
//    public SsafyMind createGame(SsafyMind ssafyMind) throws Exception{
//        List<MindDto> quizzes= quizService.getMindQuiz(ssafyMind.getQuizIdx());
//        ssafyMind.setQuizzes(quizzes);
//        ssafyMind.setQuizIdx(0);
//        return ssafyMindRepository.save(ssafyMind);
//    }
//
//    //게임 종료
//    public boolean deleteGame(Long roomCode) throws Exception{
//        ssafyMindRepository.deleteById(roomCode);
//        Optional<SsafyMind> ssafyMind = ssafyMindRepository.findById(roomCode);
//        if (ssafyMind.isPresent()) {
//            return false;
//        }
//        else return true;
//    }
//
//    //다음 플레이 할 팀, 다음 문제 요청, 점수 갱신
//    public SsafyMind playNextTeam(SsafyMind ssafyMind) throws Exception{
//        //처음 시작시
//        if(ssafyMind.getQuizIdx()==0){
//            return ssafyMind;
//        }
//
//        //게임 종료
//        if(ssafyMind.getQuizIdx()==ssafyMind.getQuizzes().size()){
//            return null;
//        }
//
//        //게임 진행중
//        int nextTeam = ssafyMind.getPlayTeamIdx()+1;
//        if(nextTeam==ssafyMind.getTeamOrder().size()){
//            nextTeam=0;
//        }
//        int nextQuiz = ssafyMind.getQuizIdx()+1;
//        ssafyMind.setQuizIdx(nextQuiz);
//        ssafyMind.setPlayTeamIdx(nextTeam);
//        return ssafyMindRepository.save(ssafyMind);
//    }

    //점수 갱신, 요청  // 필요한가

    //채팅 기록
//    public List<String[]> chat(String roomCode, String guestId,String Sentence) throws Exception{
//        SsafyMind ssafyMind= ssafyMindRepository.findById(Long.parseLong(roomCode)).get();
//        List<String[]> chats= ssafyMind.getChat();
//        chats.add(new String[]{roomCode,guestId,Sentence});
//        ssafyMind.setChat(chats);
//        ssafyMindRepository.save(ssafyMind);
//        return chats;
//    }

//    //그림 좌표 기록, 요청
//    public SsafyMind drawGirin(String roomCode, String[] points) throws Exception{
//        SsafyMind ssafyMind= ssafyMindRepository.findById(Long.parseLong(roomCode)).get();
//        List<String[]> girin= ssafyMind.getPoints();
//        girin.add(points);
//        ssafyMind.setPoints(girin);
//        return ssafyMindRepository.save(ssafyMind);
//    }
}
