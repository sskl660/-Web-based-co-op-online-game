<template>
  <div class="waiting-room">
    <!-- 게임 선택 모달창 -->
    <SelectGameModal
      v-if="openmodal == true"
      v-bind:host="this.room.host"
      @getOpenModal="getOpenModal"
      @infoSsafyMind="infoSsafyMind"
      @infoSpeakGame="infoSpeakGame"
      @infoJumpGame="infoJumpGame"
      v-on:selectGame="selectGame"
    />

    <div class="room-title">
      <span id="game-title">{{ room.name }}</span>
      <span id="game-code"
        >입장코드
        <button @click="copyCode" id="game-code-btn">복사</button>
      </span>
    </div>
    <div class="d-flex justify-content-center">
      <div class="room-left">
        <div class="btn-group" v-if="this.room.host == this.getUser.id">
          <button id="btn-1" class="btn" @click="showTeam(1)">1</button>
          <button id="btn-2" class="btn" @click="showTeam(2)">2</button>
          <button id="btn-3" class="btn" @click="showTeam(3)">3</button>
          <button id="btn-4" class="btn" @click="showTeam(4)">4</button>
          <button id="btn-5" class="btn" @click="showTeam(5)">5</button>
          <button id="btn-6" class="btn" @click="showTeam(6)">6</button>
          <button id="btn-7" class="btn" @click="showTeam(7)">7</button>
          <button id="btn-8" class="btn" @click="showTeam(8)">8</button>
          <button id="btn-9" class="btn" @click="showTeam(9)">9</button>
          <button id="btn-10" class="btn" @click="showTeam(10)">10</button>
        </div>
        <!-- </div> -->
        <div class="team-group">
          <div v-for="index in totalTeam" :key="index" class="line">
            <div
              v-bind:class="'team-group team-group-' + index"
              @click="changeTeam(index)"
              v-if="room.teamline != null && room.teamline[index]"
            >
              <div v-bind:id="'team-' + index" class="team">{{ index }}</div>
              <div v-bind:id="'team-' + index + '-name'" class="team-name">
                {{ assignTeamNo[index][0] }}
              </div>
              <div v-bind:id="'team-' + index + '-name'" class="team-name">
                {{ assignTeamNo[index][1] }}
              </div>
              <div v-bind:id="'team-' + index + '-name'" class="team-name">
                {{ assignTeamNo[index][2] }}
              </div>
              <div v-bind:id="'team-' + index + '-name'" class="team-name">
                {{ assignTeamNo[index][3] }}
              </div>
              <div v-bind:id="'team-' + index + '-name'" class="team-name">
                {{ assignTeamNo[index][4] }}
              </div>
              <div v-bind:id="'team-' + index + '-name'" class="team-name">
                {{ assignTeamNo[index][5] }}
              </div>
            </div>
          </div>
          <!-- 여기서부터는 관전자 -->
          <div class="team-group-extra" @click="changeTeam(0)" v-if="room.members != null">
            <div id="team-extra">관전</div>
            <span v-for="(member, index) in room.members" :key="index">
              <span v-if="member.teamNo === 0" id="team-extra-name" class="team-name">
                {{ member.participantName }}
              </span>
            </span>
          </div>
        </div>
      </div>
      <div class="room-right">
        <div class="score">
          <div class="score-line d-flex justify-content-center">
            <p id="score-title">총 누적 점수</p>
            <div style="float:left">
              <div v-for="(score, idx) in this.room.scores" :key="idx">
                <div v-if="idx <= 5 && idx >= 1">
                  <span id="score-team">{{ idx }}팀</span>
                  <span id="score-number" v-if="room.teamline[idx]">{{ score }}점</span>
                  <span id="score-number" v-else> - </span>
                  <br /><br />
                </div>
              </div>
            </div>
            <div style="float:right">
              <div v-for="(score, idx) in this.room.scores" :key="idx">
                <div v-if="idx >= 6">
                  <span id="score-team">{{ idx }}팀</span>
                  <span id="score-number" v-if="room.teamline[idx]">{{ score }}점</span>
                  <span id="score-number" v-else> - </span>
                  <br /><br />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="thumbnail">
          <div class="thumbnail-line">
            <div
              v-if="
                ssafymind_explain === false &&
                  speakgame_explain === false &&
                  jumpgame_explain === false
              "
            >
              <img id="ssazip-img" src="~@/assets/images/ssazip-create.png" alt="" />
              <div id="selectgame-name" style="font-family: 'Poor Story', cursive; font-size:30px">
                게임 선택 버튼을 눌러 게임을 고르세요~!
              </div>
            </div>
            <div v-if="ssafymind_explain === true">
              <div id="game-name">싸피 마인드</div>
              <img id="ssafymind-img" src="~@/assets/ssafymind.png" alt="" />
            </div>
            <div v-if="speakgame_explain === true">
              <div id="game-name">또박또박 말해요</div>
              <img id="speakgame-img" src="~@/assets/speakgame.png" alt="" />
            </div>
            <div v-if="jumpgame_explain === true">
              <div id="game-name">싸집이 점프</div>
              <img id="jumpgame-img" src="~@/assets/jumpgame.png" alt="" />
            </div>
          </div>
        </div>
        <div>
          <button
            class="waiting-room-btn select-game"
            @click="openmodal = true"
            v-if="this.room.host == this.getUser.id"
          >
            게임 선택
          </button>
          <button
            class="waiting-room-btn final-score"
            @click="endGame()"
            v-if="this.room.host == this.getUser.id"
          >
            최종 결과
          </button>
          <img
            class="play-btn"
            src="~@/assets/play-button.png"
            v-if="this.room.host == this.getUser.id"
            @click="gameStart()"
            alt=""
          />
          <button
            class="waiting-room-btn select-game"
            @click="openmodal = true"
            v-if="this.room.host != this.getUser.id"
          >
            게임 설명
          </button>
          <button
            class="waiting-room-btn final-score"
            @click="exitRoom()"
            v-if="this.room.host != this.getUser.id"
          >
            나가기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import '@/css/room.css';
