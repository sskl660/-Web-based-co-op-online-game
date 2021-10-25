<template>
  <div>
    <input type="text" name="" id="">
    <input type="text" name="" id="">
    <button @click="kakaoLogin">로그인</button>
    <button @click="kakaoLogout">로그아웃</button>
    <router-link to="/signup">
      <button>가입하기</button>
    </router-link>
  </div>
</template>
<script src="https://developers.kakao.com/sdk/js/kakao.min.js"></script>
<script>
export default {
  name: "Login",
  data: () => {
    return {
      hi: 'hi'
    }
  },
  created: function () {
    Kakao.init('3ca1d242ac699c076cee4d7e91ebbaab');
  },
  mounted: function () {
  },
  methods: {
    kakaoLogin: function() {
      Kakao.Auth.login({
        success: function(authObj) {
          console.log(authObj)
          Kakao.API.reqeust({
            url: '/',
            success: function(res) {
              console.log(res)
            },
            fail: function(err) {
              alert(JSON.stringify(err))
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
    }
  }
}
</script>

<style>

</style>