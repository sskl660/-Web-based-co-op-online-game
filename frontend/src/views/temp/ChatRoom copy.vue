<template>
  <div>
    <h1>'{{ name }}' 채팅방</h1>
    <div>
      <input
        type="text"
        placeholder="메세지를 입력하세요"
        v-model="message"
        @keyup.enter="sendMessage"
      />
      <div v-for="(msg, idx) in receivedMessages" :key="idx">
        <p>{{ msg.message }}</p>
      </div>
    </div>
    <hr />
    <!-- <div>
      <input type="checkbox" id="chk-hear-mic"><label for="chk-hear-mic">마이크 소리 듣기</label>
      <button id="record">녹음</button>
      <button id="stop">중지</button>
      <div id="sound-clips"></div>
    </div> -->
    <div>
      <p>비디오/오디오 통신</p>
      <!-- <video src="@/assets/images/back.mp4" id="myFace" style="width: 400px; border: 1px solid black;" autoplay playsinline/> -->
      <video id="myFace" style="width: 400px; border: 1px solid black;" autoplay playsinline />
      <video id="peerFace" style="width: 400px; border: 1px solid black;" autoplay playsinline />
      <video id="myFace2" style="width: 400px; border: 1px solid black;" autoplay playsinline />
      <br />
      <button id="video">비디오 On</button>
      <button id="audio">소리 On</button>
      <select name="" id="cameras"></select>
      <button id="cameraBtn">적용</button>
    </div>
  </div>
</template>

<script>
import { socketConnect } from '@/util/socket-common.js';

