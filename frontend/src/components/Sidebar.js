import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode'; // Import for decoding JWT token
import '../styles/Sidebar.css';  // Custom styles

const Sidebar = () => {
  const token = localStorage.getItem('token'); // Retrieve the token from local storage
  let userRole = null;

  // Check if token exists and decode it
  if (token) {
    const decodedToken = jwtDecode(token); // Decode the token
    userRole = decodedToken.roles ? decodedToken.roles[0] : null; // Assuming roles are in an array
  }

  const [isDashboardExpanded, setIsDashboardExpanded] = useState(false);

  const toggleDashboardMenu = () => {
    setIsDashboardExpanded(!isDashboardExpanded);
  };

  return (
    <div className="sidebar">
      <ul>
        {/* <li><Link to="/newsfeed">Newsfeed</Link></li> */}
        <li><Link to="/tournaments">Tournaments</Link></li>
        {/* <li><Link to="/leaderboards">Leaderboards</Link></li> */}

        {/* Show additional items based on user role */}
        {userRole === 'USER' && (
          <>
            <li onClick={toggleDashboardMenu} className="dashboard-tab">
              <span>Dashboard</span>
            </li>
            {isDashboardExpanded && (
              <ul className="submenu">
                <li><Link to="/dashboard/my-tournaments">My tournaments</Link></li>
                <li><Link to="/dashboard/upcoming">Upcoming</Link></li>
              </ul>
            )}
          </>
        )}

        {userRole === 'ADMIN' && (
          <>
            <li><Link to="/admin/dashboard">Dashboard</Link></li> {/* Admin path update */}
          </>
        )}
        
        {/* <li><Link to="/profile">Profile</Link></li> */}
      </ul>
    </div>
  );
};

export default Sidebar;



// import React from 'react';
// import { Link } from 'react-router-dom';
// import '../styles/Sidebar.css';  // Custom styles

// const Sidebar = () => {
//   return (
//     <div className="sidebar">
//       <ul>
//         <li><Link to="/newsfeed">Newsfeed</Link></li>
//         <li><Link to="/tournaments">Tournaments</Link></li>
//         <li><Link to="/leaderboards">Leaderboards</Link></li>
//         <li><Link to="/games">Games</Link></li>
//         <li><Link to="/profile">Profile</Link></li>
//       </ul>
//     </div>
//   );
// };

// export default Sidebar;
