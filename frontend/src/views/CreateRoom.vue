<template>
  <div class="room-create">
    <img src="@/assets/images/user-background.jpg" alt="background-img" class="background-img">
    <div class="logout">
      <span class="logout-text">이장섭님 안녕하세요</span>
      <button class="logout-button">로그아웃</button>
    </div>
    <div class="create-form">
      <img src="@/assets/images/ssazip-create.png" alt="ssazip-create-img" class="ssazip-create-img">
      <input type="text" placeholder="방 제목을 설정해주세요">
      <button>방 만들기</button>
    </div>
  </div>
</template>

<script>
import "@/css/create-room.css"

export default {
  name: 'CreateRoom',
}
</script>

<style>

</style>