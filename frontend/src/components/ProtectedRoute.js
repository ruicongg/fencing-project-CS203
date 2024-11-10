import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

const ProtectedRoute = ({ requiredRole }) => {
  const token = localStorage.getItem('token');  // Retrieve JWT token from localStorage
  
  // Check if the user is authenticated
  const isAuthenticated = !!token;

  const isTokenExpired = (token) => {
    if (!token) return true;
    try {
      const decodedToken = jwtDecode(token);
      const currentTime = Date.now() / 1000; // Current time in seconds
      return decodedToken.exp < currentTime; // Token expiration check
    } catch (error) {
      console.error('Failed to decode token', error);
      return true; // Consider the token expired if decoding fails
    }
  };

  if (!isAuthenticated || isTokenExpired(token)) {
    // Clear invalid/expired token from localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    return <Navigate to="/login" />;
  }

  // Decode the token to get the role after verifying the token is valid
  const decodedToken = jwtDecode(token);
  const userRoles = decodedToken.roles || [];

  if (!userRoles.includes(requiredRole)) {
    return <Navigate to="/unauthorized" />;
  }
  console.log("Decoded Token:", jwtDecode(token));
  // const userRole = decodedToken.role; // Ensure 'role' is part of your token payload

  // // Check if the user's role matches the required role
  // if (userRole !== requiredRole) {
  //   return <Navigate to="/unauthorized" />;
  // }

  // If authenticated and authorized, render the child routes
  return <Outlet />;
};

export default ProtectedRoute;

// import React from 'react';
// import { Navigate, Outlet } from 'react-router-dom';
// import { jwtDecode } from 'jwt-decode';  // Library to decode JWT token

// const ProtectedRoute = ({ requiredRole }) => {
//   const token = localStorage.getItem('token');  // Retrieve JWT token from localStorage
//   const userRole = localStorage.getItem('role'); // Retrieve the user's role from localStorage

//   // Check if the user is authenticated by verifying the existence of the token
//   const isAuthenticated = !!token; 

//   // Check for token expiration
//   const isTokenExpired = (token) => {
//     if (!token) return true;
//     try {
//       const decodedToken = jwtDecode(token);
//       const currentTime = Date.now() / 1000; // Current time in seconds
//       return decodedToken.exp < currentTime; // Token expiration check
//     } catch (error) {
//       console.error('Failed to decode token', error);
//       return true; // Consider the token expired if decoding fails
//     }
//   };

//   // If the user is not authenticated or the token is expired, redirect to login
//   if (!isAuthenticated || isTokenExpired(token)) {
//     // Clear invalid/expired token from localStorage
//     localStorage.removeItem('token');
//     localStorage.removeItem('user');
//     localStorage.removeItem('role');
    
//     return <Navigate to="/login" />;
//   }

//   // If the user's role does not match the required role, redirect to unauthorized page
//   if (userRole !== requiredRole) {
//     return <Navigate to="/unauthorized" />;
//   }

//   // If both authenticated and authorized, render the child routes
//   return <Outlet />;
// };

// export default ProtectedRoute;

// import React from 'react';
// import { Navigate, Outlet } from 'react-router-dom';

// const ProtectedRoute = ({ requiredRole }) => {
//   const token = localStorage.getItem('token');  // Retrieve JWT token from localStorage
//   const userRole = localStorage.getItem('role'); // Retrieve the user's role from localStorage

//   // Check if the user is authenticated by verifying the existence of the token
//   const isAuthenticated = !!token; 

//   if (!isAuthenticated) {
//     // If the user is not authenticated, redirect to the login page
//     return <Navigate to="/login" />;
//   }

//   if (userRole !== requiredRole) {
//     // If the user's role does not match the required role, redirect to unauthorized page
//     return <Navigate to="/unauthorized" />;
//   }

//   // If both authenticated and authorized, render the child routes
//   return <Outlet />;
// };

// export default ProtectedRoute;


// import React from 'react';
// import { Navigate, Outlet } from 'react-router-dom';

// // This function assumes you have a way to get the current user's role and authentication status
// // For example, you could store the user's role in a global state or use context
// const ProtectedRoute = ({ requiredRole }) => {
//   const isAuthenticated = !!localStorage.getItem('token'); // Example: check token in localStorage
//   const userRole = localStorage.getItem('role'); // Example: retrieve role from localStorage

//   if (!isAuthenticated) {
//     // If the user is not authenticated, redirect to login
//     return <Navigate to="/login" />;
//   }

//   if (userRole !== requiredRole) {
//     // If the user does not have the required role, show an error or redirect
//     return <Navigate to="/unauthorized" />;
//   }

//   // If authenticated and authorized, render the requested route
//   return <Outlet />;
// };

// export default ProtectedRoute;

