import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/shared/Button.css';

axios.defaults.baseURL = 'http://localhost:8080';

const CreateAccountPage = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [gender, setGender] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const validateForm = () => {
    if (!username || !email || !password || !gender) {
      setError('All fields are required.');
      return false;
    }
    if (!/\S+@\S+\.\S+/.test(email)) {
      setError('Please enter a valid email.');
      return false;
    }
    // if (password.length < 6) {
    //   setError('Password must be at least 6 characters long.');
    //   return false;
    // }
    return true;
  };

  const handleCreateAccount = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    setLoading(true); // Set loading state when form is submitted

    try {
      await axios.post('/api/v1/auth/register', 
        { username, email, password, role: 'USER', gender }, 
        { withCredentials: true }
      );
      navigate('/login'); // Redirect to login page after account creation
    } catch (error) {
      setError('Error creating account.');
    } finally {
      setLoading(false); // Reset loading state after the request completes
    }
  };

  return (
    <div className="login-page">
      <div className="modal">
        <h1>Create Account</h1>
        <form onSubmit={handleCreateAccount} className="modal-content">
          <div className="form-group">
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="text-input"
              disabled={loading}
            />
          </div>
          <div className="form-group">
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="text-input"
              disabled={loading}
            />
          </div>
          <div className="form-group">
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="text-input"
              disabled={loading}
            />
          </div>
          <div className="form-group">
            <select
              value={gender}
              onChange={(e) => setGender(e.target.value)}
              className="text-input"
              disabled={loading}
            >
              <option value="">Select Gender</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
          <button type="submit" className="button" disabled={loading}>
            {loading ? 'Creating Account...' : 'Create Account'}
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
          <p>Already have an account? <Link to="/login">Login</Link></p>
        </div>
      </div>
    </div>
  );
};


export default CreateAccountPage;