import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import '../styles/UserProfilePage.css';

const UserProfilePage = () => {
  const [user, setUser] = useState({});
  const [editableUser, setEditableUser] = useState({
    username: '',
    email: '',
    password: '',
  });
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const token = localStorage.getItem('token');
  const navigate = useNavigate();

  useEffect(() => {
    if (!token || isTokenExpired(token)) {
      localStorage.removeItem('token');
      navigate('/login');
    } else {
      fetchUserId();
    }
  }, [token, navigate]);

  const fetchUserId = async () => {
    try {
      const response = await axios.get('/users/id', {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchUserProfile(response.data);
    } catch (error) {
      setError('Failed to fetch user ID.');
      console.error('Error fetching user ID:', error);
    }
  };

  const fetchUserProfile = async (userId) => {
    try {
      const response = await axios.get(`/players/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUser(response.data);
      setEditableUser({
        username: response.data.username,
        email: response.data.email,
        password: '', // Leave password blank initially
      });
    } catch (error) {
      setError('Failed to fetch user profile.');
      console.error('Error fetching user profile:', error);
    } finally {
      setLoading(false);
    }
  };

  const isTokenExpired = (token) => {
    try {
      const decodedToken = jwtDecode(token);
      return decodedToken.exp < Date.now() / 1000;
    } catch {
      return true;
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditableUser((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSaveChanges = async () => {
    setError(null);
    setSuccessMessage(null);

    // Validate username
    if (!editableUser.username.trim()) {
      setError('Username cannot be empty.');
      return;
    }

    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(editableUser.email)) {
      setError('Please enter a valid email address.');
      return;
    }

    try {
      const response = await axios.put(
        `/users/${user.id}`,
        editableUser,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // Update the token in localStorage with the new JWT from backend response
      const newToken = response.data.token;
      localStorage.setItem('token', newToken);

      // Update user data in state
      setUser((prev) => ({
        ...prev,
        username: editableUser.username,
        email: editableUser.email,
      }));

      // Clear password field after save, if applicable
      if (editableUser.password) setEditableUser((prev) => ({ ...prev, password: '' }));

      setSuccessMessage('Profile updated successfully!');
      setEditing(false);
    } catch (error) {
      console.error('Error updating profile:', error);

      if (error.response) {
        const errorMessage = error.response.data?.error || error.response.data?.message;
        
        if (error.response.status === 400) {
          if (errorMessage?.includes('username')) {
            setError('Username is already taken. Please choose another one.');
          } else if (errorMessage?.includes('email')) {
            setError('Email address is invalid or already registered with another account.');
          } else {
            setError(errorMessage || 'Invalid input. Please check your details.');
          }
        } else if (error.response.status === 401) {
          setError('Your session has expired. Please log in again.');
          navigate('/login');
        } else if (error.response.status === 403) {
          setError('You do not have permission to update this profile.');
        } else {
          setError('An unexpected error occurred. Please try again later.');
        }
      } else if (error.request) {
        setError('Unable to reach the server. Please check your internet connection.');
      } else {
        setError('An error occurred while updating your profile. Please try again.');
      }
    }
  };

  if (loading) return <p>Loading profile...</p>;
  if (error) return <p className="error">{error}</p>;

  return (
    <div className="profile-page">
      <h1>User Profile</h1>

      {successMessage && <p className="success-message">{successMessage}</p>}
      <div className="profile-field">
        <label>ID:</label>
        <span>{user.id}</span>
      </div>
      <div className="profile-field">
        <label>Username:</label>
        {editing ? (
          <input
            type="text"
            name="username"
            value={editableUser.username}
            onChange={handleInputChange}
          />
        ) : (
          <span>{user.username}</span>
        )}
      </div>
      <div className="profile-field">
        <label>Email:</label>
        {editing ? (
          <input
            type="email"
            name="email"
            value={editableUser.email}
            onChange={handleInputChange}
          />
        ) : (
          <span>{user.email}</span>
        )}
      </div>
      <div className="profile-field">
        <label>Gender:</label>
        <span>{user.gender}</span>
      </div>
      <div className="profile-field">
        <label>ELO:</label>
        <span>{user.elo}</span>
      </div>

      {/* Password is only editable */}
      {editing && (
        <div className="profile-field">
          <label>New Password:</label>
          <input
            type="password"
            name="password"
            value={editableUser.password}
            onChange={handleInputChange}
            placeholder="Enter new password"
          />
        </div>
      )}

      <div className="profile-actions">
        {editing ? (
          <>
            <button onClick={handleSaveChanges}>Save Changes</button>
            <button onClick={() => setEditing(false)}>Cancel</button>
          </>
        ) : (
          <button onClick={() => setEditing(true)}>Edit Profile</button>
        )}
      </div>
    </div>
  );
};

export default UserProfilePage;
