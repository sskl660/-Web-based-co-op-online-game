<template>
  <div>
    <div>
      <input type="text" name="nickname" id="nickname" placeholder="이름을 입력해주세요">
    </div>
    <div>
      <input type="text" name="part" id="part" placeholder="참여코드를 입력해주세요">
      <button>참여하기</button>
    </div>
    <router-link to="login">
      <button>방 만들기</button>
    </router-link>
  </div>
</template>

<script>
export default {
  name: "Home",
}
</script>

<style>

</style>