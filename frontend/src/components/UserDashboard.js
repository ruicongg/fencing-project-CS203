// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import { Route, Routes, useNavigate } from 'react-router-dom';
// // import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
// import TournamentCard from './UserTournamentCard';  // For displaying tournaments without edit buttons for users
// import EventsList from './UserEventsList';  // For displaying events
// import UpcomingMatchesPage from './UserUpcomingMatchesPage';  // For upcoming matches

import React from 'react';
import { useNavigate, Routes, Route } from 'react-router-dom';
import EventsPage from './UserEventsPage'
import UpcomingMatchesPage from './UserUpcomingMatchesPage';
import TournamentsPage from './UserTournamentsPage';
import TournamentEventsPage from './UserTournamentsEventsPage';
import '../styles/UserDashboard.css';

// axios.defaults.baseURL = 'http://localhost:8080';

const UserDashboard = () => {
  const navigate = useNavigate();

  return (
    <div className="user-dashboard">
      {/* Navigation Buttons */}
      <div className="button-container">
        <button onClick={() => navigate('/dashboard/my-events')}>My Events</button>
        <button onClick={() => navigate('/dashboard/upcoming')}>Upcoming</button>
        <button onClick={() => navigate('/leaderboards')}>Leaderboards</button>
      </div>

      {/* Main Content */}
      <div className="main-content">
        <Routes>
          {/* Default Dashboard Route to show Tournaments */}
          <Route path="/dashboard" element={<TournamentsPage />} />

          <Route path="/dashboard/my-events" element={<EventsPage />} />
          
          {/* Upcoming Matches Route */}
          <Route path="/dashboard/upcoming" element={<UpcomingMatchesPage />} />

          {/* Leaderboards Route */}
          <Route path="/dashboard/leaderboards" element={<div>Leaderboards Content Here</div>} />

          {/* Tournament Events Route */}
          <Route path="/tournaments/:tournamentId/events" element={<TournamentEventsPage />} />
        </Routes>
      </div>
    </div>
  );
};

export default UserDashboard;

// const UserDashboard = () => {
//   const [activeTournaments, setActiveTournaments] = useState([]);
//   const [completedTournaments, setCompletedTournaments] = useState([]);
//   const [activeTab, setActiveTab] = useState('active');
//   const [filteredTournaments, setFilteredTournaments] = useState([]);
//   const [selectedTournament, setSelectedTournament] = useState(null);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState(null);

//   const navigate = useNavigate();

//   useEffect(() => {
//     fetchTournaments();
//   }, []);

//   const fetchTournaments = async () => {
//     try {
//       setLoading(true);
//       const response = await axios.get('/tournaments');
//       const now = new Date();
//       const active = response.data.filter(t => new Date(t.tournamentEndDate) > now);
//       const completed = response.data.filter(t => new Date(t.tournamentEndDate) <= now);
//       setActiveTournaments(active);
//       setCompletedTournaments(completed);
//       applyFilters(active);
//     } catch (error) {
//       setError('Failed to load tournaments');
//       console.error('Error fetching tournaments:', error);
//     } finally {
//       setLoading(false);
//     }
//   };

//   const applyFilters = (tournaments) => {
//     let filtered = tournaments;
//     const now = new Date();
//     if (activeTab === 'active') {
//       filtered = filtered.filter(t => new Date(t.startDate) <= now);
//     }
//     setFilteredTournaments(filtered);
//   };

//   const handleTabClick = (tab) => {
//     setActiveTab(tab);
//     applyFilters(tab === 'active' ? activeTournaments : completedTournaments);
//   };

//   const handleTournamentClick = (tournament) => {
//     setSelectedTournament(tournament);
//   };

//   if (loading) return <p>Loading tournaments...</p>;
//   if (error) return <p>{error}</p>;

