<template>
  <div class="speak-game">
    <div style="display:none;" id="audios-container"></div>
    <GameOrderModal
      v-if="ordermodal == true"
      v-bind:teamOrder="room.teamOrder"
      v-bind:teamCnt="room.curTeamCnt"
      v-bind:host="room.host"
      v-bind:user="getUser.id"
      v-on:sendGameStartTrigger="sendGameStartTrigger"
      :gameType="'speaking'"
    />
    <SpeakAnswerModal
      v-if="answermodal == true && rankmodal == false"
      @getCloseAnsModal="getCloseAnsModal"
      v-bind:teamOrder="room.teamOrder"
      v-bind:host="room.host"
    />
    <SpeakingRankModal
      v-if="rankmodal == true"
      @getCloseRankModal="getCloseRankModal"
      v-bind:host="room.host"
      v-bind:scores="room.scores"
      v-bind:curTeam="room.curTeam"
      v-on:sendGoWaitingRoomTrigger="sendGoWaitingRoomTrigger"
    />
    <Header
      v-bind:gameTitle="'또박또박 말해요'"
      :host="room.host"
      @onDisconnect="onDisconnect"
    />
    <div class="game-screen">
      <GameStatus
        game="speak"
        v-bind:teamOrder="room.teamOrder"
        v-bind:teams="room.teams"
        v-bind:curPlayer="room.curPlayer"
        v-bind:scores="room.scores"
        v-bind:curTeam="room.curTeam"
        v-bind:curTeamCnt="room.curTeamCnt"
        v-if="room != null"
        :timer="timer"
        :gameType="'speaking'"
      />
      <div class="game-board">
        <p class="sentence">"{{ quiz }}"</p>
        <div
          v-for="(member, i) in teamMember"
          :key="i"
          class="sentence-board"
          id="sentence-board"
        >
          <div v-if="i % 2 != 1" class="member-left sentence-board-child">
            <div class="member-name">
              <span>{{ member.participantName }}</span>
              <img :src="speakImg[0]" alt="speaking progress image" />
            </div>
            <div class="member-sentence member-sentence-left">
              <span></span>
            </div>
          </div>
          <div v-else class="member-right sentence-board-child">
            <div class="member-sentence member-sentence-right">
              <span></span>
            </div>
            <div class="member-name">
              <span>{{ member.participantName }}</span>
              <img :src="speakImg[0]" alt="speaking progress image" />
            </div>
          </div>
        </div>
        <img
          v-if="showMic"
          :src="micImg[2]"
          alt="mic img"
          class="game-mic-default"
          id="record"
        />
        <img
          v-else
          :src="micImg[1]"
          alt="mic img"
          class="game-mic-default"
          id="record"
        />
        <button @click="enterAudioRoom">마이크</button>
      </div>

      <!-- 아래는 나중에 사용할 아이들 -->
      <div style="display:none;">
        <p>녹음하기</p>
        <input type="checkbox" id="chk-hear-mic" /><label for="chk-hear-mic"
          >마이크 소리 듣기</label
        >
        <!-- <button id="record">녹음</button> -->
        <!-- <button id="stop">녹음 정지</button> -->
        <span id="final_span"></span>
        <span id="interim_span"></span>
        <div id="sound-clips"></div>
      </div>
    </div>
  </div>
</template>


<script src="/node_modules/webrtc-adapter/out/adapter.js"></script>
<script src="https://rtcmulticonnection.herokuapp.com/dist/RTCMultiConnection.min.js"></script>
<script src="https://rtcmulticonnection.herokuapp.com/socket.io/socket.io.js"></script>
<script>
import "@/css/speaking-game.css";
import { mapGetters } from "vuex";
import { socketConnect } from "@/util/socket-common.js";
import Header from "@/components/common/Header.vue";
import GameOrderModal from "@/components/GameOrderModal";
import SpeakAnswerModal from "@/components/speaking/SpeakAnswerModal";
import SpeakingRankModal from "@/components/speaking/SpeakingRankModal";
import GameStatus from "@/components/GameStatus.vue";
import swal from "sweetalert";

