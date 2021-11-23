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
      <input type="text" id="room-id" value="abcdef" autocorrect=off autocapitalize=off size=20>
      <button id="open-room">Open Room</button>
      <button id="join-room">Join Room</button>
      <div id="room-urls" style="text-align: center;display: none;background: #F1EDED;margin: 15px -10px;border: 1px solid rgb(189, 189, 189);border-left: 0;border-right: 0;"></div>
      <div id="audios-container"></div>
      <button id="audio">소리 On</button>
      <button id="cameraBtn">적용</button>
    </div>
  </div>
</template>

<script src="/node_modules/webrtc-adapter/out/adapter.js"></script>
<script src="https://rtcmulticonnection.herokuapp.com/dist/RTCMultiConnection.min.js"></script>
<script src="https://rtcmulticonnection.herokuapp.com/socket.io/socket.io.js"></script>
<script>

// import { socketConnect } from '@/util/socket-common.js';

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
    };
  },
  created() {
    this.id = this.$route.query.id;
    this.name = this.$route.query.name;
    // console.log(this.stompClient);
  },
  mounted() {
    // this.stompClient = socketConnect(this.onConnected, this.onError);
    this.socketio();
  },
  methods: {
    socketio: async function() {
      const connection = new RTCMultiConnection();

      document.getElementById('open-room').onclick = function() {
        connection.open(document.getElementById('room-id').value, function() {
          showRoomURL(connection.sessionid);
        });
      };

      document.getElementById('join-room').onclick = function() {
        connection.join(document.getElementById('room-id').value);
      };

      connection.socketURL = 'https://rtcmulticonnection.herokuapp.com:443/';

      connection.socketMessageEvent = 'audio-conference-demo';
      connection.session = {
        audio: true,
        video: false
      };
      connection.mediaConstraints = {
        audio: true,
        video: false
      };
      connection.sdpConstraints.mandatory = {
        OfferToReceiveAudio: true,
        OfferToReceiveVideo: false
      };
      connection.iceServers = [{
        'urls': [
          'stun:stun.l.google.com:19302',
          'stun:stun1.l.google.com:19302',
          'stun:stun2.l.google.com:19302',
          'stun:stun.l.google.com:19302?transport=udp',
        ]
      }];
      connection.audiosContainer = document.getElementById('audios-container');
      connection.onstream = function(event) {
        var width = parseInt(connection.audiosContainer.clientWidth / 2) - 20;
        var mediaElement = getHTMLMediaElement(event.mediaElement, {
          title: event.userid,
          buttons: ['full-screen'],
          width: width,
          showOnMouseEnter: false
        });

        connection.audiosContainer.appendChild(mediaElement);

        setTimeout(function() {
          mediaElement.media.play();
        }, 5000);

        mediaElement.id = event.streamid;
      };
      connection.onstreamended = function(event) {
        var mediaElement = document.getElementById(event.streamid);
        if (mediaElement) {
          mediaElement.parentNode.removeChild(mediaElement);
        }
      };
      function showRoomURL(roomid) {
        var roomHashURL = '#' + roomid;
        var roomQueryStringURL = '?roomid=' + roomid;

        var html = '<h2>Unique URL for your room:</h2><br>';

        html += 'Hash URL: <a href="' + roomHashURL + '" target="_blank">' + roomHashURL + '</a>';
        html += '<br>';
        html += 'QueryString URL: <a href="' + roomQueryStringURL + '" target="_blank">' + roomQueryStringURL + '</a>';

        var roomURLsDiv = document.getElementById('room-urls');
        roomURLsDiv.innerHTML = html;

        roomURLsDiv.style.display = 'block';
      }
      (function() {
        var params = {},
          r = /([^&=]+)=?([^&]*)/g;

        function d(s) {
          return decodeURIComponent(s.replace(/\+/g, ' '));
        }
        var match, search = window.location.search;
        while (match = r.exec(search.substring(1)))
          params[d(match[1])] = d(match[2]);
        window.params = params;
      })();
      var roomid = '';
    if (localStorage.getItem(connection.socketMessageEvent)) {
        roomid = localStorage.getItem(connection.socketMessageEvent);
    } else {
        roomid = connection.token();
    }
    document.getElementById('room-id').value = roomid;
    document.getElementById('room-id').onkeyup = function() {
        localStorage.setItem(connection.socketMessageEvent, this.value);
    };

    var hashString = location.hash.replace('#', '');
    if (hashString.length && hashString.indexOf('comment-') == 0) {
      hashString = '';
    }

    var roomid = params.roomid;
    if (!roomid && hashString.length) {
      roomid = hashString;
    }

    if (roomid && roomid.length) {
      document.getElementById('room-id').value = roomid;
      localStorage.setItem(connection.socketMessageEvent, roomid);

      // auto-join-room
      (function reCheckRoomPresence() {
        connection.checkPresence(roomid, function(isRoomExist) {
          if (isRoomExist) {
            connection.join(roomid);
            return;
          }

          setTimeout(reCheckRoomPresence, 5000);
        });
      })();
    }
    },
    
    // 소켓 연결 해제
    socketDisconnect() {
      this.stompClient.socketDisconnect();
    },

    // 메세지 수신
    onMessageReceived(payload) {
      let receiveMessage = JSON.parse(payload.body);
      this.receivedMessages.push(receiveMessage);
      console.log(receiveMessage);
    },
    // 에러 수신
    onError() {},
  },
};
</script>
<style></style>
