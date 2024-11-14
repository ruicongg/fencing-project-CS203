import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import '../styles/shared/index.css';
import { FormField } from './shared/FormField';

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
        setError('Failed to update profile.');
        console.error('Error updating profile:', error);
      }
    };
  
    if (loading) return <p>Loading profile...</p>;
    if (error) return <p className="error">{error}</p>;
  
    return (
        <div className="dashboard">
            {/* Error Message Container */}
            {error && (
                <div className="error-container">
                <svg className="error-icon" viewBox="0 0 24 24">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
                </svg>
                <span className="error-message">{error}</span>
                <button className="close-error-button" onClick={() => setError(null)}>✕</button>
                </div>
            )}
          <h1 className="dashboard-title">User Profile</h1>
      
          <div className="section-container">
            {successMessage && <p className="success-message">{successMessage}</p>}
      
            <div className="modal">
              <p className="input">
                <strong>ID: </strong>
                <span>{user.id}</span>
              </p>
      
              <p className="input">
                <strong>Username: </strong>
                {editing ? (
                  <FormField
                    id="username"
                    type="text"
                    value={editableUser.username}
                    onChange={handleInputChange}
                    placeholder="Enter username"
                  />
                ) : (
                  <span>{user.username}</span>
                )}
              </p>
      
              <p className="input">
                <strong>Email: </strong>
                {editing ? (
                  <FormField
                    id="email"
                    type="email"
                    value={editableUser.email}
                    onChange={handleInputChange}
                    placeholder="Enter email"
                  />
                ) : (
                  <span>{user.email}</span>
                )}
              </p>
      
              <p className="profile-field">
                <strong>Gender: </strong>
                <span>{user.gender}</span>
              </p>
      
              <p className="profile-field">
                <strong>ELO: </strong>
                <span>{user.elo}</span>
              </p>
            
      
                {/* Editable Password Field */}
                {editing && (
                <p className="input">
                    <strong>New Password: </strong>
                    <FormField
                    id="password"
                    type="password"
                    value={editableUser.password}
                    onChange={handleInputChange}
                    placeholder="Enter new password"
                    />
                </p>
                )}
            </div>
      
            {/* Profile action buttons */}
            <div className="modal-actions">
              {editing ? (
                <>
                  <button onClick={handleSaveChanges} className="add-button">
                    Save Changes
                  </button>
                  <button onClick={() => setEditing(false)} className="cancel-button">
                    Cancel
                  </button>
                </>
              ) : (
                <button onClick={() => setEditing(true)} className="edit-button">
                  Edit Profile
                </button>
              )}
            </div>
          </div>
        </div>
      );
      
  };
  
  export default UserProfilePage;
