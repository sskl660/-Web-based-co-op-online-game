<template>
  <div>
    <div class="speak-game">
      <Header v-bind:gameTitle="'또박또박 말해요'" :host="getUser.id"/>
      <div class="game-screen">
        <GameStatus game="speak" />
        <div class="game-board">
          <p class="sentence">"삼성 청년 소프트웨어 아카데미"</p>
          <div v-for="(member, i) in teamMember" :key="i" class="sentence-board" id="sentence-board">
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
          <img :src="micImg[0]" alt="mic img" class="game-mic-default" id="record" />
        </div>
      </div>
    </div>

    <!-- 아래는 나중에 사용할 아이들 -->
    <div style="display:none;">
      <p>녹음하기</p>
      <input type="checkbox" id="chk-hear-mic" /><label for="chk-hear-mic">마이크 소리 듣기</label>
      <!-- <button id="record">녹음</button> -->
      <!-- <button id="stop">녹음 정지</button> -->
      <span id="final_span"></span>
      <span id="interim_span"></span>
      <div id="sound-clips"></div>
    </div>
  </div>
</template>

<script>
import '@/css/speaking-game.css';
import { mapGetters } from 'vuex';
import { socketConnect } from '@/util/socket-common.js';
import Header from '@/components/common/Header.vue';
import GameStatus from '@/components/GameStatus.vue';

