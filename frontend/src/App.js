import React from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  BrowserRouter,
} from "react-router-dom";
import { Navigate } from "react-router-dom";
import axios from 'axios';
import LoginPage from "./components/LoginPage";
import CreateAccountPage from "./components/CreateAccountPage";
import AdminDashboard from "./components/AdminDashboard";
import UserDashboard from "./components/UserDashboard";
import TournamentsPage from "./components/UserTournamentsPage";
import UpcomingMatchesPage from "./components/UserUpcomingMatchesPage";
import ProtectedRoute from "./components/ProtectedRoute";
import Sidebar from "./components/Sidebar";
// import NotFound from './components/NotFound';
import UnauthorizedPage from "./components/UnauthorizedPage";
import AdminEventDetailsPage from "./components/AdminEventDetailsPage";
import AdminStageDetailsPage from "./components/AdminStageDetailsPage";
import AdminMatchDetailsPage from "./components/AdminMatchDetailsPage";

axios.defaults.baseURL = 'http://localhost:8080';

axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token'); // Retrieve token from localStorage

    // Skip Authorization header for specific public endpoints
    const publicEndpoints = ['/api/v1/auth/authenticate', '/api/v1/auth/register'];
    
    if (token && !publicEndpoints.includes(config.url)) {
      config.headers.Authorization = `Bearer ${token}`; // Attach token to Authorization header
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Add a redirect to login for the root */}
        <Route path="/" element={<Navigate to="/login" />} />

        {/* Login route */}
        <Route path="/login" element={<LoginPage />} />

        {/* Create account route */}
        <Route path="/create-account" element={<CreateAccountPage />} />

        {/* Admin routes (Protected by role 'ADMIN') */}
        <Route
          element={<ProtectedRoute requiredRole="ROLE_ADMIN" />}
          path="/admin/*"
        >
          <Route path="dashboard" element={<AdminDashboard />} />

          {/* Event Details */}
          <Route
            path="tournaments/:tournamentId/events/:eventId"
            element={<AdminEventDetailsPage />}
          />

          {/* Stage Details */}
          <Route path="tournaments/:tournamentId/events/:eventId/knockoutStage/:stageId" element={<AdminStageDetailsPage />} />
          <Route path="tournaments/:tournamentId/events/:eventId/groupStage/:stageId" element={<AdminStageDetailsPage />} />

          {/* Match Details */}
          <Route
            path="tournaments/:tournamentId/events/:eventId/:stageType/:stageId/match/:matchId"
            element={<AdminMatchDetailsPage />}
          />
        </Route>

        {/* User routes (Protected by role 'USER') */}
        <Route
          element={<ProtectedRoute requiredRole="ROLE_USER" />}
          path="/dashboard/*"
        >
          <Route path="*" element={<UserDashboard />} />
        </Route>

        {/* Catch-all route for unauthorized access */}
        <Route path="/unauthorized" element={<UnauthorizedPage />} />

        {/* Catch-all route for 404
        <Route path="*" element={<NotFound />} /> */}
      </Routes>
    </BrowserRouter>
  );
};

const AdminLayout = () => {
  return (
    <>
      <Sidebar /> {/* Sidebar for admin */}
      <Routes>
        <Route path="dashboard" element={<AdminDashboard />} />

        {/* Event Details */}
        <Route
          path="tournaments/:tournamentId/events/:eventId"
          element={<AdminEventDetailsPage />}
        />

        {/* Stage Details */}
        <Route
          path="tournaments/:tournamentId/events/:eventId/:stageType/:stageId"
          element={<AdminStageDetailsPage />}
        />

        {/* Match Details */}
        <Route
          path="tournaments/:tournamentId/events/:eventId/:stageType/:stageId/match/:matchId"
          element={<AdminMatchDetailsPage />}
        />
      </Routes>
    </>
  );
};

const UserLayout = () => {
  return (
    <div className="user-layout">
      <Sidebar /> {/* Sidebar for users */}
      <div className="main-content">
        <Routes>
          <Route path="my-tournaments" element={<UserDashboard />} /> {/* Default redirect to My Tournaments */}
          <Route path="upcoming" element={<UpcomingMatchesPage />} />
        </Routes>
      </div>
    </div>
  );
};

export default App;

