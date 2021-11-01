<template>
  <div class="room-create">
    <img src="@/assets/images/user-background.jpg" alt="background-img" class="background-img" />
    <div class="logout">
      <span class="logout-text">이장섭님 안녕하세요</span>
      <button class="logout-button">로그아웃</button>
    </div>
    <div class="create-form">
      <img
        src="@/assets/images/ssazip-create.png"
        alt="ssazip-create-img"
        class="ssazip-create-img"
      />
      <input type="text" placeholder="방 제목을 설정해주세요" v-model="roomName" />
      <button @click="createGameRoom">방 만들기</button>
    </div>
  </div>
</template>

<script>
import '@/css/create-room.css';
import axios from '@/util/http-common.js';

export default {
  name: 'CreateRoom',
  component: {},
  data() {
    return {
      roomName: '',
      userName: '김싸피',
    };
  },
  methods: {
    // 게임 방 만들기
    createGameRoom() {
      axios({
        method: 'post',
        url: `/game/room/${this.roomName}/${this.userName}`,
      })
        .then((res) => {
          let room = res.data;
          this.$router.push('/room/' + room.roomId);
        })
        .catch(() => {});
    },
  },
};
</script>

<style></style>