export default {
  name: 'SpeakGame',
  components: {
    Header,
    GameStatus,
  },
  data: () => {
    return {
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
      room: null,
      interimTranscript: '',
      finalTranscript: '',
      teamMember: [],
      answerIdx: 0,
      speakImg: [
        '/img/speak/speak-wait.png',
        '/img/speak/speak-loading.png',
        '/img/speak/speak-fail.png',
        '/img/speak/speak-success.png',
      ],
      micImg: [
        '/img/mic/mic-disabled.png',
        '/img/mic/mic-off.png',
        '/img/mic/mic-on.png',
      ]
    };
  },
  created() {
    history.pushState(null, null, location.href);
    window.onpopstate = () => {
      history.go(1);
      alert(`게임 내에서는 '뒤로가기'가 불가능합니다.`)
    }
    this.stompClient = socketConnect(this.onConnected, this.onError);
  },
  mounted() {
    // this.getAudio();
    this.translate();
  },
  destroyed() {
    // this.onDisconnect();
  },
  watch: {},
  computed: {
    ...mapGetters(['getUser', 'getRoomId']),
  },
  methods: {
    /*  */
    // 음성을 텍스트로 번역
    /*  */
    translate: function() {
      if (typeof webkitSpeechRecognition !== 'function') {
        alert('크롬에서만 동작합니다');
        return false;
      }
      const speech = new (window.SpeechRecognition || window.webkitSpeechRecognition)();

      let isRecognizing = false;
      let ignoreEndProcess = false;
      let finalTranscript = '';

      // 원하는 언어를 앞으로 뺴라(해당 언어만 지원)
      // 비어있으면 영어, 한국어 둘 다 지원(한국어 우선)
      speech.lang = ['ko-KR', 'en-US'];
      speech.continuous = true;
      speech.interimResults = true;

      speech.onstart = function() {
        isRecognizing = true;
      };

      speech.onend = function() {
        isRecognizing = false;
        console.log(isRecognizing);

        if (ignoreEndProcess) {
          return false;
        }

        if (!finalTranscript) {
          return false;
        }
      };


      speech.onresult = (event) => {
        let interimTranscript = '';
        this.interimTranscript = '';
        if (typeof event.results === 'undefined') {
          speech.onend = null;
          speech.stop();
          return;
        }

        for (let i = event.resultIndex; i < event.results.length; ++i) {
          const transcript = event.results[i][0].transcript;

          if (event.results[i].isFinal) {
            finalTranscript += transcript;
            this.finalTranscript += transcript;
            console.log('파이널', finalTranscript);
            console.log('파이널', transcript);
          } else {
            interimTranscript += transcript;
            this.interimTranscript += transcript;
          }
        }
        this.stompClient.send(
          `/pub/speaking/talk/${this.getRoomId}`,
          {},
          JSON.stringify({
            sentence: finalTranscript + interimTranscript,
          })
        );
      };

      speech.onerror = function(event) {
        if (event.error.match(/no-speech|audio-capture|not-allowed/)) {
          ignoreEndProcess = true;
        }
      };

      const record = document.querySelector('#record');
      record.addEventListener('click', async () => {
        if (this.getUser.name !== this.room.teams[this.room.teamOrder[0]].members[this.answerIdx].participantName) return;
        const speakImg = document.querySelectorAll('.member-name > img');
        if (this.isRecording) {
          let message = finalTranscript;
          record.src = this.micImg[2];

          // 마이크 버튼을 눌서 finalTranscript가 없을 때
          if (finalTranscript === this.finalTranscript) {
            message = this.finalTranscript + this.interimTranscript;
          }
          console.log('-----------------');
          console.log(message);
          console.log('-----------------');

          this.stompClient.send(
            `/pub/speaking/answer/${this.getRoomId}`,
            {},
            JSON.stringify({
              name: '안기훈',
              teamNo: 1,
              message: message,
              correct: false,
            })
          );
          finalTranscript = '';
          this.finalTranscript = '';
          this.isRecording = false;
          speech.stop();
        } else {
          record.src = this.micImg[1];
          speakImg[this.answerIdx].src = this.speakImg[1];
          finalTranscript = '';
          this.finalTranscript = '';
          const sentenceBox = document.querySelectorAll('.sentence-board > .sentence-board-child > .member-sentence > span')[this.answerIdx];
          sentenceBox.innerText = '';
          this.isRecording = true;
          speech.start();
        }
      });

      // const stop = document.querySelector('#stop');
      // console.log(stop.src);
      // stop.addEventListener("click", () => {

      // })

      speech.addEventListener('result', (event) => {
        const transcript = event['results'];
        // this.mediaRecorder.stop();
        console.log(transcript);
      });
    },
    // 멤버 설정
    setTeamMembers: function() {
      this.teamMember = this.room.teams[this.room.teamOrder[0]].members
    },
    nextTurn: function() {
      this.stompClient.send('/pub/speaking/next/team', {}, this.getRoomId)
      this.userAnswerIdx++;
      this.answerIdx++;
    },
    setMic: function(before, after) {
      console.log(before, after)
      // before
      // game-mike 클래스 없애주기
      // 사진 mic[0]으로
      // after가 0이면 새로 뿌려주기 때문에 위 2개는 할 필요가 없음

      // after
      // game-mike 클래스 넣어주기
      // 사진 mic[1]로
    },
    /**
     * 소켓 통신
     */
    async onConnected() {
      // 방 정보 교환 채널
      this.stompClient.subscribe('/speaking/' + this.getRoomId, this.onMessageReceived);
      // 정답 데이터 채널
      this.stompClient.subscribe('/speaking/answer/' + this.getRoomId, this.onAnswerMessageReceived);
      // 현재 진행 중인 사람의 문장 전송
      this.stompClient.subscribe('/speaking/talk/' + this.getRoomId, this.onTalkingMessageReceived);
      // 플레이어 변경
      this.stompClient.subscribe(`/speaking/change/player/` + this.getRoomId, this.onChangePlayer);
      // 다음 팀으로
      this.stompClient.subscribe(`/speaking/next/team/` + this.getRoomId, this.onChangeTeam);

      // 입장 시 데이터 수신
      this.stompClient.send(
        '/pub/speaking/enter',
        {},
        JSON.stringify({
          roomId: this.getRoomId,
          participantId: this.getUser.id,
          participantName: this.getUser.name,
          teamNo: this.getUser.teamNo,
        })
      );
    },
    onMessageReceived(payload) {
      // if (payload.body == 'exit') {
      //   // 모든 참가자의 연결을 끊고
      //   this.onDisconnect();
      //   alert('방장이 퇴장하여 게임이 종료됩니다!');
      //   // 모든 참가자 내보내기
      //   this.$router.push('/room/' + this.getRoomId);
      //   return;
      // }
      const data = JSON.parse(payload.body);  
      this.room = data;
      this.setTeamMembers();
    },
    onDisconnect() {
      // this.stompClient.send(
      //   '/pub/speaking/exit',
      //   {},
      //   JSON.stringify({
      //     roomId: this.getRoomId,
      //     participantId: this.getUser.id,
      //     participantName: this.getUser.name,
      //   })
      // );
      // this.stompClient.disconnect();
      // this.$router.push('/room/', this.getRoomId);
    },
    onAnswerMessageReceived(payload) {
      const data = JSON.parse(payload.body);
      const speakImg = document.querySelectorAll('.member-name > img');
      const sentenceBox = document.querySelectorAll('.sentence-board > .sentence-board-child > .member-sentence > span')[this.answerIdx];
      sentenceBox.innerText = data.message;
      console.log(data)
      if (data.correct) {
        speakImg[this.answerIdx].src = this.speakImg[3];
        this.stompClient.send('/pub/speaking/change/player', {}, this.getRoomId);
      } else {
        speakImg[this.answerIdx].src = this.speakImg[2];
      }
    },
    onTalkingMessageReceived(payload) {
      console.log(this.isRecording);
      if (this.isRecording) {
        const data = JSON.parse(payload.body);
        const sentenceBox = document.querySelectorAll('.sentence-board > .sentence-board-child > .member-sentence > span')[this.answerIdx];
        sentenceBox.innerText = data.sentence;
        console.log(data.sentence);
      }
    },
    onChangePlayer(payload) {
      const data = JSON.parse(payload.body);
      // 다음 팀으로 넘겨야 하는 경우
      if (data + 1 === this.room.teamOrder[0].members.length) {
        this.stompClient.send('/pub/speaking/next/team', {}, this.getRoomId);
      // 다른 선수로 넘겨야 하는 경우
      } else {
        this.answerIdx = data;
        if (this.getUser.name === this.room.teamOrder[0].members[data]) {
          // 마이크 권한 주기
          this.setMic(data-1, data); // 이전 사람과 이번 턴의 index
        }
      }
    },
    onChangeTeam(payload) {
      const data = JSON.parse(payload.body);
      this.room = data;
      this.setMic(this.answerIdx, 0); // 이전 사람과 이번 팀의 index
      this.answerIdx = 0;
      // answerIdx 바꿔주기
      // 팀원들 싹 다시 뿌려주기
      // span 싹 지워주기
      // 0번에게 마이크 권한
    },
    onError() {},
    /*  */
    // 음성 녹음해 blob 파일로 만들기
    /*  */
    getAudio: function() {
      this.record = document.getElementById('record');
      this.stop = document.getElementById('stop');
      this.soundClips = document.getElementById('sound-clips');
      this.chkHearMic = document.getElementById('chk-hear-mic');

      const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      this.audioCtx = audioCtx;

      const analyser = audioCtx.createAnalyser();
      this.analyser = analyser;

      function makeSound(stream) {
        const source = this.audioCtx.createMediaStreamSource(stream);
        source.connect(this.analyser);
        this.analyser.connect(this.audioCtx.destination);
      }

      if (navigator.mediaDevices) {
        console.log('getUserMedia supported.');

        navigator.mediaDevices
          .getUserMedia({ audio: true })
          .then((stream) => {
            const mediaRecorder = new MediaRecorder(stream);
            this.mediaRecorder = mediaRecorder;
            this.chkHearMic.onchange = (e) => {
              if (e.target.checked == true) {
                audioCtx.resume();
                makeSound(stream);
              } else {
                audioCtx.suspend();
              }
            };
            this.record.onclick = () => {
              this.mediaRecorder.start();
              console.log(this.mediaRecorder.state);
            };
            this.stop.onclick = () => {
              this.mediaRecorder.stop();
              console.log(this.mediaRecorder.state);
            };

            this.mediaRecorder.onstop = () => {
              console.log('data available after MediaRecorder.stop() called.');

              const clipName = prompt('오디오 파일 제목을 입력하세요.', new Date());

              const clipContainer = document.createElement('article');
              const clipLabel = document.createElement('p');
              const audio = document.createElement('audio');
              const deleteButton = document.createElement('button');

              clipContainer.classList.add('clip');
              audio.setAttribute('controls', '');
              deleteButton.innerHTML = '삭제';
              clipLabel.innerHTML = clipName;

              clipContainer.appendChild(audio);
              clipContainer.appendChild(clipLabel);
              clipContainer.appendChild(deleteButton);
              this.soundClips.appendChild(clipContainer);
              audio.controls = true;
              const blob = new Blob(this.chunks, {
                type: 'audio/ogg codecs=opus',
                // type: 'audio/wav; codecs=0'
              });
              console.log(blob);
              this.chunks = [];
              const audioURL = URL.createObjectURL(blob);
              audio.src = audioURL;
              console.log(audio);
              console.log('recorder stopped');

              deleteButton.onclick = (e) => {
                const evtTgt = e.target;
                evtTgt.parentNode.parentNode.removeChild(evtTgt.parentNode);
              };
            };

            this.mediaRecorder.ondataavailable = (e) => {
              this.chunks.push(e.data);
              console.log(this.chunks);
            };
          })
          .catch((err) => {
            console.log('The following error occurred: ' + err);
          });
      }
    },
  },
};
</script>

<style></style>
