<template>
  <div class="home">
    <img src="@/assets/images/user-background.jpg" alt="background-img" class="background-img" />
    <div>
      <img src="@/assets/images/ssazip-home.png" alt="ssazip-home-img" class="ssazip-home-img" />
      <div class="enter">
        <div class="enter-inputs">
          <input
            type="text"
            name="nickname"
            id="nickname"
            placeholder="이름을 입력해주세요"
            v-model="user.name"
          />
          <input
            type="text"
            name="part"
            id="part"
            placeholder="참여코드를 입력해주세요"
            v-model="roomId"
          />
        </div>
        <button class="button-enter" @click="joinGameRoom">참여하기</button>
      </div>
      <router-link to="login">
        <button class="button-create">게임을 만드시겠어요?</button>
      </router-link>
    </div>
  </div>
</template>

<script>
import '@/css/home.css';
import { mapActions } from 'vuex';
import axios from '@/util/http-common.js';
import { uuidv4 } from '@/util/util.js';
import swal from 'sweetalert';


export default {
  name: 'Home',
  data() {
    return {
      user: {
        id: '',
        name: '',
        teamNo: 0,
      },
      roomId: '',
    };
  },
  methods: {
    joinGameRoom() {
      // 닉네임 길이는 3글자 이상, 15글자 이하로 한다.
      if (1 > this.user.name.length || this.user.name.length > 4) {
        swal({
          // className:'alert',
          title: '닉네임 길이는 1글자 이상,',
          text: ' 4글자 이하로 설정해주세요.',
          icon: "/img/ssazip-logo.png",
          buttons: {
          text: '확인',
        },
        })
        return;
      }
      // roomId를 입력하지 않는 경우 참여가 불가능하다
      if (this.roomId.length == 0) {
        swal({
            // className:'alert',
            title: '참가 코드를 입력하세요!',
            icon: "/img/ssazip-logo.png",
            buttons: {
            text: '확인',
          },
        })
        return;
      }
      // 유저 정보 저장
      this.user.id = uuidv4();
      // 서버 레벨 입장 가능 여부 확인 로직
      axios({
        method: 'post',
        url: `/game/user`,
        data: { roomId: this.roomId, participantName: this.user.name, participantId: this.user.id },
      })
        .then((res) => {
          let exist = res.data;
          // 방이 존재하지 않는 경우
          if (exist == 0) {
            swal({
                // className:'alert',
                title: '해당 방은 존재하지 않습니다!',
                text: '입장코드를 확인해주세요',
                icon: "/img/ssazip-logo.png",
                buttons: {
                text: '확인',
              },
            })
            return;
          }
          // 입장 제한인원을 초과한 경우
          else if (exist == 1) {
            swal({
                // className:'alert',
                title: '해당 방의 정원을 초과했습니다!',
                icon: "/img/ssazip-logo.png",
                buttons: {
                text: '확인',
              },
            })
            return;
          }
          // 동일한 이름의 참가자가 존재하는 경우
          else if (exist == 2) {
            swal({
                // className:'alert',
                title: "동일한 이름의 참가자가 존재합니다.",
                text: '이름을 수정해주세요!',
                icon: "/img/ssazip-logo.png",
                buttons: {
                text: '확인',
              },
            })
            return;
          }
          // 유저 정보 저장
          // this.user.id = uuidv4();
          this.login(this.user);
          // 유저가 참여중인 방 정보 갱신
          this.joinRoom(this.roomId);
          // 방으로 이동
          this.$router.push('/room/' + this.roomId);
        })
        .catch(() => {});
    },
    ...mapActions(['login', 'joinRoom']),
  },
};
</script>

<style></style>