//   return (
//     <div className="user-dashboard">
//       {/* Main Navigation Buttons */}
//       <div className="button-container">
//         <button onClick={() => navigate('/dashboard/my-tournaments')}>My Tournaments</button>
//         <button onClick={() => navigate('/dashboard/upcoming')}>Upcoming</button>
//         <button onClick={() => navigate('/leaderboards')}>Leaderboards</button>
//       </div>

//       {/* Main Content */}
//       <div className="main-content">
//         <Routes>
//           <Route path="/dashboard/my-tournaments" element={
//             <div>
//               <div className="tabs">
//                 <button
//                   onClick={() => handleTabClick('active')}
//                   className={activeTab === 'active' ? 'active' : ''}
//                 >
//                   Active
//                 </button>
//                 <button
//                   onClick={() => handleTabClick('completed')}
//                   className={activeTab === 'completed' ? 'active' : ''}
//                 >
//                   Completed
//                 </button>
//               </div>

//               <div className="tournament-cards">
//                 {activeTab === 'active' && filteredTournaments.length === 0 && <p>No active tournaments</p>}
//                 {activeTab === 'completed' && filteredTournaments.length === 0 && <p>No completed tournaments</p>}
//                 {filteredTournaments.map(tournament => (
//                   <TournamentCard
//                     key={tournament.id}
//                     tournament={tournament}
//                     onSelect={() => handleTournamentClick(tournament)}
//                     showStatus={true}
//                   />
//                 ))}
//               </div>

//               {selectedTournament && (
//                 <div className="events-section">
//                   <h2>My events for {selectedTournament.name}</h2>
//                   <EventsList tournamentId={selectedTournament.id} showWithdrawButton={false} />
//                 </div>
//               )}
//             </div>
//           } />

//           <Route path="/dashboard/upcoming" element={<UpcomingMatchesPage />} />
//           <Route path="/leaderboards" element={<div>Leaderboards Content Here</div>} />
//         </Routes>
//       </div>
//     </div>
//   );
// };

// export default UserDashboard;

// const UserDashboard = () => {
//   const [activeTournaments, setActiveTournaments] = useState([]);
//   const [completedTournaments, setCompletedTournaments] = useState([]);
//   const [activeTab, setActiveTab] = useState('active');
//   const [filteredTournaments, setFilteredTournaments] = useState([]);
//   const [selectedTournament, setSelectedTournament] = useState(null);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState(null);

//   // Filter states
//   const [selectedTimeframe, setSelectedTimeframe] = useState('This week');
//   const [selectedLocation, setSelectedLocation] = useState('Global');

//   useEffect(() => {
//     fetchTournaments();
//   }, []);

//   // Fetch tournaments and categorize them
//   const fetchTournaments = async () => {
//     try {
//       setLoading(true);
//       const response = await axios.get('/tournaments');
//       const now = new Date();
//       const active = response.data.filter(t => new Date(t.tournamentEndDate) > now);
//       const completed = response.data.filter(t => new Date(t.tournamentEndDate) <= now);
//       setActiveTournaments(active);
//       setCompletedTournaments(completed);
//       applyFilters(active); // Apply filters initially to active tournaments
//     } catch (error) {
//       setError('Failed to load tournaments');
//       console.error('Error fetching tournaments:', error);
//     } finally {
//       setLoading(false);
//     }
//   };

//   // Apply filters based on the selectedTimeframe and selectedLocation
//   const applyFilters = (tournaments) => {
//     let filtered = tournaments;

//     // Timeframe filter
//     if (selectedTimeframe === 'This week') {
//       const now = new Date();
//       const endOfWeek = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 7);
//       filtered = filtered.filter(t => new Date(t.startDate) <= endOfWeek);
//     } else if (selectedTimeframe === 'This month') {
//       const now = new Date();
//       const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
//       filtered = filtered.filter(t => new Date(t.startDate) <= endOfMonth);
//     } else if (selectedTimeframe === 'This year') {
//       const now = new Date();
//       const endOfYear = new Date(now.getFullYear(), 11, 31);
//       filtered = filtered.filter(t => new Date(t.startDate) <= endOfYear);
//     }

