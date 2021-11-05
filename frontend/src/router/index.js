import Vue from 'vue';
import VueRouter from 'vue-router';
import Home from '@/views/Home.vue';
import Login from '@/views/accounts/Login.vue';
import SignUp from '@/views/accounts/SignUp.vue';
import Rank from '@/views/Rank.vue';
import Room from '@/views/Room.vue';
import CreateRoom from '@/views/CreateRoom.vue';
import SpeakGame from '@/views/SpeakGame.vue'
import Temp from '@/views/temp/Chat.vue';
import Chat from '@/views/temp/ChatRoom.vue';
import SSazipJump from '@/views/SSazipJump.vue';
import SSazipJumpTemp from '@/views/ssazipjump/SSazipJumpTemp.vue';
import SsafyMind from '@/views/ssafymind/SsafyMind.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/signup',
    name: 'SignUp',
    component: SignUp,
  },
  {
    path: '/room/:roomId',
    name: 'Room',
    component: Room,
  },
  {
    path: '/rank',
    name: 'Rank',
    component: Rank,
  },
  {
    path: '/room',
    name: 'CreateRoom',
    component: CreateRoom
  },
  {
    path: '/game-speak',
    name: 'SpeakGame',
    component: SpeakGame
  },
  {
    path: '/ssafymind',
    name: 'SsafyMind',
    component: SsafyMind
  },
  // {
  //   path: '/about',
  //   name: 'About',
  //   // route level code-splitting
  //   // this generates a separate chunk (about.[hash].js) for this route
  //   // which is lazy-loaded when the route is visited.
  //   component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  // }
  // 임시 채팅 예제
  {
    path: '/temp',
    name: 'Temp',
    component: Temp,
  },
  {
    path: '/chat',
    name: 'Chat',
    component: Chat,
    props: true,
  },
  {
    path: '/ssazipjump',
    name: 'SSazipJump',
    component: SSazipJump,
  },
  {
    path: '/ssazipjumptemp',
    name: 'SSazipJumpTemp',
    component: SSazipJumpTemp,
  }
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes,
});

export default router;
