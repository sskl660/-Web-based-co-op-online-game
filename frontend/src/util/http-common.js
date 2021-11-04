import axios from 'axios';

export default axios.create({
  // baseURL: 'http://localhost:8080',
  baseURL: 'https://3.38.99.194:8080',
  headers: {
    'Content-type': 'application/json',
  },
});