//     // Location filter
//     if (selectedLocation === 'Local') {
//       filtered = filtered.filter(t => t.location === 'Local');
//     }

//     setFilteredTournaments(filtered);
//   };

//   const handleTimeframeChange = (e) => {
//     setSelectedTimeframe(e.target.value);
//     applyFilters(activeTab === 'active' ? activeTournaments : completedTournaments);
//   };

//   const handleLocationChange = (e) => {
//     setSelectedLocation(e.target.value);
//     applyFilters(activeTab === 'active' ? activeTournaments : completedTournaments);
//   };

//   const handleTabClick = (tab) => {
//     setActiveTab(tab);
//     applyFilters(tab === 'active' ? activeTournaments : completedTournaments);
//   };

//   const handleTournamentClick = (tournament) => {
//     setSelectedTournament(tournament);
//   };

//   if (loading) {
//     return <p>Loading tournaments...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>;
//   }

//   return (
//     <div className="user-dashboard">
//       {/* Sidebar */}
//       <div className="sidebar">
//         <ul>
//           <li>
//             <Link to="/dashboard/my-tournaments">Dashboard</Link>
//             <ul>
//               <li><Link to="/dashboard/my-tournaments">My Tournaments</Link></li>
//               <li><Link to="/dashboard/upcoming">Upcoming</Link></li>
//             </ul>
//           </li>
//           <li><Link to="/leaderboards">Leaderboards</Link></li>
//         </ul>
//       </div>

//       {/* Main Content */}
//       <div className="main-content">
//         {/* Routes */}
//         <Routes>
//           {/* Default route for Dashboard */}
//           <Route path="/dashboard/my-tournaments" element={
//             <div>
//               {/* Tabs for Active and Completed */}
//               <div className="tabs">
//                 <button
//                   onClick={() => handleTabClick('active')}
//                   className={activeTab === 'active' ? 'active' : ''}
//                 >
//                   Active
//                 </button>
//                 <button
//                   onClick={() => handleTabClick('completed')}
//                   className={activeTab === 'completed' ? 'active' : ''}
//                 >
//                   Completed
//                 </button>
//               </div>

//               {/* Filter By Section */}
//               <div className="filter-by">
//                 <h4>Filter by</h4>
//                 <select value={selectedTimeframe} onChange={handleTimeframeChange}>
//                   <option value="This week">This week</option>
//                   <option value="This month">This month</option>
//                   <option value="This year">This year</option>
//                 </select>

//                 <select value={selectedLocation} onChange={handleLocationChange}>
//                   <option value="Global">Global</option>
//                   <option value="Local">Local</option>
//                 </select>
//               </div>

//               <div className="tournament-cards">
//                 {activeTab === 'active' && filteredTournaments.length === 0 && <p>No active tournaments</p>}
//                 {activeTab === 'completed' && filteredTournaments.length === 0 && <p>No completed tournaments</p>}

//                 {filteredTournaments.map(tournament => (
//                   <TournamentCard
//                     key={tournament.id}
//                     tournament={tournament}
//                     onSelect={() => handleTournamentClick(tournament)}
//                     showStatus={true}
//                   />
//                 ))}
//               </div>

//               {/* Selected tournament's events */}
//               {selectedTournament && (
//                 <div className="events-section">
//                   <h2>My events for {selectedTournament.name}</h2>
//                   <EventsList tournamentId={selectedTournament.id} showWithdrawButton={false} />
//                 </div>
//               )}
//             </div>
//           } />

//           {/* Upcoming Matches Route */}
//           <Route path="/dashboard/upcoming" element={<UpcomingMatchesPage />} />

