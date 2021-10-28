<template>
  <div class="login">
    <img src="@/assets/images/user-background.jpg" alt="background-img" class="background-img">
    <div>
      <img src="@/assets/images/ssazip-login.png" alt="ssazip-login-img" class="ssazip-login-img">
      <p></p>
      <input type="text" name="userId" id="userId" placeholder="ID">
      <input type="text" name="userPassword" id="userPassword" placeholder="PW">
      <div>
        <button class="button-login">로그인</button>
        <button @click="loginCancel" class="button-cancel">취소</button>
      </div>
      <router-link to="/signup">
        <button class="button-signup">회원가입하기</button>
      </router-link>
      <p>소설 로그인</p>
      <div>
        <div id="naverIdLogin" />
        <button @click="kakaoLogin">카카오 로그인</button>
        <button>구글 로그인</button>
      </div>
    </div>
  </div>
</template>
<script src="https://developers.kakao.com/sdk/js/kakao.js"></script>
<script type="text/javascript" src="https://static.nid.naver.com/js/naverLogin_implicit-1.0.3.js" charset="utf-8"></script>
<script>
import "@/css/accounts/login.css";

export default {
  name: "Login",
  data: () => {
    return {
      key: 'AAAAO_FZJrDT8XmalCPwIuCAjbIHiDgqCdNL1UevV20cuFH5T8aOzM5LUwxsZn2pYh8dlNfCtSivT5jdnELDbtwnDek'
    }
  },
  created: function () {
  },
  mounted: function () {
    this.initializeNaverLogin();
  },
  methods: {
    kakaoLogin: function() {
      Kakao.Auth.login({
        success: function(authObj) {
          Kakao.API.request({
            url: "/v2/user/me",
            success: function(res) {
              console.log(res)
              window.location.replace('http://localhost:8080/room')
            },
            fail: function(err) {
              console.log(err)
            }
          })
        },
        fail: function(err) {
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
    initializeNaverLogin: () => {
      const { naver } = window;
      const naverLogin = new naver.LoginWithNaverId({
            clientId: "J8ASU80_cvgYfiW0WCqD",
            callbackUrl: "http://localhost:8080/login",
            isPopup: false,
            loginButton: {color: "white", type: 1, height: 47},
            callbackHandle: true
        });
      naverLogin.init();

      window.addEventListener('load', function () {
        naverLogin.getLoginStatus(function (status) {
          if (status) {
            const email = naverLogin.user.getEmail();
            if (email == undefined || email == undefined) {
              alert("이메일과 이름을 체크해주세요.")
              naverLogin.reprompt();
              return;
            }
            // window.location.replace('http://localhost:8080/login');
          } else {
            alert('로그인 실패');
          }
        })
      });
    },
  }
}
</script>