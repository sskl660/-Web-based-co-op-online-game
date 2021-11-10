import http from 'http';
import SocketIO from 'socket.io';
import express from "express";

const app = express();
app.all('/*', function(req, res, next) { 
  res.header("Access-Control-Allow-Origin", "*"); 
  res.header("Access-Control-Allow-Headers", "X-Requested-With"); 
  next(); 
});

const httpServer = http.createServer(app);
const wsServer = SocketIO(httpServer);

wsServer.on("connection", (socket) => {
  socket.on("join_room", (roomName) => {
    socket.join(roomName);
    socket.to(roomName).emit("welcome");
  });
  socket.on("offer", (offer, roomName) => {
    socket.to(roomName).emit("offer", offer);
  });
  socket.on("answer", (answer, roomName) => {
    socket.to(roomName).emit("answer", answer);
  });
  socket.on("ice", (ice, roomName) => {
    socket.to(roomName).emit("ice", ice);
  });
  socket.on("room", (msg) => console.log(msg));
});

const handleListen = () => console.log(`Listening on http://localhost:3000`);
httpServer.listen(3001, handleListen);