//           {/* Leaderboards Route */}
//           <Route path="/leaderboards" element={<div>Leaderboards Content Here</div>} />
//         </Routes>
//       </div>
//     </div>
//   );
// };

// export default UserDashboard;







// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
// import TournamentCard from './UserTournamentCard';  // For displaying tournaments without edit buttons for users
// import EventsList from './UserEventsList';  // For displaying events
// import UpcomingMatchesPage from './UserUpcomingMatchesPage';  // For upcoming matches
// import '../styles/UserDashboard.css';

// const UserDashboard = () => {
//   const [activeTournaments, setActiveTournaments] = useState([]);
//   const [completedTournaments, setCompletedTournaments] = useState([]);
//   const [filteredTournaments, setFilteredTournaments] = useState([]); // For filtered results
//   const [selectedTournament, setSelectedTournament] = useState(null);
//   const [activeTab, setActiveTab] = useState('myTournaments');
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState(null);

//   // Filter states
//   const [selectedTimeframe, setSelectedTimeframe] = useState('This week');
//   const [selectedLocation, setSelectedLocation] = useState('Global');

//   useEffect(() => {
//     if (activeTab === 'myTournaments') {
//       fetchTournaments();
//     }
//   }, [activeTab]);

//   // Fetch tournaments
//   const fetchTournaments = async () => {
//     try {
//       setLoading(true);
//       const response = await axios.get('/tournaments');
//       const now = new Date();
//       const active = response.data.filter(tournament => new Date(tournament.endDate) > now);
//       const completed = response.data.filter(tournament => new Date(tournament.endDate) <= now);
//       setActiveTournaments(active);
//       setCompletedTournaments(completed);
//       applyFilters(activeTab === 'active' ? active : completed); // Apply filters initially
//     } catch (error) {
//       setError('Failed to load tournaments');
//       console.error('Error fetching tournaments:', error);
//     } finally {
//       setLoading(false);
//     }
//   };

//   // Apply filters based on the selectedTimeframe and selectedLocation
//   const applyFilters = (tournaments) => {
//     let filtered = tournaments;

//     // Timeframe filter
//     if (selectedTimeframe === 'This week') {
//       const now = new Date();
//       const endOfWeek = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 7);
//       filtered = filtered.filter(t => new Date(t.startDate) <= endOfWeek);
//     } else if (selectedTimeframe === 'This month') {
//       const now = new Date();
//       const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
//       filtered = filtered.filter(t => new Date(t.startDate) <= endOfMonth);
//     } else if (selectedTimeframe === 'This year') {
//       const now = new Date();
//       const endOfYear = new Date(now.getFullYear(), 11, 31);
//       filtered = filtered.filter(t => new Date(t.startDate) <= endOfYear);
//     }

//     // Location filter
//     if (selectedLocation === 'Local') {
//       filtered = filtered.filter(t => t.location === 'Local');
//     }

//     setFilteredTournaments(filtered);
//   };

//   const handleTimeframeChange = (e) => {
//     setSelectedTimeframe(e.target.value);
//     applyFilters(activeTab === 'myTournaments' ? activeTournaments : completedTournaments);
//   };

//   const handleLocationChange = (e) => {
//     setSelectedLocation(e.target.value);
//     applyFilters(activeTab === 'myTournaments' ? activeTournaments : completedTournaments);
//   };

//   const handleTabClick = (tab) => {
//     setActiveTab(tab);
//   };

//   const handleTournamentClick = (tournament) => {
//     setSelectedTournament(tournament);
//   };

//   if (loading) {
//     return <p>Loading tournaments...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>;
//   }

//   return (
//     <Router>
//       <div className="user-dashboard">
//         {/* Tabs */}
//         <div className="tabs">
//           <button
//             onClick={() => handleTabClick('myTournaments')}
//             className={activeTab === 'myTournaments' ? 'active' : ''}
//           >
//             My Tournaments
//           </button>
//           <Link to="/upcoming-matches">
//             <button
//               onClick={() => handleTabClick('upcomingMatches')}
//               className={activeTab === 'upcomingMatches' ? 'active' : ''}
//             >
//               Upcoming
//             </button>
//           </Link>
//         </div>

