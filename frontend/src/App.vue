<template>
  <div id="app">
    <router-view/>
  </div>
</template>
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
}
body{
  background-color: #66C5CF;
}

body {
  margin: 0;
}
</style>
<script>
import { mapState } from 'vuex';

export default {
  data: function() {
    return {
      path: '',
    }
  },
  created() {
    this.path = this.$route.path
  },
  mounted() {
  },
  computed: {
    ...mapState(['isLogin', 'user', 'visitedRoomId']),
  },
  watch: {
    path: function() {
      // 방장
      if(this.isLogin) {
        // 참가했던 방이 있는 경우
        if (this.visitedRoomId) {
          this.$router.push(`/room/${this.visitedRoomId}`);
        // 참가했던 방이 없는 경우
        } else {
          // home, login, signup 페이지에 있으면 '방 생성' 페이지로 이동
          if(this.path === '/' || this.path === '/login' || this.path === '/signup') {
            this.$router.push('/room');
            return
          }
        }
      // 참가자
      } else if(!this.isLogin) {
        // 방이 있고, userName이 남아있는 경우
        // 나갔을 때 방이 사라진 경우 Room.vue의 'checkRoom'에서 지워줌
        if (this.user.userName && this.visitedRoomId) {
          this.$router.push(`/room/${this.visitedRoomId}`);
          return
        // 방과 이름이 둘 중 하나라도 없으면 다시 참가 페이지로 보낸다.
        } else {
          if (this.path !== '/' && this.path !== '/login' && this.path !== '/signup') {
            this.$router.push('/');
            return
          }
        }
      }
    }
  },
  beforeUpdate() {
    this.path = this.$route.path
    console.log(this.path)
  },
}
</script>