export default {
  name: 'Chat',
  data() {
    return {
      id: '',
      name: '',
      message: '',
      stompClient: null,
      receivedMessages: [],
      record: {},
      stop: {},
      soundClips: {},
      chkHearMic: {},
      audioCtx: {},
      analyser: {},
      chunks: [],
      video: false,
      audio: false,
      myStream: {},
      myPeerConnection: {},
      getOffer: false,
      getAnswer: false,
      dataStream: {},
    };
  },
  created() {
    this.id = this.$route.query.id;
    this.name = this.$route.query.name;
    // console.log(this.stompClient);
  },
  mounted() {
    this.stompClient = socketConnect(this.onConnected, this.onError);
    this.socketio();
  },
  methods: {
    socketio: async function() {
      let myStream;
      const myFace = document.querySelector("#myFace");
      const myFace2 = document.querySelector("#myFace2");
      const video = document.querySelector("#video");
      const audio = document.querySelector("#audio");
      const cameraSelect = document.querySelector('#cameras');
      const cameraBtn = document.querySelector('#cameraBtn');

      async function getMikes() {
        const devices = await navigator.mediaDevices.enumerateDevices();
        const cameras = devices.filter(device => device.kind === 'videoinput');
        const currentCamera = myStream.getVideoTracks()[0];
        cameras.forEach(camera => {
          const option = document.createElement("option");
          option.value = camera.deviceId;
          option.innerText = camera.label;
          if(currentCamera.label == camera.label) {
            option.selected = true;
          }
          cameraSelect.appendChild(option);
        });
      }
      
      const getMedia = async (deviceId) => {
        const initialConstaints = {
          audio: false,
          video: { facingMode: "user" },
        };
        const cameraConstraints = {
          audio: false,
          video: { deviceId: { exact: deviceId } },
        }
        try {
          myStream = await navigator.mediaDevices.getUserMedia(
            deviceId ? cameraConstraints : initialConstaints
          )
          myFace.srcObject = myStream;
          if(!deviceId) {
            await getMikes();
          }
          this.myStream = myStream;
        } catch (e) {
          console.log(e);
        }
      }

      const initCall = async () => {
        await getMedia();
        makeConnection();
      }

      const makeConnection = async () => {
        console.log(this.myPeerConnection);
        console.log(this.myPeerConnection);
        console.log(this.myPeerConnection);
        console.log(this.myPeerConnection);
        console.log(this.myPeerConnection);
        this.myPeerConnection = new RTCPeerConnection({
          iceServers: [
            {
              urls: [
                "stun:stun.l.google.com:19302",
                "stun:stun2.l.google.com:19302",
                "stun:stun3.l.google.com:19302",
                "stun:stun4.l.google.com:19302",
                "stun:stun5.l.google.com:19302",
              ]
            }
          ],
        });
        this.myPeerConnection.addEventListener("icecandidate", (data) => {
          console.log(data)
          this.stompClient.send(
            '/pub/chat/audio',
            {},
            JSON.stringify({ roomId: this.id, offer: data.candidate, writer: '김태현' })
          );
        });
        this.myPeerConnection.addEventListener("addstream", (data) => {
          const peerFace = document.getElementById("peerFace");
          this.dataStream = data.stream;

          console.log('---------------');
          console.log('This is peer stream');
          console.log(data.stream);
          console.log('---------------');
          console.log('This is My Stream');
          console.log(this.myStream);
          console.log('---------------');

          peerFace.srcObject = this.dataStream;
          myFace2.srcObject = myStream;
        });
        myStream
          .getTracks()
          .forEach((track) => this.myPeerConnection.addTrack(track, myStream));
      }

      video.addEventListener('click', () => {
        this.myStream.getVideoTracks().forEach((track) => (track.enabled = !track.enabled));
        if (this.video) {
          video.innerText = '비디오 On';
          this.video = false;
        } else {
          video.innerText = '비디오 Off';
          this.video = true;
        }
      });

      audio.addEventListener('click', () => {
        this.myStream.getAudioTracks().forEach((track) => (track.enabled = !track.enabled));
        if (this.audio) {
          audio.innerText = '오디오 On';
          this.audio = false;
        } else {
          audio.innerText = '오디오 Off';
          this.audio = true;
        }
      });

      cameraBtn.addEventListener("click", () => {
        this.useMike();
      });

      cameraSelect.addEventListener("input", async () => {
        await getMedia(cameraSelect.value);
      });

      await initCall();

    },
    // 채팅 채널 구독 및 입장 메세지 출력
    onConnected() {
      this.stompClient.subscribe('/chat/room/' + this.id, this.onMessageReceived);
      this.stompClient.send(
        '/pub/chat/message',
        {},
        JSON.stringify({ roomId: this.id, message: '입장', writer: '김태현' })
      );
    },
    // 소켓 연결 해제
    socketDisconnect() {
      this.stompClient.socketDisconnect();
    },

    async useMike() {
      const offer = await this.myPeerConnection.createOffer();
      this.myPeerConnection.setLocalDescription(offer);
      console.log("sent the offer");
      this.stompClient.send(
        '/pub/chat/audio',
        {},
        JSON.stringify({ roomId: this.id, offer: offer, writer: '김태현' })
      );
    },
    // 메세지 발신
    // async sendMessage() {
    //   if (this.message.trim() && this.stompClient) {
    //     console.log(this.id)
    //     // const offer = await this.myPeerConnection.createOffer();
    //     // this.myPeerConnection.setLocalDescription(offer);
    //     this.stompClient.send(
    //       '/pub/chat/message',
    //       {},
    //       JSON.stringify({ roomId: this.id, message: this.message, writer: '김태현' })
    //     );
    //     this.message = '';
    //   }
    // },
    async sendOffer(offer) {
      console.log("received the offer");
      await this.myPeerConnection.setRemoteDescription(offer);
      const answer = await this.myPeerConnection.createAnswer();
      await this.myPeerConnection.setLocalDescription(answer);
      if (offer.type && this.stompClient) {
        await this.stompClient.send(
          '/pub/chat/audio',
          {},
          JSON.stringify({ roomId: this.id, offer: answer, writer: this.user.name })
        )
        console.log("sent the answer");
      }
    },

    async sendAnswer(answer) {
      console.log("received the answer");
      console.log('==========================================')
      await this.myPeerConnection.setRemoteDescription(answer);
      console.log(this.myPeerConnection)
      console.log('==========================================')
    },

    async sendIce(ice) {
      console.log("received the candidate");
      await this.myPeerConnection.addIceCandidate(ice);
    },

    // 메세지 수신
    onMessageReceived(payload) {
      let receiveMessage = JSON.parse(payload.body);
      if (receiveMessage.offer) {
        if (receiveMessage.writer == '안기훈') return
        if (receiveMessage.offer.type === 'offer') {
          this.sendOffer(receiveMessage.offer);
          return
        } else if (receiveMessage.offer.type === 'answer') {
          this.sendAnswer(receiveMessage.offer)
          return
        } else if (receiveMessage.offer.candidate) {
          this.sendIce(receiveMessage.offer)
          return
        }
      } else return;
      this.receivedMessages.push(receiveMessage);
      console.log(receiveMessage);
    },
    // 에러 수신
    onError() {},
  },
};
</script>

<style></style>