export default {
  name: "SpeakGame",
  components: {
    Header,
    GameStatus,
    GameOrderModal,
    SpeakAnswerModal,
    SpeakingRankModal,
  },
  data: () => {
    return {
      ordermodal: true,
      answermodal: false,
      record: {},
      stop: {},
      soundClips: {},
      chkHearMic: {},
      audioCtx: {},
      analyser: {},
      chunks: [],
      mediaRecorder: {},
      isRecording: false,
      // Socket 정보
      stompClient: null,
      room: {},
      interimTranscript: "",
      finalTranscript: "",
      teamMember: [],
      answerIdx: 0,
      speakImg: [
        "/img/speak/speak-wait.png",
        "/img/speak/speak-loading.png",
        "/img/speak/speak-fail.png",
        "/img/speak/speak-success.png",
      ],
      micImg: [
        "/img/mic/mic-default.png",
        "/img/mic/mic-enabled.png",
        "/img/mic/mic-on.png",
        "/img/mic/mic-off.png",
      ],
      talkFinish: false,
      quiz: null,
      lastTeamLen: 0,
      talker: 0,
      showMic: false,
      timer: true,
      rankmodal: false,
      connection: new RTCMultiConnection(),
      isRoomOpened: false,
      isRoomJoined: false,
    };
  },
  created() {
    history.pushState(null, null, location.href);
    window.onpopstate = () => {
      history.go(1);
      swal({
        // className:'alert',
        title: "게임 내에서는 뒤로가기가 불가능합니다.",
        icon: "/img/ssazip-logo.png",
        buttons: {
          text: "확인",
        },
      });
    };
    this.stompClient = socketConnect(this.onConnected, this.onError);
  },
  async mounted() {
    // this.stompClient.debug = function() {};
    // this.getAudio();
    await this.translate();
    await this.setAudio();
  },
  destroyed() {
    // 나갈 때 음성 닫아주기
    this.connection.close();
    this.onDisconnect();
  },
  watch: {},
  computed: {
    ...mapGetters(["getUser", "getRoomId"]),
  },
  methods: {
    /*  */
    // 음성을 텍스트로 번역
    /*  */
    translate: async function() {
      if (typeof webkitSpeechRecognition !== "function") {
        swal({
          // className:'alert',
          title: "크롬에서만 동작합니다.",
          icon: "/img/ssazip-logo.png",
          buttons: {
            text: "확인",
          },
        });
        return false;
      }
      const speech = new (window.SpeechRecognition ||
        window.webkitSpeechRecognition)();

      let isRecognizing = false;
      let ignoreEndProcess = false;
      let finalTranscript = "";

      // 오류를 막기 위한 console
      console.log(isRecognizing);
      // 원하는 언어를 앞으로 뺴라(해당 언어만 지원)
      // 비어있으면 영어, 한국어 둘 다 지원(한국어 우선)
      speech.lang = ["ko-KR", "en-US"];
      speech.continuous = true;
      speech.interimResults = true;

      speech.onstart = function() {
        isRecognizing = true;
      };

      speech.onend = function() {
        isRecognizing = false;

        if (ignoreEndProcess) {
          return false;
        }

        if (!finalTranscript) {
          return false;
        }
      };

      speech.onresult = (event) => {
        const talker = this.answerIdx;
        let interimTranscript = "";
        this.interimTranscript = "";
        if (typeof event.results === "undefined") {
          speech.onend = null;
          speech.stop();
          return;
        }

        for (let i = event.resultIndex; i < event.results.length; ++i) {
          const transcript = event.results[i][0].transcript;

          if (event.results[i].isFinal) {
            finalTranscript += transcript;
            this.finalTranscript += transcript;
            // console.log('파이널', finalTranscript);
            // console.log('파이널', transcript);
          } else {
            interimTranscript += transcript;
            this.interimTranscript += transcript;
          }
        }
        if (this.isRecording) {
          this.stompClient.send(
            `/pub/speaking/talk/${this.getRoomId}`,
            {},
            JSON.stringify({
              sentence: finalTranscript + interimTranscript,
              talker: talker,
            })
          );
        }
      };

      speech.onerror = function(event) {
        if (event.error.match(/no-speech|audio-capture|not-allowed/)) {
          ignoreEndProcess = true;
        }
      };

      const record = document.querySelector("#record");
      record.addEventListener("click", async () => {
        if (
          this.getUser.name !==
            this.room.teams[this.room.teamOrder[0]].members[this.answerIdx]
              .participantName &&
          !this.isRecording
        ) {
          this.isRecording = true;
          return;
        } else if (
          this.getUser.name !==
            this.room.teams[this.room.teamOrder[0]].members[this.answerIdx]
              .participantName &&
          this.isRecording
        ) {
          this.isRecording = false;
        } else {
          const speakImg = document.querySelectorAll(".member-name > img");
          if (this.isRecording) {
            let message = finalTranscript;
            this.setMic(2);

            // 마이크 버튼을 눌서 finalTranscript가 없을 때
            if (finalTranscript === this.finalTranscript) {
              message = this.finalTranscript + this.interimTranscript;
            }

            this.stompClient.send(
              `/pub/speaking/answer/${this.getRoomId}`,
              {},
              JSON.stringify({
                name: this.getUser.name,
                teamNo: this.getUser.teamNo,
                message: message,
                correct: false,
              })
            );
            finalTranscript = "";
            this.finalTranscript = "";
            this.isRecording = false;
            this.talkFinish = true;
            speech.stop();
          } else {
            this.setMic(1);
            speakImg[this.answerIdx].src = this.speakImg[1];
            finalTranscript = "";
            this.finalTranscript = "";
            const sentenceBox = document.querySelectorAll(
              ".sentence-board > .sentence-board-child > .member-sentence > span"
            )[this.answerIdx];
            sentenceBox.innerText = "";
            this.isRecording = true;
            this.talkFinish = false;
            this.stompClient.send(
              `/pub/speaking/talk/${this.getRoomId}`,
              {},
              JSON.stringify({
                sentence: "",
                talker: this.answerIdx,
              })
            );
            speech.start();
          }
        }
      });

      // const stop = document.querySelector('#stop');
      // console.log(stop.src);
      // stop.addEventListener("click", () => {
      // })

      speech.addEventListener("result", (event) => {
        const transcript = event["results"];
        // this.mediaRecorder.stop();
        return transcript;
      });
    },
    // 멤버 설정
    setTeamMembers: async function() {
      this.teamMember = this.room.teams[this.room.teamOrder[0]].members;
    },
    nextTurn: function() {
      this.stompClient.send("/pub/speaking/next/team", {}, this.getRoomId);
      this.userAnswerIdx++;
      this.answerIdx++;
    },
    setMic: function(background) {
      const record = document.querySelector("#record");
      // record.src = require(this.micImg[1]);
      this.showMic = true;
      if (background === 0) {
        record.src = this.micImg[1];
        return;
      } else if (background === 1) {
        record.src = this.micImg[3];
        // record.className = 'game-mic-default game-mic-off';
        return;
      } else if (background === 2) {
        record.src = this.micImg[2];
        // record.className = 'game-mic-default game-mic-on';
        return;
      }
      // before
      // game-mic 클래스 없애주기
      // 사진 mic[0]으로

      // after
      // game-mic 클래스 넣어주기
      // 사진 mic[1]로
    },
    removeMic: function() {
      const record = document.querySelector("#record");
      // record.src = 'https://k5a302.p.ssafy.io/img/mic/mic-disabled.png';
      this.showMic = false;
      record.classList.remove("game-mic-on");
      record.classList.remove("game-mic-off");
    },
    setAudio: async function() {
      this.connection.socketURL = 'https://rtcmulticonnection.herokuapp.com:443/';
      this.connection.socketMessageEvent = 'audio-conference-demo';
      this.connection.session = {
        audio: true,
        video: false
      };
      this.connection.mediaConstraints = {
        audio: true,
        video: false
      };
      this.connection.sdpConstraints.mandatory = {
        OfferToReceiveAudio: true,
        OfferToReceiveVideo: false
      };
      this.connection.iceServers = [{
        'urls': [
          'stun:stun.l.google.com:19302',
          'stun:stun1.l.google.com:19302',
          'stun:stun2.l.google.com:19302',
          'stun:stun.l.google.com:19302?transport=udp',
        ]
      }];
      this.connection.audiosContainer = document.getElementById('audios-container');
      this.connection.onstream = function(event) {
        var width = parseInt(this.connection.audiosContainer.clientWidth / 2) - 20;
        var mediaElement = getHTMLMediaElement(event.mediaElement, {
          title: event.userid,
          buttons: ['full-screen'],
          width: width,
          showOnMouseEnter: false
        });

        this.connection.audiosContainer.appendChild(mediaElement);

        setTimeout(function() {
          mediaElement.media.play();
        }, 5000);

        mediaElement.id = event.streamid;
      };
      this.connection.onstreamended = function(event) {
        var mediaElement = document.getElementById(event.streamid);
        if (mediaElement) {
          mediaElement.parentNode.removeChild(mediaElement);
        }
      };

      // (function() {
      //   var params = {},
      //     r = /([^&=]+)=?([^&]*)/g;

      //   function d(s) {
      //     return decodeURIComponent(s.replace(/\+/g, ' '));
      //   }
      //   var match, search = window.location.search;
      //   while (match = r.exec(search.substring(1)))
      //     params[d(match[1])] = d(match[2]);
      //   window.params = params;
      // })();

      // var roomid = '';
      // if (localStorage.getItem(this.connection.socketMessageEvent)) {
      //     roomid = localStorage.getItem(this.connection.socketMessageEvent);
      // } else {
      //     roomid = this.connection.token();
      // }

      // var hashString = location.hash.replace('#', '');
      // if (hashString.length && hashString.indexOf('comment-') == 0) {
      //   hashString = '';
      // }

      // var roomid = params.roomid;
      // if (!roomid && hashString.length) {
      //   roomid = hashString;
      // }
      // if (roomid && roomid.length) {
      //   document.getElementById('room-id').value = roomid;
      //   localStorage.setItem(this.connection.socketMessageEvent, roomid);

      //   // auto-join-room
      //   (function reCheckRoomPresence() {
      //     this.connection.checkPresence(roomid, function(isRoomExist) {
      //       if (isRoomExist) {
      //         this.connection.join(roomid);
      //         return;
      //       }

      //       setTimeout(reCheckRoomPresence, 5000);
      //     });
      //   })();
      // }
              // 방장인 경우 소켓통신 방 만들기
      // if (this.room.host === this.getUser.id) {
      //   this.connection.open('b1', function() {
      //   });
      // // 참여자는 방에 참여하기
      // } else {
      // }
    },
    makeAudioRoom: function() {
      this.connection.openOrJoin(this.getRoomId);
      this.isRoomOpened = true;
      this.isRoomJoined = true;
    },
    enterAudioRoom: function() {
      this.connection.join(this.getRoomId);
      this.isRoomJoined = true;
    },
    giveAudio: function() {
      // 마이크 주고 뻇기
    },
    /**
     * 소켓 통신
     */
    async onConnected() {
      // 방 정보 교환 채널
      await this.stompClient.subscribe(
        "/speaking/" + this.getRoomId,
        this.onMessageReceived
      );
      // 정답 데이터 채널
      this.stompClient.subscribe(
        "/speaking/answer/" + this.getRoomId,
        this.onAnswerMessageReceived
      );
      // 현재 진행 중인 사람의 문장 전송
      this.stompClient.subscribe(
        "/speaking/talk/" + this.getRoomId,
        this.onTalkingMessageReceived
      );
      // 플레이어 변경
      await this.stompClient.subscribe(
        `/speaking/change/player/` + this.getRoomId,
        this.onChangePlayer
      );
      // 다음 팀으로
      // this.stompClient.subscribe(`/speaking/next/team/` + this.getRoomId, this.onChangeTeam);
      // 모달 정보 교환
      this.stompClient.subscribe(
        "/ssafymind/close/modal/" + this.getRoomId,
        this.onModalMessageReceived
      );

      // 입장 시 데이터 수신
      await this.stompClient.send(
        "/pub/speaking/enter",
        {},
        JSON.stringify({
          roomId: this.getRoomId,
          participantId: this.getUser.id,
          participantName: this.getUser.name,
          teamNo: this.getUser.teamNo,
        })
      );

    },
    async onMessageReceived(payload) {
      if (payload.body == "exit") {
        this.$router.push("/room/" + this.getRoomId).catch(() => {});
        this.stompClient.disconnect();
        swal({
          title: "방장이 퇴장하여 게임이 종료됩니다!",
          icon: "/img/ssazip-logo.png",
          buttons: {
            text: "확인",
          },
        });
        return;
      }
      const data = JSON.parse(payload.body);
      this.room = data;
      
      if (!this.isRoomOpened && data.host === this.getUser.id) {
        this.makeAudioRoom();
      }

      // 나가는 로직
      if (data.teamOrder[0] == null) {
        // 랭킹 모달 띄우기
        this.rankmodal = true;
        return;
      }

      console.log(this.room);
      this.quiz = data.quizzes[data.quizzes.length - 1].problem;
      this.answerIdx = 0;
      await this.setTeamMembers();
      if (
        data.teams[data.teamOrder[0]].members.length != 0 &&
        this.getUser.name ===
          data.teams[data.teamOrder[0]].members[0].participantName
      ) {
        // 마이크 권한 주기
        this.giveAudio(); // 이전 사람과 이번 턴의 index
        // 마이크 아이콘 변경
        this.setMic(2);
      }
      // span 싹 지워주기
      const sentences = document.querySelectorAll(
        ".sentence-board > .sentence-board-child > .member-sentence > span"
      );
      const speakImg = document.querySelectorAll(".member-name > img");

      sentences.forEach((sentence, idx) => {
        sentence.innerText = "";
        speakImg[idx].src = this.speakImg[0];
      });
    },
    onDisconnect() {
      let isHost;
      if (this.room.host == this.getUser.id) isHost = true;
      else isHost = false;
      this.stompClient.send(
        "/pub/speaking/exit",
        {},
        JSON.stringify({
          roomId: this.getRoomId,
          participantId: this.getUser.id,
          participantName: this.getUser.name,
          host: isHost,
        })
      );
    },
    async onAnswerMessageReceived(payload) {
      this.talkFinish = true;
      const data = JSON.parse(payload.body);
      const speakImg = document.querySelectorAll(".member-name > img");
      const sentenceBox = document.querySelectorAll(
        ".sentence-board > .sentence-board-child > .member-sentence > span"
      )[this.answerIdx];
      sentenceBox.innerText = await data.message;
      if (data.correct) {
        speakImg[this.answerIdx].src = this.speakImg[3];
        if (
          this.getUser.name ===
          this.room.teams[this.room.teamOrder[0]].members[this.answerIdx]
            .participantName
        ) {
          this.setMic(0);
          this.removeMic();
          this.stompClient.send(
            "/pub/speaking/change/player",
            {},
            this.getRoomId
          );
        }
      } else {
        speakImg[this.answerIdx].src = this.speakImg[2];
      }
    },
    onTalkingMessageReceived(payload) {
      const data = JSON.parse(payload.body);
      if (
        this.talker !== this.answerIdx &&
        data.talker !== this.answerIdx &&
        this.talker !== data.talker
      ) {
        this.talker = data.talker;
        return;
      }
      if (data.sentence === "") {
        this.talkFinish = false;
      }
      if (!this.talkFinish || data.sentence === "") {
        const speakImg = document.querySelectorAll(".member-name > img");
        speakImg[this.answerIdx].src = this.speakImg[1];
        const sentenceBox = document.querySelectorAll(
          ".sentence-board > .sentence-board-child > .member-sentence > span"
        )[this.answerIdx];
        sentenceBox.innerText = data.sentence;
      }
    },
    onChangePlayer(payload) {
      const data = JSON.parse(payload.body);
      this.isRecording = false;
      this.talkFinish = false;
      // 다음 팀으로 넘겨야 하는 경우
      if (
        data === 0
        // && this.answerIdx + 1 === this.room.teams[this.room.teamOrder[0]].members.length
      ) {
        this.answermodal = true;
        this.timer = true;
        if (
          this.getUser.name ===
          this.room.teams[this.room.teamOrder[0]].members[this.answerIdx]
            .participantName
        ) {
          this.stompClient.send("/pub/speaking/next/team", {}, this.getRoomId);
          this.answerIdx = data;
          this.setMic(0);
          this.removeMic();
        }
        // 다른 선수로 넘겨야 하는 경우
      } else {
        this.answerIdx = data;
        const speakImg = document.querySelectorAll(".member-name > img");
        speakImg[data].src = this.speakImg[0];
        if (
          this.getUser.name ===
          this.room.teams[this.room.teamOrder[0]].members[data].participantName
        ) {
          // 마이크 권한 주기
          this.giveAudio(); // 이전 사람과 이번 턴의 index
          // 마이크 아이콘 변경
          this.setMic(2);
        }
      }
    },
    // onChangeTeam(payload) {
    //   const data = JSON.parse(payload.body);
    //   // 팀원들 싹 다시 뿌려주기
    //   this.room = data;
    //   this.setMic(2); // 이전 사람과 이번 팀의 index
    //   // answerIdx 바꿔주기
    //   this.answerIdx = 0;
    //   // span 싹 지워주기
    //   const sentences = document.querySelectorAll('.sentence-board > .sentence-board-child > .member-sentence > span');
    //   // 0번에게 마이크 권한
    // },
    onModalMessageReceived(payload) {
      const flag = JSON.parse(payload.body);
      this.ordermodal = flag;
      this.answermodal = flag;
      this.timer = false;
    },
    onError() {},
    sendGameStartTrigger: function() {
      this.stompClient.send(`/pub/ssafymind/close/modal`, {}, this.getRoomId);
      this.enterAudioRoom();
    },
    getCloseAnsModal: function(answermodal) {
      this.answermodal = answermodal;
      this.stompClient.send(`/pub/ssafymind/close/modal`, {}, this.getRoomId);
    },
    getCloseRankModal: function(rankmodal) {
      this.rankmodal = rankmodal;
    },
    // 대기실로 돌아가는 트리거
    sendGoWaitingRoomTrigger() {
      this.stompClient.send("/pub/speaking/end", {}, this.getRoomId);
      this.onDisconnect();
    },
  },
};
</script>
