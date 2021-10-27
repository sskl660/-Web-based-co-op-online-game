<template>
  <div class="home">
    <img src="@/assets/images/user-background.jpg" alt="background-img" class="background-img">
    <div>
      <img src="@/assets/images/ssazip-home.png" alt="ssazip-home-img" class="ssazip-home-img">
      <div class="enter">
        <div class="enter-inputs">
          <input type="text" name="nickname" id="nickname" placeholder="이름을 입력해주세요">
          <input type="text" name="part" id="part" placeholder="참여코드를 입력해주세요">
        </div>
        <button class="button-enter">참여하기</button>
      </div>
      <router-link to="login">
        <button class="button-create">게임을 만드시겠어요?</button>
      </router-link>
    </div>
  </div>
</template>

<script>
import "@/css/home.css";

export default {
  name: "Home",
}
</script>

<style>

</style>