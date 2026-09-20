import axios from 'axios';
import { getStoredUser } from '../config/auth';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

api.interceptors.request.use((config) => {
  const user = getStoredUser();
  if (user && user.email) {
    config.headers['X-User-Id'] = user.email.toLowerCase().trim();
    config.headers['X-User-Email'] = user.email;
    config.headers['X-User-Name'] = user.name || '';
  }
  return config;
});

export default api;