//         {/* Routes for navigation */}
//         <Routes>
//           <Route
//             path="/"
//             element={
//               <div>
//                 {/* Filter By Section */}
//                 <div className="filter-by">
//                   <h4>Filter by</h4>
//                   <select value={selectedTimeframe} onChange={handleTimeframeChange}>
//                     <option value="This week">This week</option>
//                     <option value="This month">This month</option>
//                     <option value="This year">This year</option>
//                   </select>

//                   <select value={selectedLocation} onChange={handleLocationChange}>
//                     <option value="Global">Global</option>
//                     <option value="Local">Local</option>
//                   </select>
//                 </div>

//                 {/* Tournament Cards */}
//                 <div className="tournament-cards">
//                   {filteredTournaments.length > 0 ? (
//                     <div className="tournaments-list">
//                       {filteredTournaments.map(tournament => (
//                         <TournamentCard
//                           key={tournament.id}
//                           tournament={tournament}
//                           onSelect={() => handleTournamentClick(tournament)}
//                           showStatus={activeTab === 'myTournaments'}
//                         />
//                       ))}
//                     </div>
//                   ) : (
//                     <p>No tournaments found.</p>
//                   )}
//                 </div>

//                 {/* Selected tournament's events */}
//                 {selectedTournament && (
//                   <div className="events-section">
//                     <h2>My events for {selectedTournament.name}</h2>
//                     <EventsList tournamentId={selectedTournament.id} showWithdrawButton={false} />
//                   </div>
//                 )}
//               </div>
//             }
//           />
//           {/* Route for Upcoming Matches */}
//           <Route path="/upcoming-matches" element={<UpcomingMatchesPage />} />
//         </Routes>
//       </div>
//     </Router>
//   );
// };

// export default UserDashboard;


// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import TournamentCard from './TournamentCard';  // Reuse this for displaying tournaments without edit buttons for users
// import EventsList from './UserEventsList';  // Reuse this for displaying events
// import './UserDashboard.css';

// const UserDashboard = () => {
//   const [activeTournaments, setActiveTournaments] = useState([]);
//   const [completedTournaments, setCompletedTournaments] = useState([]);
//   const [filteredTournaments, setFilteredTournaments] = useState([]); // For filtered results
//   const [selectedTournament, setSelectedTournament] = useState(null);
//   const [activeTab, setActiveTab] = useState('active');
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState(null);

//   // Filter states
//   const [selectedTimeframe, setSelectedTimeframe] = useState('This week');
//   const [selectedLocation, setSelectedLocation] = useState('Global');

//   useEffect(() => {
//     const fetchTournaments = async () => {
//       try {
//         setLoading(true);
//         const response = await axios.get('/tournaments');
//         const now = new Date();
//         const active = response.data.filter(tournament => new Date(tournament.endDate) > now);
//         const completed = response.data.filter(tournament => new Date(tournament.endDate) <= now);
//         setActiveTournaments(active);
//         setCompletedTournaments(completed);
//         applyFilters(activeTab === 'active' ? active : completed); // Apply filters initially
//       } catch (error) {
//         setError('Failed to load tournaments');
//         console.error('Error fetching tournaments:', error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchTournaments();
//   }, [activeTab]);

//   // Apply filters based on the selectedTimeframe and selectedLocation
//   const applyFilters = (tournaments) => {
//     let filtered = tournaments;

