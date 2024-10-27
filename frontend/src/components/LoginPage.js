import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/LoginPage.css';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async () => {
    // Validate input
    if (!username || !password) {
      setError('Please enter both username and password.');
      return;
    }

    setLoading(true);  // Start loading state

    try {

      const response = await axios.post('/api/v1/auth/authenticate', { username, password });
      console.log(response);
      // Store the JWT token and user information in localStorage
      localStorage.setItem('token', response.data.token); // JWT token
      localStorage.setItem('user', JSON.stringify(response.data.user)); // User data

      // Redirect based on user role
      const userRole = response.data.user.role.toLowerCase(); // Ensure case-insensitive
      if (userRole === 'admin') {

        navigate('/admin'); // Admin dashboard
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

  return (
    <div className="login-container">
      <h1>FENCING</h1>
      <div className="input-container">
        <input
          type="text"
          placeholder="USERNAME"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
      </div>
      <div className="input-container">
        <input
          type="password"
          placeholder="PASSWORD"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <button onClick={handleLogin} disabled={loading}>
        {loading ? 'Logging in...' : 'LOGIN'}
      </button>
      {error && <p className="error">{error}</p>}
      <p>
      <Link to="/create-account" className="create-account-link"> {/* Use Link here */}
          Create account here
        </Link>
      </p>
    </div>
  );
};

export default LoginPage;


// import React, { useState } from 'react';
// import axios from 'axios';
// import { useNavigate } from 'react-router-dom';
// import '../styles/Login.css';

// const LoginPage = () => {
//   const [username, setUsername] = useState('');
//   const [password, setPassword] = useState('');
//   const [error, setError] = useState(null);
//   const navigate = useNavigate();

//   const handleLogin = async () => {
//     try {
//       const response = await axios.post('/auth/login', { username, password });
//       localStorage.setItem('token', response.data.token); // Store JWT token
//       localStorage.setItem('user', JSON.stringify(response.data.user)); // Store user data

//       // Redirect based on the user's role (admin or regular user)
//       if (response.data.user.role === 'admin') {
//         navigate('/admin-dashboard');
//       } else {
//         navigate('/user-dashboard');
//       }
//     } catch (error) {
//       setError('Invalid credentials. Please try again.');
//     }
//   };

//   return (
//     <div className="login-container">
//       <h1>FENCING</h1>
//       <div className="input-container">
//         <input
//           type="text"
//           placeholder="USERNAME"
//           value={username}
//           onChange={(e) => setUsername(e.target.value)}
//         />
//       </div>
//       <div className="input-container">
//         <input
//           type="password"
//           placeholder="PASSWORD"
//           value={password}
//           onChange={(e) => setPassword(e.target.value)}
//         />
//       </div>
//       <button onClick={handleLogin}>LOGIN</button>
//       {error && <p className="error">{error}</p>}
//       <p>
//         <a href="/create-account" className="create-account-link">
//           Create account here
//         </a>
//       </p>
//     </div>
//   );
// };

// export default LoginPage;


// import React, { useState } from 'react';
// import { useNavigate } from 'react-router-dom';
// import authService from '../services/authService';
// import '../styles/LoginPage.css';  // Custom styling from Figma

// const LoginPage = () => {
//   const [username, setUsername] = useState('');
//   const [password, setPassword] = useState('');
//   const [error, setError] = useState('');
//   const navigate = useNavigate();

//   const handleLogin = async (e) => {
//     e.preventDefault();

//     try {
//       const { token, role } = await authService.login(username, password);
//       localStorage.setItem('token', token);
//       localStorage.setItem('role', role);
      
//       role === 'ADMIN' ? navigate('/admin/dashboard') : navigate('/dashboard');
//     } catch (err) {
//       setError('Invalid username or password');
//     }
//   };

//   return (
//     <div className="login-container">
//       <h2>Login</h2>
//       {error && <p style={{ color: 'red' }}>{error}</p>}
//       <form onSubmit={handleLogin}>
//         <div>
//           <label>Username</label>
//           <input
//             type="text"
//             value={username}
//             onChange={(e) => setUsername(e.target.value)}
//             required
//           />
//         </div>
//         <div>
//           <label>Password</label>
//           <input
//             type="password"
//             value={password}
//             onChange={(e) => setPassword(e.target.value)}
//             required
//           />
//         </div>
//         <button type="submit">Login</button>
//       </form>
//     </div>
//   );
// };

// export default LoginPage;
