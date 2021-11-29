<template>
  <div class="game-answer-modal">
    <div class="show-team-dialog">
      <div class="show-team-content">
        <div class="modal-body">
          <div class="show-team-explainbox">
            <div class="show-answer-title">축하합니다</div>
            <img id="team-num-img" :src="require(`../../assets/team${this.teamOrder[0]}.png`)" alt="" />
            <span class="team">팀</span>
            <span class="score">+100점</span>
            <button
              class="start-game-btn"
              @click="startModal"
              v-if="teamOrder[0] != undefined && host == this.getUser.id"
            >
              닫기
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import '@/components/css/gameAnswerModal.css';
import { mapGetters } from 'vuex';
export default {
  name: 'GameAnswerModal',
  props: ['teamOrder', 'host'],
  data: function() {
    return {
      answermodal: false,
    };
  },
  computed: {
    ...mapGetters(['getUser']),
  },
  methods: {
    startModal() {
      this.$emit('getCloseAnsModal', this.answermodal);
    },
  },
};
</script>
<style>
body {
  margin: 0;
}
</style>
