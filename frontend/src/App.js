import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { Navigate } from 'react-router-dom';
import LoginPage from './components/LoginPage';
import CreateAccountPage from './components/CreateAccountPage';
import AdminDashboard from './components/AdminDashboard';
import UserDashboard from './components/UserDashboard';
import TournamentsPage from './components/UserTournamentsPage';
import UpcomingMatchesPage from './components/UserUpcomingMatchesPage';
import ProtectedRoute from './components/ProtectedRoute';
import Sidebar from './components/Sidebar';
// import NotFound from './components/NotFound';
import UnauthorizedPage from './components/UnauthorizedPage';
import AdminEventDetailsPage from './components/AdminEventDetailsPage';
import AdminStageDetailsPage from './components/AdminStageDetailsPage';
import AdminMatchDetailsPage from './components/AdminMatchDetailsPage';

const App = () => {
  return (
    <Router>
      <Routes>
        {/* Add a redirect to login for the root */}
        <Route path="/" element={<Navigate to="/login" />} />

        {/* Login route */}
        <Route path="/login" element={<LoginPage />} />

        {/* Create account route */}
        <Route path="/create-account" element={<CreateAccountPage />} />

        {/* Admin routes (Protected by role 'ADMIN') */}
        <Route
          path="/admin/*"
          element={
            <ProtectedRoute requiredRole="ROLE_ADMIN">
              <AdminLayout />
            </ProtectedRoute>
          }
        />

        {/* User routes (Protected by role 'USER') */}
        <Route
          path="/dashboard/*"
          element={
            <ProtectedRoute requiredRole="ROLE_USER">
              <UserLayout />
            </ProtectedRoute>
          }
        />

        {/* Catch-all route for unauthorized access */}
        <Route path="/unauthorized" element={<UnauthorizedPage />} />

        {/* Catch-all route for 404
        <Route path="*" element={<NotFound />} /> */}
      </Routes>
    </Router>
  );
};

const AdminLayout = () => {
  return (
    <>
      <Sidebar /> {/* Sidebar for admin */}
      <Routes>
        <Route path="dashboard" element={<AdminDashboard />} />
        
        {/* Event Details */}
        <Route path="tournaments/:tournamentId/events/:eventId" element={<AdminEventDetailsPage />} />
        
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
    <>
      <Sidebar /> {/* Sidebar for users */}
      <Routes>
        <Route path="" element={<UserDashboard />} /> {/* UserDashboard as the default */}
        
        {/* My Tournaments */}
        <Route path="my-tournaments" element={<TournamentsPage />} />
        
        {/* Upcoming Matches */}
        <Route path="upcoming" element={<UpcomingMatchesPage />} />
      </Routes>
    </>
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


