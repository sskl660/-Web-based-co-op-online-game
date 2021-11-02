import Vue from 'vue';
import VueRouter from 'vue-router';
import Home from '@/views/Home.vue';
import Login from '@/views/accounts/Login.vue';
import SignUp from '@/views/accounts/SignUp.vue';
import Rank from '@/views/Rank.vue';
import JS from '@/views/test/jangseop.vue';
import MC from '@/views/test/multichat.vue';
import MT from '@/views/test/multi.vue';
import Room from '@/views/Room.vue';
import CreateRoom from '@/views/CreateRoom.vue';
import Temp from '@/views/temp/Chat.vue';
import Chat from '@/views/temp/ChatRoom.vue';
import Roomjs from '@/views/test/Roomjs.vue';
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
        path: '/rank',
        name: 'Rank',
        component: Rank,
    },
    {
        path: '/js',
        name: 'jangseoptest',
        component: JS,
    },
    {
        path: '/multichat',
        name: 'multichat',
        component: MC,
    },
    {
        path: '/multi',
        name: 'multi',
        component: MT,
    },

    // {
    //   path: '/about',
    //   name: 'About',
    //   // route level code-splitting
    //   // this generates a separate chunk (about.[hash].js) for this route
    //   // which is lazy-loaded when the route is visited.
    //   component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')

    {
        path: '/room/1',
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
        component: CreateRoom,
    },
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
        path: '/roomjs',
        name: 'Roomjs',
        component: Roomjs,
        props: true,
    },
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes,
});

export default router;