//     // Timeframe filter
//     if (selectedTimeframe === 'This week') {
//       const now = new Date();
//       const endOfWeek = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 7);
//       filtered = filtered.filter(t => new Date(t.startDate) <= endOfWeek);
//     } else if (selectedTimeframe === 'This month') {
//       const now = new Date();
//       const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
//       filtered = filtered.filter(t => new Date(t.startDate) <= endOfMonth);
//     } else if (selectedTimeframe === 'This year') {
//       const now = new Date();
//       const endOfYear = new Date(now.getFullYear(), 11, 31);
//       filtered = filtered.filter(t => new Date(t.startDate) <= endOfYear);
//     }

//     // Location filter
//     if (selectedLocation === 'Local') {
//       filtered = filtered.filter(t => t.location === 'Local');
//     }

//     setFilteredTournaments(filtered);
//   };

//   const handleTimeframeChange = (e) => {
//     setSelectedTimeframe(e.target.value);
//     applyFilters(activeTab === 'active' ? activeTournaments : completedTournaments);
//   };

//   const handleLocationChange = (e) => {
//     setSelectedLocation(e.target.value);
//     applyFilters(activeTab === 'active' ? activeTournaments : completedTournaments);
//   };

//   const handleTabClick = (tab) => {
//     setActiveTab(tab);
//     applyFilters(tab === 'active' ? activeTournaments : completedTournaments);
//   };

//   const handleTournamentClick = (tournament) => {
//     setSelectedTournament(tournament);
//   };

//   if (loading) {
//     return <p>Loading tournaments...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>;
//   }

//   return (
//     <div className="user-dashboard">
//       {/* Tabs */}
//       <div className="tabs">
//         <button
//           onClick={() => handleTabClick('active')}
//           className={activeTab === 'active' ? 'active' : ''}
//         >
//           Active
//         </button>
//         <button
//           onClick={() => handleTabClick('completed')}
//           className={activeTab === 'completed' ? 'active' : ''}
//         >
//           Completed
//         </button>
//       </div>

//       {/* Filter By Section */}
//       <div className="filter-by">
//         <h4>Filter by</h4>
//         <select value={selectedTimeframe} onChange={handleTimeframeChange}>
//           <option value="This week">This week</option>
//           <option value="This month">This month</option>
//           <option value="This year">This year</option>
//         </select>

//         <select value={selectedLocation} onChange={handleLocationChange}>
//           <option value="Global">Global</option>
//           <option value="Local">Local</option>
//         </select>
//       </div>

//       {/* Tournament Cards */}
//       <div className="tournament-cards">
//         {filteredTournaments.length > 0 ? (
//           <div className="tournaments-list">
//             {filteredTournaments.map(tournament => (
//               <TournamentCard
//                 key={tournament.id}
//                 tournament={tournament}
//                 onSelect={() => handleTournamentClick(tournament)}
//                 showStatus={activeTab === 'active'}
//               />
//             ))}
//           </div>
//         ) : (
//           <p>No tournaments found.</p>
//         )}
//       </div>

//       {/* Selected tournament's events */}
//       {selectedTournament && (
//         <div className="events-section">
//           <h2>My events for {selectedTournament.name}</h2>
//           <EventsList tournamentId={selectedTournament.id} showWithdrawButton={false} />
//         </div>
//       )}
//     </div>
//   );
// };

// export default UserDashboard;


// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import TournamentCard from './UserTournamentCard';
// import EventsList from './EventsList';
// import '../styles/UserDashboard.css';

// const UserDashboard = () => {
//     const [activeTournaments, setActiveTournaments] = useState([]);
//     const [completedTournaments, setCompletedTournaments] = useState([]);
//     const [filteredTournaments, setFilteredTournaments] = useState([]);
//     const [selectedTournament, setSelectedTournament] = useState(null);
//     const [filter, setFilter] = useState({ timeframe: 'This week', location: 'Global' });
//     const [activeTab, setActiveTab] = useState('active'); // "active" or "completed"
  
