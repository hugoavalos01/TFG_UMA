import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api', // URL de la API
});

// Interceptor para todas las solicitudes
instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

export default instance;
