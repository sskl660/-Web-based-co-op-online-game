<template>
  <div>
    <p>녹음하기</p>
    <input type="checkbox" id="chk-hear-mic"><label for="chk-hear-mic">마이크 소리 듣기</label>
    <button id="record">녹음</button>
    <button id="stop">녹음 정지</button>
    <span id="final_span"></span>
    <span id="interim_span"></span>
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
      mediaRecorder: {},
    }
  },
  mounted: function() {
    this.getAudio();
    this.translate();
  },
  methods: {
    getAudio: function() {
      this.record = document.getElementById("record");
      this.stop = document.getElementById("stop");
      this.soundClips = document.getElementById("sound-clips");
      this.chkHearMic = document.getElementById("chk-hear-mic");

      const audioCtx = new(window.AudioContext || window.webkitAudioContext)();
      this.audioCtx = audioCtx;
      
      const analyser = audioCtx.createAnalyser()
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
          this.mediaRecorder = mediaRecorder
          this.chkHearMic.onchange = e => {
            if(e.target.checked == true) {
              audioCtx.resume();
              makeSound(stream)
            } else {
              audioCtx.suspend();
            }
          }
          this.record.onclick = () => {
            this.mediaRecorder.start();
            console.log(this.mediaRecorder.state)
          }
          this.stop.onclick = () => {
            this.mediaRecorder.stop();
            console.log(this.mediaRecorder.state)
          }

          this.mediaRecorder.onstop = () => {
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
            audio.controls = true;
            const blob = new Blob(this.chunks, {
              type: 'audio/ogg codecs=opus'
              // type: 'audio/wav; codecs=0'
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

          this.mediaRecorder.ondataavailable = e => {
            this.chunks.push(e.data)
            console.log(this.chunks)
          }
        })
        .catch(err => {
          console.log('The following error occurred: ' + err)
        })
      }
    },
    translate: function() {
      if (typeof webkitSpeechRecognition !== 'function') {
        alert('크롬에서만 동작합니다')
        return false
      }

      const speech = new (window.SpeechRecognition || window.webkitSpeechRecognition);
      let isRecognizing = false;
      let ignoreEndProcess = false;
      let finalTranscript = '';

      const final_span = document.querySelector('#final_span')
      const interim_span = document.querySelector('#interim_span')
      // 원하는 언어를 앞으로 뺴라(해당 언어만 지원)
      // 비어있으면 영어, 한국어 둘 다 지원(한국어 우선)
      speech.lang = ["ko-KR", "en-US"];
      speech.continuous = true;
      speech.interimResults = true;

      speech.onstart = function() {
        isRecognizing = true;
        console.log(isRecognizing)
      }

      speech.onend = function () {
        isRecognizing = false;
        console.log(isRecognizing)

        if (ignoreEndProcess) {
          return false;
        }

        if (!finalTranscript) {
          return false;
        }
      };

      speech.onresult = function (event) {
        let interimTranscript = '';
        if (typeof event.results === 'undefined') {
          speech.onend = null;
          speech.stop();
          return;
        }

        for (let i = event.resultIndex; i < event.results.length; ++i) {
          const transcript = event.results[i][0].transcript;

          if (event.results[i].isFinal) {
            finalTranscript += transcript;
            console.log('파이널', finalTranscript)
            console.log('파이널', transcript)
          } else {
            interimTranscript += transcript;
          }
        }
        final_span.innerHTML = finalTranscript;
        interim_span.innerHTML = interimTranscript;
        console.log(interimTranscript)
      }

      speech.onerror = function (event) {
        if(event.error.match(/no-speech|audio-capture|not-allowed/)) {
          ignoreEndProcess = true;
        }
      }
      console.log(speech)

      document.querySelector("#record").addEventListener("click", () => {
        speech.start();
      })

      document.querySelector("#stop").addEventListener("click", () => {
        speech.stop();
      })

      speech.addEventListener("result", (event) => {
        const transcript = event["results"];
        // this.mediaRecorder.stop();
        console.log(transcript);
      })
    },
  },
}
</script>

<style>

</style>