import SelectGameModal from '@/components/room/SelectGameModal';
import axios from '@/util/http-common.js';
import { mapActions, mapGetters, mapState } from 'vuex';
import { socketConnect } from '@/util/socket-common.js';
import swal from 'sweetalert';

export default {
  name: 'Room',
  components: {
    SelectGameModal, // 게임 선택 모달
  },
  data: function() {
    return {
      openmodal: false,
      ssafymind_explain: false,
      speakgame_explain: false,
      jumpgame_explain: false,
      room: {},
      assignTeamNo: {
        0: [],
        1: [],
        2: [],
        3: [],
        4: [],
        5: [],
        6: [],
        7: [],
        8: [],
        9: [],
        10: [],
      },
      curGame: '',
      totalTeam: 10,
      // socket Client
      stompClient: null,
      pNumber: 3,
      gameType: null,
      interval: null,
    };
  },
  // 방 생성시
  created() {
    this.checkRoom();
    this.checkName();
    // 방정보 초기화
    this.room.id = this.getRoomId;
    this.stompClient = socketConnect(this.onConnected, this.onError);
    this.stompClient.debug = function() {};
    history.pushState(null, null, location.href);
    window.onpopstate = () => {
      history.go(1);
      swal({
        // className:'alert',
        title: '게임 내에서는 뒤로가기가 불가합니다.',
        icon: '/img/ssazip-logo.png',
        buttons: {
          text: '확인',
        },
      });
    };
    // if (this.getIsLogin) {
    //   this.interval = setInterval(this.keepSign, 1000);
    // }
  },
  // 방 삭제시
  updated() {
    this.setButton();
  },
  computed: {
    ...mapGetters(['getRoomId', 'getUser', 'getStompClient', 'getIsLogin']),
    ...mapState(['visitedRoomId']),
  },
  watch: {
    'room.members'() {
      this.assignTeam();
    },

    curGame() {
      this.moveCurGame();
    },
  },

  methods: {
    checkHost: function() {
      if (this.getUser.name == this.room.host) return true;
      else false;
    },

    // 유효하지 않은 방으로 입장했다면 home 화면으로 이동
    checkRoom: function() {
      const roomId = this.$route.params.roomId;
      // 서버 레벨 입장 가능 여부 확인 로직
      axios({
        method: 'post',
        url: `/game/user`,
        data: {
          roomId: roomId,
          participantName: this.getUser.name,
          participantId: this.getUser.id,
        },
      })
        .then((res) => {
          let exist = res.data;
          // 방이 존재하지 않는 경우
          if (exist == 0) {
            swal({
              // className:'alert',
              title: '해당 방은 존재하지 않습니다.',
              text: '입장코드를 확인해주세요!',
              icon: '/img/ssazip-logo.png',
              buttons: {
                text: '확인',
              },
            });
            this.$router.push('/');
            return;
          }
          // 입장 제한인원을 초과한 경우
          else if (exist == 1) {
            swal({
              // className:'alert',
              title: '해당 방의 정원을 초과했습니다!',
              icon: '/img/ssazip-logo.png',
              buttons: {
                text: '확인',
              },
            });
            this.$router.push('/');
            return;
          }
          // 동일한 이름의 참가자가 존재하는 경우
          else if (exist == 2) {
            // 새로고침하는 경우
            if (this.getUser.name.length != 0) return;
            swal({
              // className:'alert',
              title: '동일한 이름의 참가자가 존재합니다.',
              text: '이름을 수정해주세요!',
              icon: '/img/ssazip-logo.png',
              buttons: {
                text: '확인',
              },
            });
            this.$router.push('/');
            return;
          }
        })
        .catch(() => {});
    },
    // 이름이 없으면(url을 통해 이름을 정하지 않고 들어왔다면) home 화면으로 강퇴
    checkName: function() {
      const userName = this.getUser.name;
      if (!userName) {
        swal({
          // className:'alert',
          title: '잘못된 접근입니다.',
          icon: '/img/ssazip-logo.png',
          buttons: {
            text: '확인',
          },
        });
        this.$router.push('/');
      }
    },
    showTeam: function(team) {
      // 자신을 찾기
      const btn = document.querySelector(`#btn-${team}`);
      if (this.room.teamline[team] == true) {
        this.room.teamline[team] = false;
        btn.classList.remove(`btn-${team}`);
        for (let member of this.room.members) {
          if (member.teamNo == team) {
            member.teamNo = 0;
          }
        }
      } else if (this.room.teamline[team] == false) {
        this.room.teamline[team] = true;
        btn.classList.add(`btn-${team}`);
      }
      this.changeTeamMessage();
    },
    assignTeam: function() {
      for (let i = 0; i < 11; i++) {
        this.assignTeamNo[i] = [];
      }
      for (let member of this.room.members) {
        this.assignTeamNo[member.teamNo].push(member.participantName);
      }
    },
    changeTeam: function(teamNo) {
      for (let idx = 0; idx < this.room.members.length; idx++) {
        if (this.room.members[idx].participantName == this.getUser.name) {
          if (this.room.members[idx].teamNo == teamNo) return;
          if (this.assignTeamNo[teamNo].length >= 6 && teamNo != 0) {
            swal({
              // className:'alert',
              title: '해당 팀은 정원이 가득 찼습니다!',
              icon: '/img/ssazip-logo.png',
              buttons: {
                text: '확인',
              },
            });
            return;
          }
          this.room.members[idx].teamNo = teamNo;
          let temp = this.room.members[idx];
          this.room.members.splice(idx, 1);
          this.room.members.push(temp);
          // vuex 팀정보도 바꾸기
          this.changeUserTeamNo(teamNo);
          break;
        }
      }
      this.changeTeamMessage();
    },
    getOpenModal(openmodal) {
      this.openmodal = openmodal;
    },
    infoSsafyMind(ssafymind_explain) {
      this.ssafymind_explain = ssafymind_explain;
      this.speakgame_explain = false;
      this.jumpgame_explain = false;
      this.gameType = 1;
    },
    infoSpeakGame(speakgame_explain) {
      this.speakgame_explain = speakgame_explain;
      this.ssafymind_explain = false;
      this.jumpgame_explain = false;
      this.gameType = 2;
    },
    infoJumpGame(jumpgame_explain) {
      this.jumpgame_explain = jumpgame_explain;
      this.ssafymind_explain = false;
      this.speakgame_explain = false;
      this.gameType = 3;
    },
    // 입장코드 복사
    copyCode() {
      this.$copyText(this.getRoomId);
      swal({
        // className:'alert',
        title: '입장 코드를 복사했습니다!',
        icon: '/img/ssazip-logo.png',
        buttons: {
          text: '확인',
        },
      });
    },
    /**
     * Socket 요청들
     */
    // 게임 방 입장 : 정보 구독 및 유저 정보 전송
    onConnected() {
      // 유저 정보 교환
      this.stompClient.subscribe('/game/room/' + this.getRoomId, this.onMessageReceived);
      // 게임방 조작 관련 메세지
      this.stompClient.subscribe('/game/start/' + this.getRoomId, this.startMessageReceived);
      this.stompClient.send(
        '/pub/game/enter',
        {},
        JSON.stringify({
          roomId: this.getRoomId,
          participantId: this.getUser.id,
          participantName: this.getUser.name,
          teamNo: this.getUser.teamNo,
        })
      );
    },
    keepSign() {
      axios({
        method: 'get',
        url: `/sign/${this.getRoomId}`,
      })
        .then(() => {})
        .catch(() => {});
    },
    // 메세지 수신
    onMessageReceived(payload) {
      // 방장이 퇴장한 경우
      if (payload.body == 'exit') {
        // 모든 참가자의 연결을 끊고
        this.onDisconnect();
        swal({
          // className:'alert',
          title: '방장이 퇴장하여 게임이 종료됩니다!!',
          icon: '/img/ssazip-logo.png',
          buttons: {
            text: '확인',
          },
        });
        return;
      }
      let room = JSON.parse(payload.body);
      this.room = room;
      this.ssafymind_explain = room.ssafymindExplain;
      this.speakgame_explain = room.speakgameExplain;
      this.jumpgame_explain = room.jumpgameExplain;
      this.gameType = room.gameType;
      // 현재 진행중인 게임으로 자동 이동
      if (this.room.progress) {
        this.curGame = this.room.gameType;
        this.moveCurGame();
      }
    },
    // 버튼 클릭 초기화
    setButton() {
      if (this.room.teamline == null) return;
      for (let idx = 1; idx < 11; idx++) {
        if (this.room.teamline[idx]) {
          // 자신을 찾기
          let btn = document.querySelector(`#btn-${idx}`);
          if (btn == null) return;
          btn.classList.add(`btn-${idx}`);
        }
      }
    },
    onError() {},
    // 게임 방 퇴장 소켓 연결 해제 및 게임 방 유저 정보 삭제
    onDisconnect() {
      let isHost;
      if (this.room.host == this.getUser.id) isHost = true;
      else isHost = false;
      this.stompClient.send(
        '/pub/game/exit',
        {},
        JSON.stringify({
          roomId: this.getRoomId,
          participantId: this.getUser.id,
          participantName: this.getUser.name,
          host: isHost,
        })
      );
      this.$router.push('/').catch(() => {});
      setTimeout(() => {
        this.stompClient.disconnect();
      }, 2000);
    },
    // 팀 번호 변경시 소켓 요청
    changeTeamMessage() {
      this.stompClient.send('/pub/game/change/team', {}, JSON.stringify(this.room));
    },
    // 선택한 문제 개수 받기
    selectGame(pNumber) {
      this.pNumber = pNumber;
      this.stompClient.send(
        '/pub/game/change/game',
        {},
        JSON.stringify({
          roomId: this.getRoomId,
          ssafymindExplain: this.ssafymind_explain,
          speakgameExplain: this.speakgame_explain,
          jumpgameExplain: this.jumpgame_explain,
          gameType: this.gameType,
        })
      );
    },
    noParticipantChecker() {
      if (this.room.members.length == 0) {
        swal({
          // className:'alert',
          title: '참여인원이 없습니다!',
          icon: '/img/ssazip-logo.png',
          buttons: {
            text: '확인',
          },
        });
        return false;
      }
      let curCnt = 0;
      for (let i = 1; i < 11; i++) {
        if (this.room.teamline[i]) curCnt++;
        if (this.room.teamline[i] && this.assignTeamNo[i].length == 0) {
          swal({
            // className:'alert',
            title: '팀에는 최소 한 명이 필요합니다!',
            icon: '/img/ssazip-logo.png',
            buttons: {
              text: '확인',
            },
          });
          return false;
        }
      }
      if (curCnt < 2) {
        swal({
          // className:'alert',
          title: '최소 2개의 팀이 필요합니다!',
          icon: '/img/ssazip-logo.png',
          buttons: {
            text: '확인',
          },
        });

        return;
      }
      return true;
    },
    // 게임 시작
    gameStart() {
      if (this.gameType == 0) {
        swal({
          // className:'alert',
          title: '게임을 선택해주세요!',
          icon: '/img/ssazip-logo.png',
          buttons: {
            text: '확인',
          },
        });
        return;
      }
      if (!this.noParticipantChecker()) {
        return;
      }
      // 싸피마인드
      if (this.gameType == 1) {
        axios({
          method: 'post',
          url: `/game/create/ssafymind`,
          data: {
            roomId: this.room.id,
            host: this.room.host,
            exist: this.room.teamline,
            gameType: this.gameType,
            // 팀당 문제 개수 설정
            quizCnt: this.pNumber,
          },
        })
          .then(() => {
            // 방에 있는 모든 참가자 게임방으로 옮기기
            this.stompClient.send(`/pub/game/start/${this.gameType}`, {}, this.room.id);
          })
          .catch(() => {});
      }
      // 또박또박말해요
      else if (this.gameType == 2) {
        axios({
          method: 'post',
          url: `/game/create/speaking`,
          data: {
            roomId: this.room.id,
            host: this.room.host,
            exist: this.room.teamline,
            gameType: this.gameType,
            // 팀당 문제 개수 설정
            quizCnt: this.pNumber,
          },
        })
          .then(() => {
            // 방에 있는 모든 참가자 게임방으로 옮기기
            this.stompClient.send(`/pub/game/start/${this.gameType}`, {}, this.room.id);
          })
          .catch(() => {});
      }
      // 싸집이 점프
      else if (this.gameType == 3) {
        axios({
          method: 'post',
          url: `/game/create/ssazipjump`,
          data: {
            roomId: this.room.id,
            host: this.room.host,
            exist: this.room.teamline,
            gameType: this.gameType,
          },
        })
          .then(() => {
            // 방에 있는 모든 참가자 게임방으로 옮기기
            this.stompClient.send(`/pub/game/start/${this.gameType}`, {}, this.room.id);
          })
          .catch(() => {});
      }
    },
    // 게임 종료
    endGame() {
      this.gameType = -1;
      this.stompClient.send(`/pub/game/start/${this.gameType}`, {}, this.room.id);
    },
    // 방 나가기(유저만)
    exitRoom() {
      // 정보 초기화
      this.onDisconnect();
      this.login({ id: '', name: '' });
      this.joinRoom('');
    },
    // 게임 시작 메세지
    startMessageReceived(payload) {
      this.curGame = payload.body;
    },
    // 진행중인 게임으로 자동 이동
    moveCurGame() {
      // 싸피마인드로 이동
      if (this.curGame == 1) {
        this.$router.push('/ssafymind/' + this.room.id).catch(() => {});
      }
      // 또박또박말해요로 이동
      else if (this.curGame == 2) {
        this.$router.push('/speaking/' + this.room.id).catch(() => {});
      }
      // 싸집이 점프로 이동
      else if (this.curGame == 3) {
        //
        this.$router.push('/ssazipjumpseop/' + this.room.id).catch(() => {});
      }
      // 결산
      else if (this.curGame == -1) {
        this.$router.push({
          path: '/rank/' + this.room.id,
          query: { rank: this.room.scores, curTeam: this.room.teamline },
        });
      }
    },
    ...mapActions(['login', 'joinRoom', 'changeUserTeamNo', 'setStompClient']),
  },
};
</script>

<style>
body {
  margin: 0;
}
</style>
