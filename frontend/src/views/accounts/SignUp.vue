<template>
  <div class="signup">
    <img src="@/assets/images/user-background.jpg" alt="background-img" class="background-img">
    <div>
      <img src="@/assets/images/ssazip-signup.png" alt="ssazip-signup-img" class="ssazip-signup-img">
      <div class="signup-inputs">
        <input type="text" name="signupName" id="signupName" placeholder="이름을 입력해주세요">
        <div class="signupId">
          <input type="text" name="signupId" id="signupId" placeholder="아이디를 입력해주세요">
          <button>중복체크</button>
        </div>
        <input type="text" name="signupPwd" id="signupPwd" placeholder="비밀번호를 입력해주세요">
        <input type="text" name="signupRePwd" id="signupRePwd" placeholder="비밀번호를 재입력해주세요">
      </div>
      <div class="signup-buttons">
        <button>가입</button>
        <router-link to="/login">
          <button>취소</button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import "@/css/accounts/signup.css"

export default {
  name: "SignUp",
}
</script>

<style>

</style>