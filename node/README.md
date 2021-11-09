# WebRTC를 위한 Node 서버 구축

> Socket.io 사용 목적



## 초기 설정

* init

```bash
$ npm init -y
```



* package.json에서 'main'과 'scripts', 'keyword', 'author' 제거 후 내용 작성

```json
{
  "name": "node",
  "version": "1.0.0",
  "description": "또박또박 말해요 오디오 사용을 위한 Node Server",
  "license": "MIT"
}
```



* nodemon 설치

```bash
$ npm i nodemon -D
```

> 이후 `nodemon.json`과  `babel.config.json` 파일, `src` 폴더와 그 밑에 `server.js` 생성



* babel 설치

```bash
$ npm i @babel/core @babel/cli @babel/node -D
```



* .gitignore

```
$ touch .gitignore

=> 내용에 `/node_modules` 입력
```



* nodemon.json 내용 입력

```js
{
  "exec": "babel-node src/server.js"
}
```

> exec 명령어를 이용해 



* babel.config.json 설정 및 설치

```bash
$ npm i @babel/preset-env -D
```



```json
{
  "presets": ["@babel/preset-env"]
}
```



* express, pug 설치

```bash
$ npm i express
$ npm i pug
```



## Socket.io

* socket.io 설치

```bash
$ npm i socket.io
```

