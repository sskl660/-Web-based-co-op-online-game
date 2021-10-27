<template>
  <div class="login">
    <img src="@/assets/images/user-background.jpg" alt="background-img" class="background-img">
    <div>
      <img src="@/assets/images/ssazip-login.png" alt="ssazip-login-img" class="ssazip-login-img">
      <p></p>
      <input type="text" name="userId" id="userId" placeholder="ID">
      <input type="text" name="userPassword" id="userPassword" placeholder="PW">
      <div>
        <button @click="kakaoLogin" class="button-login">로그인</button>
        <button @click="loginCancel" class="button-cancel">취소</button>
      </div>
      <router-link to="/signup">
        <button class="button-signup">회원가입하기</button>
      </router-link>
    </div>
  </div>
</template>
<script src="https://developers.kakao.com/sdk/js/kakao.js"></script>
<script>
import "@/css/accounts/login.css";
import axios from 'axios';

export default {
  name: "Login",
  data: () => {
    return {
      hi: 'hi'
    }
  },
  created: function () {
  },
  mounted: function () {
  },
  methods: {
    kakaoLogin: function() {
      Kakao.Auth.login({
        success: function(authObj) {
          Kakao.API.request({
            url: "/v2/user/me",
            success: function(res) {
              console.log(res)
            },
            fail: function(err) {
              console.log(err)
            }
          })
        },
        fail: function(err) {
          console.log('hi')
          alert(JSON.stringify(err))
        }
      })
    },
    kakaoLogout: function () {
      console.log(Kakao.Auth.getAccessToken())
      if (Kakao.Auth.getAccessToken()) {
        Kakao.Auth.logout( function() {
          // success: function (response) {
          //   console.log(response)
          // },
          // fail: function (error) {
          //   console.log(error)
          // }
          alert(Kakao.Auth.getAccessToken());
          window.location.replace('http://localhost:8080/')
        })
      }
    },
    loginCancel: function () {
      this.$router.push('/')
    },

  }
}
</script>