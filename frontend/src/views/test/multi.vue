<template>
    <div class="container">
        <table class="table table-striped">
            <button @click="refresh()">방새로고침</button>
            <thead>
                <tr>
                    <th>번호</th>
                    <th>방 이름</th>
                    <th>입장버튼</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="room : ${rooms}">
                    <td th:text="${room.contents}"></td>
                    <td th:text="${room.name}"></td>
                    <td>
                        <a class="btn btn-primary" th:href="@{/rooms/{id} (id = ${room.roomId})}"></a>
                    </td>
                </tr>
            </tbody>
        </table>
        <button @click="creroom()">방만들기</button>
        <input v-model="userName">
        <!-- <a class="btn btn-primary pull-right" href="/multicreate">새로 만들기</a> -->
    </div>
</template>

<script>
import Stomp from 'webstomp-client';
import SockJS from 'sockjs-client';

export default {
    name: 'multi',
    data() {
        return {
            rooms:[],
            // roomName: '',
            // roomCode: '',
            makemessage:'',
            userName:'',
        };
    },
    created() {
        this.connect(); //페이지 생성시 연결
    },
    methods: {
        creroom(){
            this.makemessage=this.rooms.length+1;
            console.log('Send message:' + this.makemessage);
            if (this.stompClient && this.stompClient.connected) {
                const msg = {
                    userName: this.userName,
                    content: this.makemessage,
                };
                this.stompClient.send('/receive', JSON.stringify(msg), {});
            }
            this.$router.push({ path: '/multichat', query: { cn: this.userName } });


        },
        refresh(){
            this.connect();
        },
        connect() {
            const serverURL = 'http://localhost:8080/multi';
            // let socket = new SockJS(serverURL);
            let socket = new SockJS(serverURL);

            this.stompClient = Stomp.over(socket);
            console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);
            this.stompClient.connect(
                {},
                (frame) => {
                    // 소켓 연결 성공
                    this.connected = true;
                    console.log('소켓 연결 성공', frame);
                    // 서버의 메시지 전송 endpoint를 구독합니다.
                    // 이런형태를 pub sub 구조라고 합니다.
                    this.stompClient.subscribe('/send', (res) => {
                        console.log('구독으로 받은 메시지 입니다.', res.body);

                        // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
                        this.rooms.push(JSON.parse(res.body));
                    });
                },
                (error) => {
                    // 소켓 연결 실패
                    console.log('소켓 연결 실패', error);
                    this.connected = false;
                }
            );
        },
    },
};
</script>

<style></style>
