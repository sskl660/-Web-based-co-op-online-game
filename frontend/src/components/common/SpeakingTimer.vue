<template>
  <div class="speaking-timer">
    <p id="countdown">{{ this.getMinutes }}:{{ this.getSeconds }}</p>
  </div>
</template>

<script>
export default {
  name: 'Timer',
  props: ['timer'],
  data: function() {
    return {
      minutes: 2,
      seconds: 0,
      interval: 0,
    };
  },
  methods: {
    setTimer: function() {
      if (!this.timer) {
        this.minutes = 2;
        this.seconds = 0;
        this.interval = setInterval(this.timeMinus, 1000);
      } else {
        clearInterval(this.interval);
      }
    },
    timeMinus: function() {
      if (this.seconds !== 0) {
        this.seconds--;
      } else if ((this.seconds === 0) & (this.minutes !== 0)) {
        this.minutes--;
        this.seconds = 59;
      } else if ((this.seconds === 0) & (this.minutes === 0)) {
        clearInterval(this.interval);
      }
    },
  },
  computed: {
    getSeconds() {
      return this.seconds < 10 ? '0' + this.seconds : this.seconds;
    },
    getMinutes() {
      return this.minutes < 10 ? '0' + this.minutes : this.minutes;
    },
  },
  watch: {
    'timer'() {
      this.setTimer();
    }
  }
};
</script>

<style>
.speaking-timer {
  margin: 30px auto;
}
.speaking-timer > p {
  font-size: 3rem;
  margin: -3%;
  font-weight: 700;
  color: #cd5069;
}
</style>
