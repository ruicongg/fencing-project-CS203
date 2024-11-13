import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/CreateAccountPage.css';

axios.defaults.baseURL = 'http://localhost:8080';

const CreateAccountPage = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [gender, setGender] = useState(''); // New state for gender
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const validateForm = () => {
    if (!username || !email || !password || !gender) { // Include gender in validation
      setError('All fields are required.');
      return false;
    }
    if (!/\S+@\S+\.\S+/.test(email)) {
      setError('Please enter a valid email.');
      return false;
    }
    return true;
  };

  const handleCreateAccount = async () => {
    if (!validateForm()) return;
    setLoading(true); // Set loading state when form is submitted

    try {
      await axios.post('/api/v1/auth/register', {
        username,
        email,
        password,
        gender, // Add gender to request payload
        role: 'USER',
      }, { withCredentials: true });
      navigate('/login'); // Redirect to login page after account creation
    } catch (error) {
      if (error.response && error.response.data && error.response.data.error) {
        setError(error.response.data.error);
      } else {
        setError('Error creating account. Please try again.');
      }
    } finally {
      setLoading(false); // Reset loading state after the request completes
    }
  };

  return (
    <div className="create-account-container">
      <h1>FENCING</h1>
      <div className="input-container">
        <input
          type="text"
          placeholder="USERNAME"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          disabled={loading} // Disable input while loading
        />
      </div>
      <div className="input-container">
        <input
          type="email"
          placeholder="EMAIL"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          disabled={loading}
        />
      </div>
      <div className="input-container">
        <input
          type="password"
          placeholder="PASSWORD"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          disabled={loading}
        />
      </div>
      <div className="input-container">
        <label>Gender</label>
        <select
          value={gender}
          onChange={(e) => setGender(e.target.value)}
          disabled={loading}
          aria-required="true"
        >
          <option value="">Select Gender</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
        </select>
      </div>
      <button onClick={handleCreateAccount} disabled={loading}>
        {loading ? 'CREATING ACCOUNT...' : 'CREATE ACCOUNT'}
      </button>
      {error && <p className="error">{error}</p>}
    </div>
  );
};

export default CreateAccountPage;


// import React, { useState } from 'react';
// import axios from 'axios';
// import { useNavigate } from 'react-router-dom';
// import './CreateAccount.css';

// const CreateAccountPage = () => {
//   const [username, setUsername] = useState('');
//   const [email, setEmail] = useState('');
//   const [password, setPassword] = useState('');
//   const [error, setError] = useState(null);
//   const navigate = useNavigate();

//   const handleCreateAccount = async () => {
//     try {
//       await axios.post('/auth/register', { username, email, password });
//       navigate('/login'); // Redirect to login page after account creation
//     } catch (error) {
//       setError('Error creating account. Please try again.');
//     }
//   };

//   return (
//     <div className="create-account-container">
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
//           type="email"
//           placeholder="EMAIL"
//           value={email}
//           onChange={(e) => setEmail(e.target.value)}
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
//       <button onClick={handleCreateAccount}>CREATE ACCOUNT</button>
//       {error && <p className="error">{error}</p>}
//     </div>
//   );
// };

// export default CreateAccountPage;
