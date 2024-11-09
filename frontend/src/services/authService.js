import axios from 'axios';

// Attach JWT token to all requests
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Login function
const login = async (username, password) => {
  const response = await axios.post('/api/v1/auth/login', { username, password });
  return response.data;  // { token, role }
};

export default { login };