// import React from 'react';
// import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
// import LoginPage from './components/LoginPage';
// import AdminDashboard from './components/AdminDashboard';
// import UserDashboard from './components/UserDashboard';
// import EventDetailsPage from './components/AdminEventDetailsPage';
// import StageDetailsPage from './components/AdminStageDetailsPage'; // StageDetailsPage replaces GroupStage and KnockoutStage pages
// import MatchDetailsPage from './components/AdminMatchDetailsPage';
// import ProtectedRoute from './components/ProtectedRoute';
// import Sidebar from './components/Sidebar';
// import NotFound from './components/NotFound';

// const App = () => {
//   return (
//     <Router>
//       <Routes>
//         {/* Login route */}
//         <Route path="/login" element={<LoginPage />} />

//         {/* Admin routes (Protected by role 'ADMIN') */}
//         <Route
//           path="/admin/*"
//           element={
//             <ProtectedRoute requiredRole="ADMIN">
//               <AdminLayout />
//             </ProtectedRoute>
//           }
//         />

//         {/* User routes (Protected by role 'USER') */}
//         <Route
//           path="/dashboard"
//           element={
//             <ProtectedRoute requiredRole="USER">
//               <UserDashboard />
//             </ProtectedRoute>
//           }
//         />

//         {/* Catch-all route for unauthorized access */}
//         <Route path="/unauthorized" element={<UnauthorizedPage />} />

//         {/* Catch-all route for 404 */}
//         <Route path="*" element={<NotFound />} />
//       </Routes>
//     </Router>
//   );
// };

// const AdminLayout = () => {
//   return (
//     <>
//       <Sidebar /> {/* Sidebar is shown for all admin pages */}
//       <Routes>
//         <Route path="dashboard" element={<AdminDashboard />} />

//         {/* Event Details */}
//         <Route path="tournaments/:tournamentId/events/:eventId" element={<EventDetailsPage />} />

//         {/* Stage Details (for GroupStage and KnockoutStage) */}
//         <Route
//           path="tournaments/:tournamentId/events/:eventId/:stageType/:stageId"
//           element={<StageDetailsPage />}
//         />

//         {/* Match Details */}
//         <Route
//           path="tournaments/:tournamentId/events/:eventId/:stageType/:stageId/match/:matchId"
//           element={<MatchDetailsPage />}
//         />
//       </Routes>
//     </>
//   );
// };

// export default App;

// import React from 'react';
// import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
// import LoginPage from './components/LoginPage';
// import TournamentsPage from './components/TournamentsPage';
// import AdminDashboard from './components/AdminDashboard';
// import UserDashboard from './components/UserDashboard';
// import ProtectedRoute from './components/ProtectedRoute';
// import Sidebar from './components/Sidebar';

// const App = () => {
//   return (
//     <Router>
//       <Sidebar />
//       <Routes>
//         <Route path="/login" element={<LoginPage />} />
//         {/* Admin Dashboard (only accessible by Admins) */}
//         <Route
//           path="/admin/dashboard"
//           element={
//             <ProtectedRoute requiredRole="ADMIN">
//               <AdminDashboard />
//             </ProtectedRoute>
//           }
//         />
//         {/* User Dashboard (only accessible by Users) */}
//         <Route
//           path="/dashboard"
//           element={
//             <ProtectedRoute requiredRole="USER">
//               <UserDashboard />
//             </ProtectedRoute>
//           }
//         />
//       </Routes>
//     </Router>
//   );
// };

// export default App;

// import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
// import AdminDashboard from './components/AdminDashboard';
// import EventDetailsPage from './components/EventDetailsPage';
// import GroupStageDetailsPage from './components/GroupStageDetailsPage';
// import MatchDetailsPage from './components/MatchDetailsPage';

// // ...

// <Router>
//   {/* Admin routes */}
//   <Route
//     path="/admin/tournaments/:tournamentId/events/:eventId"
//     element={<EventDetailsPage />}
//   />
//   <Route
//     path="/admin/tournaments/:tournamentId/events/:eventId/groupStage/:groupStageId"
//     element={<GroupStageDetailsPage />}
//   />
//   <Route
//     path="/admin/tournaments/:tournamentId/events/:eventId/groupStage/:groupStageId/match/:matchId"
//     element={<MatchDetailsPage />}
//   />
// </Router>
