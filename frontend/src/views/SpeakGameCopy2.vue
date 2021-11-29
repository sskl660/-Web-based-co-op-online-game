<template>
  <div>
    <input type="checkbox" id="chk-hear-mic"><label for="chk-hear-mic">마이크 소리 듣기</label>
    <button id="record">녹음</button>
    <button id="stop">녹음 정지</button>
    <div id="sound-clips"></div>
  </div>
</template>

<script>
import "@/css/speak-game.css"

export default {
  name: "SpeakGame",
  data: () => {
    return {
      record: {},
      stop: {},
      soundClips: {},
      chkHearMic: {},
      audioCtx: {},
      analyser: {},
      chunks: [],
    }
  },
  mounted: function() {
    this.record = document.getElementById("record");
    this.stop = document.getElementById("stop");
    this.soundClips = document.getElementById("sound-clips");
    this.chkHearMic = document.getElementById("chk-hear-mic");

    const audioCtx = new(window.AudioContext || window.webkitAudioContext)();
    this.audioCtx = audioCtx;
    
    const analyser = audioCtx.createAnalyser();
    this.analyser = analyser;

    function makeSound(stream) {
      const source = this.audioCtx.createMediaStreamSource(stream);
      source.connect(this.analyser)
      this.analyser.connect(this.audioCtx.destination)
    }

    if (navigator.mediaDevices) {
      console.log('getUserMedia supported.')

      navigator.mediaDevices.getUserMedia({audio: true})
      .then(stream => {
        const mediaRecorder = new MediaRecorder(stream)

        this.chkHearMic.onchange = e => {
          if(e.target.checked == true) {
            audioCtx.resume();
            makeSound(stream)
          } else {
            audioCtx.suspend();
          }
        }
        this.record.onclick = () => {
          mediaRecorder.start();
          console.log(mediaRecorder.state)
        }
        this.stop.onclick = () => {
          mediaRecorder.stop();
          console.log(mediaRecorder.state)
        }

        mediaRecorder.onstop = () => {
          console.log("data available after MediaRecorder.stop() called.");

          const clipName = prompt("오디오 파일 제목을 입력하세요.", new Date());
          
          const clipContainer = document.createElement('article');
          const clipLabel = document.createElement('p');
          const audio = document.createElement('audio');
          const deleteButton = document.createElement('button');

          clipContainer.classList.add('clip');
          audio.setAttribute('controls', '');
          deleteButton.innerHTML = "삭제";
          clipLabel.innerHTML = clipName;

          clipContainer.appendChild(audio)
          clipContainer.appendChild(clipLabel)
          clipContainer.appendChild(deleteButton)
          this.soundClips.appendChild(clipContainer)
          window.
          audio.controls = true;
          const blob = new Blob(this.chunks, {
            // type: 'audio/ogg codecs=opus'
            type: 'audio/wav; codecs=0'
          })
          console.log(blob)
          this.chunks = [];
          const audioURL = URL.createObjectURL(blob);
          audio.src = audioURL;
          console.log(audio)
          console.log("recorder stopped");
          
          deleteButton.onclick = e => {
            const evtTgt = e.target;
            evtTgt.parentNode.parentNode.removeChild(evtTgt.parentNode)
          }
        }

        mediaRecorder.ondataavailable = e => {
          this.chunks.push(e.data)
        }
      })
      .catch(err => {
        console.log('The following error occurred: ' + err)
      })
    }
    this.onRecAudio();
  },
  methods: {
    // 음원 정보를 담은 노드를 생성하거나 음원을 실행 또는 디코딩시키는 일을 한다.

    
    onRecAudio: function() {
      const sr = window.webkitSpeechRecognition;
      const recognition = new sr()
      console.log(recognition)
      // const audioCtx = new (window.AudioContext || window.webkitAudioContext)();

      // 마이크 사용 권한 획득
      // navigator.mediaDevices.getUserMedia({audio : true})
      // .then(stream => {
      //   const mediaRecorder = new MediaRecorder(stream);
      //   console.log(mediaRecorder)
      //   mediaRecorder.start();
    },
  },
}
</script>

<style>

</style>