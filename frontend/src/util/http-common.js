import axios from 'axios';

export default axios.create({
  // baseURL: 'http://localhost:8080',
  baseURL: 'http://k5a302.p.ssafy.io:8080',
  headers: {
    'Content-type': 'application/json',
  },
});
