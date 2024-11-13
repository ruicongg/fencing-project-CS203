import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

import "../styles/shared/index.css";

axios.defaults.baseURL = 'http://localhost:8080';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault(); // added by ruicong

    // Validate input
    if (!username || !password) {
      setError('Please enter both username and password.');
      return;
    }

    setLoading(true);  // Start loading state

    try {

      const response = await axios.post('/api/v1/auth/authenticate', { username, password });
      console.log(response);
      const token = response.data.token;
      console.log("Received Token:", token);

      // Decode the token to get the role information
      const decodedToken = jwtDecode(token);
      console.log("Decoded Token:", decodedToken);

      // Store the JWT token in localStorage
      localStorage.setItem('token', token);

      const userRoles = decodedToken.roles;  // This should match the structure of your decoded token
      const userRole = userRoles.includes("ROLE_ADMIN") ? "admin" : "user";  // Check if "ROLE_ADMIN" is in the roles array

      if (userRole === 'admin') {
        navigate('/admin/dashboard'); // Admin dashboard
      } else {
        navigate('/dashboard'); // User dashboard
      }
    } catch (error) {
      // Handle error based on status code or generic error
      if (error.response && error.response.status === 401) {
        setError('Invalid credentials. Please try again.');
      } else {
        setError('An unexpected error occurred. Please try again later.');
      }
    } finally {
      setLoading(false);  // End loading state
    }
  };
  
  // added by ruicong
  return (       
    <div className="login-page">
      <div className="modal">
        <h1>Login</h1>
        <form onSubmit={handleLogin} className="modal-content">
          <div className="modal-content input">
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          <div className="modal-content input">
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <button type="submit" className="button" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
          {error && (
            <div className="error-container">
              <svg className="error-icon" viewBox="0 0 24 24">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
              </svg>
              <span className="error-message">{error}</span>
            </div>
          )}
        </form>
        <div className="signup-link">
          <p>Don't have an account? <Link to="/create-account">Sign up</Link></p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;