//     // Fetch tournaments from the backend on load
//     useEffect(() => {
//       const fetchTournaments = async () => {
//         try {
//           const response = await axios.get('/tournaments');  // Replace with your API
//           const now = new Date();
//           const active = response.data.filter(t => new Date(t.endDate) > now);
//           const completed = response.data.filter(t => new Date(t.endDate) <= now);
//           setActiveTournaments(active);
//           setCompletedTournaments(completed);
//           setFilteredTournaments(active);  // Default to showing active tournaments
//         } catch (error) {
//           console.error('Error fetching tournaments:', error);
//         }
//       };
  
//       fetchTournaments();
//     }, []);
  
//     // Handle switching between "Active" and "Completed" tabs
//     const handleTabClick = (tab) => {
//       setActiveTab(tab);
//       if (tab === 'active') {
//         setFilteredTournaments(activeTournaments);
//       } else {
//         setFilteredTournaments(completedTournaments);
//       }
//     };
  
//     // Handle filtering logic based on timeframe and location
//     const applyFilters = () => {
//       let filtered = activeTab === 'active' ? activeTournaments : completedTournaments;
  
//       // Filter by timeframe
//       const now = new Date();
//       if (filter.timeframe === 'This week') {
//         const oneWeekFromNow = new Date();
//         oneWeekFromNow.setDate(now.getDate() + 7);
//         filtered = filtered.filter(t => new Date(t.startDate) <= oneWeekFromNow && new Date(t.endDate) >= now);
//       } else if (filter.timeframe === 'This month') {
//         const oneMonthFromNow = new Date();
//         oneMonthFromNow.setMonth(now.getMonth() + 1);
//         filtered = filtered.filter(t => new Date(t.startDate) <= oneMonthFromNow && new Date(t.endDate) >= now);
//       } else if (filter.timeframe === 'This year') {
//         const endOfYear = new Date(now.getFullYear(), 11, 31);
//         filtered = filtered.filter(t => new Date(t.startDate) <= endOfYear && new Date(t.endDate) >= now);
//       }
  
//       // Filter by location
//       if (filter.location === 'Local') {
//         filtered = filtered.filter(t => t.location === 'Local');
//       }
  
//       setFilteredTournaments(filtered);
//     };
  
//     // Handle tournament selection
//     const handleTournamentSelect = (tournament) => {
//       setSelectedTournament(tournament);
//     };
  
//     // Apply filters every time the filter state changes
//     useEffect(() => {
//       applyFilters();
//     }, [filter, activeTab, activeTournaments, completedTournaments]);
  
//     return (
//       <div className="user-dashboard">
//         <h1>User Dashboard</h1>
//         <div className="tabs">
//           <button onClick={() => handleTabClick('active')} className={activeTab === 'active' ? 'active' : ''}>Active</button>
//           <button onClick={() => handleTabClick('completed')} className={activeTab === 'completed' ? 'active' : ''}>Completed</button>
//         </div>
  
//         <div className="filters">
//           <select value={filter.timeframe} onChange={(e) => setFilter({ ...filter, timeframe: e.target.value })}>
//             <option value="This week">This week</option>
//             <option value="This month">This month</option>
//             <option value="This year">This year</option>
//           </select>
//           <select value={filter.location} onChange={(e) => setFilter({ ...filter, location: e.target.value })}>
//             <option value="Global">Global</option>
//             <option value="Local">Local</option>
//           </select>
//         </div>
  
//         <div className="tournament-cards">
//           {filteredTournaments.map(tournament => (
//             <TournamentCard
//               key={tournament.id}
//               tournament={tournament}
//               isSelected={selectedTournament && selectedTournament.id === tournament.id}
//               onSelect={() => handleTournamentSelect(tournament)}
//             />
//           ))}
//         </div>
  
//         {selectedTournament && (
//           <div className="events-section">
//             <h2>My events for {selectedTournament.name}</h2>
//             <EventsList tournamentId={selectedTournament.id} />
//           </div>
//         )}
//       </div>
//     );
//   };
  
//   export default UserDashboard;