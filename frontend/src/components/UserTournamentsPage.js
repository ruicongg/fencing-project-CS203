import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // Use navigate for routing
import '../styles/shared/index.css'; // CSS file for the page
import TournamentCard from './UserTournamentCard'; 

axios.defaults.baseURL = 'http://localhost:8080';

const TournamentsPage = () => {
  const [tournaments, setTournaments] = useState([]);
  const [filters, setFilters] = useState({
    time: 'all',
    location: 'global',
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTournaments = async () => {
      try {
        setLoading(true);
        const response = await axios.get('/tournaments'); 
        setTournaments(response.data);
        setError(null);
      } catch (error) {
        setError('Failed to load tournaments');
      } finally {
        setLoading(false);
      }
    };

    fetchTournaments();
  }, []);

  const handleFilterChange = (filterName, value) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      [filterName]: value,
    }));
  };

  // Helper functions to get date ranges based on filter selections
  const getStartOfWeek = () => {
    const date = new Date();
    const dayOfWeek = date.getDay();
    const diff = date.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);
    return new Date(date.setDate(diff));
  };

  const getEndOfWeek = () => {
    const startOfWeek = getStartOfWeek();
    return new Date(startOfWeek.setDate(startOfWeek.getDate() + 6));
  };

  const getStartOfMonth = () => new Date(new Date().getFullYear(), new Date().getMonth(), 1);
  const getEndOfMonth = () => new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0);
  const getStartOfYear = () => new Date(new Date().getFullYear(), 0, 1);
  const getEndOfYear = () => new Date(new Date().getFullYear(), 11, 31);

  // Set date range based on filter selection
  let startDate, endDate;
  switch (filters.time) {
    case 'this week':
      startDate = getStartOfWeek();
      endDate = getEndOfWeek();
      break;
    case 'this month':
      startDate = getStartOfMonth();
      endDate = getEndOfMonth();
      break;
    case 'this year':
      startDate = getStartOfYear();
      endDate = getEndOfYear();
      break;
    default:
      startDate = null;
      endDate = null;
      break;
  }

  // Filter tournaments based on date and location criteria
  const filteredTournaments = tournaments.filter((tournament) => {
    const tournamentStart = new Date(tournament.tournamentStartDate);
    const tournamentEnd = new Date(tournament.tournamentEndDate);

    // Time filter
    const withinTimeRange =
      !startDate || !endDate || 
      (tournamentStart >= startDate && tournamentStart <= endDate) ||
      (tournamentEnd >= startDate && tournamentEnd <= endDate) ||
      (tournamentStart <= startDate && tournamentEnd >= endDate);

    // Location filter
    const locationMatch =
      filters.location === 'global' || 
      (tournament.location && tournament.location.toLowerCase() === filters.location.toLowerCase());

    return withinTimeRange && locationMatch;
  });

  const handleTournamentClick = (tournamentId) => {
    navigate(`/dashboard/tournaments/${tournamentId}/events`);
  };

  return (
    <div className="dashboard">
      {error && (
        <div className="error-container">
          <svg className="error-icon" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
          </svg>
          <span className="error-message">{error}</span>
          <button className="close-error-button" onClick={() => setError(null)}>✕</button>
        </div>
      )}
      
      <h1 className="dashboard-title">Explore Tournaments</h1>

      {/* Filter Section */}
      <div className="filter-by">
        <label htmlFor="timeFilter">Time</label>
        <select
          id="timeFilter"
          value={filters.time}
          onChange={(e) => handleFilterChange('time', e.target.value)}
        >
          <option value="all">All</option>
          <option value="this week">This week</option>
          <option value="this month">This month</option>
          <option value="this year">This year</option>
        </select>

        <label htmlFor="locationFilter">Location</label>
        <select
          id="locationFilter"
          value={filters.location}
          onChange={(e) => handleFilterChange('location', e.target.value)}
        >
          <option value="global">Global</option>
          <option value="local">Local</option>
        </select>
      </div>

      {/* Loading, Error, and Tournaments Display */}
      {loading ? (
        <p className="loading-message">Loading tournaments...</p>
      ) : error ? (
        <p className="error-message">{error}</p>
      ) : (
        <div className="section-container">
          <div className="tournament-list-container">
            {filteredTournaments.length > 0 ? (
              filteredTournaments.map((tournament) => (
                <TournamentCard
                  key={tournament.id}
                  tournament={tournament}
                  onSelect={() => handleTournamentClick(tournament.id)}
                />
              ))
            ) : (
              <p className="no-tournaments-message">No tournaments found.</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default TournamentsPage;

// const TournamentsPage = () => {
//   const [tournaments, setTournaments] = useState([]);
//   const [filters, setFilters] = useState({
//     time: 'all',
//     location: 'global',
//   });
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const navigate = useNavigate();

//   useEffect(() => {
//     const fetchTournaments = async () => {
//       try {
//         setLoading(true);
//         const response = await axios.get('/tournaments'); // Ensure no `params` are passed to avoid backend issues
//         setTournaments(response.data);
//         setError(null);
//       } catch (error) {
//         setError('Failed to load tournaments');
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchTournaments();
//   }, []);

//   const handleFilterChange = (filterName, value) => {
//     setFilters((prevFilters) => ({
//       ...prevFilters,
//       [filterName]: value,
//     }));
//   };

//   // Helper functions to get date ranges based on filter selections
//   const getStartOfWeek = () => {
//     const date = new Date();
//     const dayOfWeek = date.getDay();
//     const diff = date.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1); // Adjust for Sunday as the start of the week
//     return new Date(date.setDate(diff));
//   };

//   const getEndOfWeek = () => {
//     const startOfWeek = getStartOfWeek();
//     return new Date(startOfWeek.setDate(startOfWeek.getDate() + 6));
//   };

//   const getStartOfMonth = () => new Date(new Date().getFullYear(), new Date().getMonth(), 1);
//   const getEndOfMonth = () => new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0);
//   const getStartOfYear = () => new Date(new Date().getFullYear(), 0, 1);
//   const getEndOfYear = () => new Date(new Date().getFullYear(), 11, 31);

//   // Set date range based on filter selection
//   let startDate, endDate;
//   switch (filters.time) {
//     case 'this week':
//       startDate = getStartOfWeek();
//       endDate = getEndOfWeek();
//       break;
//     case 'this month':
//       startDate = getStartOfMonth();
//       endDate = getEndOfMonth();
//       break;
//     case 'this year':
//       startDate = getStartOfYear();
//       endDate = getEndOfYear();
//       break;
//     default:
//       startDate = null;
//       endDate = null;
//       break;
//   }

//   // Filter tournaments based on date and location criteria
//   const filteredTournaments = tournaments.filter((tournament) => {
//     const tournamentStart = new Date(tournament.tournamentStartDate);
//     const tournamentEnd = new Date(tournament.tournamentEndDate);

//     // Time filter
//     const withinTimeRange =
//       !startDate || !endDate || // "All" option (no date filter)
//       (tournamentStart >= startDate && tournamentStart <= endDate) ||
//       (tournamentEnd >= startDate && tournamentEnd <= endDate) ||
//       (tournamentStart <= startDate && tournamentEnd >= endDate);

//     // Location filter
//     const locationMatch =
//       filters.location === 'global' || tournament.location.toLowerCase() === filters.location.toLowerCase();

//     return withinTimeRange && locationMatch;
//   });

//   const handleTournamentClick = (tournamentId) => {
//     navigate(`/dashboard/tournaments/${tournamentId}/events`);
//   };

//   return (
//     <div className="dashboard">
//       {/* Error Message Container */}
//       {error && (
//         <div className="error-container">
//           <svg className="error-icon" viewBox="0 0 24 24">
//             <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
//           </svg>
//           <span className="error-message">{error}</span>
//           <button className="close-error-button" onClick={() => setError(null)}>✕</button>
//         </div>
//       )}
      
//       <h1 className="dashboard-title">Explore Tournaments</h1>

//       {/* Filter Section */}
//       <div className="filter-by">
//         <label htmlFor="timeFilter">Time</label>
//         <select
//           id="timeFilter"
//           value={filters.time}
//           onChange={(e) => handleFilterChange('time', e.target.value)}
//         >
//           <option value="all">All</option>
//           <option value="this week">This week</option>
//           <option value="this month">This month</option>
//           <option value="this year">This year</option>
//         </select>

//         <label htmlFor="locationFilter">Location</label>
//         <select
//           id="locationFilter"
//           value={filters.location}
//           onChange={(e) => handleFilterChange('location', e.target.value)}
//         >
//           <option value="global">Global</option>
//           <option value="local">Local</option>
//         </select>
//       </div>

//       {/* Loading, Error, and Tournaments Display */}
//       {loading ? (
//         <p className="loading-message">Loading tournaments...</p>
//       ) : error ? (
//         <p className="error-message">{error}</p>
//       ) : (
//         <div className="section-container">
//         <div className="tournament-list-container">
//           {filteredTournaments.length > 0 ? (
//             filteredTournaments.map((tournament) => (
//               <TournamentCard
//                 key={tournament.id}
//                 tournament={tournament}
//                 onSelect={() => handleTournamentClick(tournament.id)}
//               />
//             ))
//           ) : (
//             <p className="no-tournaments-message">No tournaments found.</p>
//           )}
//         </div>
//         </div>
//       )}
//     </div>
//   );
// };

// export default TournamentsPage;

// const TournamentsPage = () => {
//   const [tournaments, setTournaments] = useState([]);
//   const [filters, setFilters] = useState({
//     time: 'this week',
//     location: 'global',
//   });
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const navigate = useNavigate();

//   useEffect(() => {
//     const fetchTournaments = async () => {
//       try {
//         const response = await axios.get('/tournaments', { params: filters });
//         setTournaments(response.data);
//       } catch (error) {
//         setError('Failed to load tournaments');
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchTournaments();
//   }, [filters]);

//   const handleFilterChange = (filterName, value) => {
//     setFilters((prevFilters) => ({
//       ...prevFilters,
//       [filterName]: value,
//     }));
//   };

//   const handleTournamentClick = (tournamentId) => {
//     navigate(`/tournaments/${tournamentId}/events`);
//   };

//   const formatDate = (dateString) => new Date(dateString).toLocaleDateString();

//   return (
//     <div className="tournaments-page">
//       <h1>Explore Tournaments</h1>
//       <div className="filter-by">
//         <label>Time</label>
//         <select onChange={(e) => handleFilterChange('time', e.target.value)}>
//           <option value="this week">This week</option>
//           <option value="this month">This month</option>
//           <option value="this year">This year</option>
//         </select>

//         <label>Location</label>
//         <select onChange={(e) => handleFilterChange('location', e.target.value)}>
//           <option value="global">Global</option>
//           <option value="local">Local</option>
//         </select>
//       </div>

//       {loading ? (
//         <p>Loading tournaments...</p>
//       ) : error ? (
//         <p>{error}</p>
//       ) : (
//         <div className="tournament-list">
//           {tournaments.length > 0 ? (
//             tournaments.map((tournament) => (
//               <div
//                 key={tournament.id}
//                 className="tournament-card"
//                 onClick={() => handleTournamentClick(tournament.id)}
//               >
//                 <h3>{tournament.name}</h3>
//                 <p>{formatDate(tournament.startDate)} - {formatDate(tournament.endDate)}</p>
//                 <p>{tournament.venue}</p>
//               </div>
//             ))
//           ) : (
//             <p>No tournaments found.</p>
//           )}
//         </div>
//       )}
//     </div>
//   );
// };

// export default TournamentsPage;

// const TournamentsPage = () => {
//   const [tournaments, setTournaments] = useState([]);
//   const [filters, setFilters] = useState({
//     time: 'this week',
//     location: 'global',
//   });
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const navigate = useNavigate(); // Initialize navigate

//   useEffect(() => {
//     const fetchTournaments = async () => {
//       try {
//         const response = await axios.get('/tournaments', { params: filters });
//         setTournaments(response.data);
//       } catch (error) {
//         setError('Failed to load tournaments');
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchTournaments();
//   }, [filters]);

//   const handleFilterChange = (filterName, value) => {
//     setFilters((prevFilters) => ({
//       ...prevFilters,
//       [filterName]: value,
//     }));
//   };

//   const handleTournamentClick = (tournamentId) => {
//     navigate(`/tournaments/${tournamentId}/events`); // Navigate to the events page
//   };

//   const formatDate = (dateString) => {
//     return new Date(dateString).toLocaleDateString(); // Format the date
//   };

//   return (
//     <div className="tournaments-page">
//       <h1>Tournaments</h1>
//       <div className="filter-by">
//         <label>Time</label>
//         <select onChange={(e) => handleFilterChange('time', e.target.value)}>
//           <option value="this week">This week</option>
//           <option value="this month">This month</option>
//           <option value="this year">This year</option>
//         </select>

//         <label>Location</label>
//         <select onChange={(e) => handleFilterChange('location', e.target.value)}>
//           <option value="global">Global</option>
//           <option value="local">Local</option>
//         </select>
//       </div>

//       {loading ? (
//         <p>Loading tournaments...</p>
//       ) : error ? (
//         <p>{error}</p>
//       ) : (
//         <div className="tournament-list">
//           {tournaments.length > 0 ? (
//             tournaments.map((tournament) => (
//               <div
//                 key={tournament.id}
//                 className="tournament-card"
//                 onClick={() => handleTournamentClick(tournament.id)}
//               >
//                 <h3>{tournament.name}</h3>
//                 <p>{formatDate(tournament.startDate)} - {formatDate(tournament.endDate)}</p>
//                 <p>{tournament.venue}</p>
//                 <p>{tournament.participants || 0} participants</p>
//               </div>
//             ))
//           ) : (
//             <p>No tournaments found.</p>
//           )}
//         </div>
//       )}
//     </div>
//   );
// };

// export default TournamentsPage;


// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import '../styles/TournamentsPage.css'; // CSS file for the page

// const TournamentsPage = () => {
//   const [tournaments, setTournaments] = useState([]);
//   const [filters, setFilters] = useState({
//     time: 'this week',
//     location: 'global',
//   });
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);

//   useEffect(() => {
//     const fetchTournaments = async () => {
//       try {
//         const response = await axios.get('/tournaments', { params: filters });
//         setTournaments(response.data);
//       } catch (error) {
//         setError('Failed to load tournaments');
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchTournaments();
//   }, [filters]);

//   const handleFilterChange = (filterName, value) => {
//     setFilters((prevFilters) => ({
//       ...prevFilters,
//       [filterName]: value,
//     }));
//   };

//   return (
//     <div className="tournaments-page">
//       <h1>Tournaments</h1>
//       <div className="filter-by">
//         <label>Time</label>
//         <select onChange={(e) => handleFilterChange('time', e.target.value)}>
//           <option value="this week">This week</option>
//           <option value="this month">This month</option>
//           <option value="this year">This year</option>
//         </select>

//         <label>Location</label>
//         <select onChange={(e) => handleFilterChange('location', e.target.value)}>
//           <option value="global">Global</option>
//           <option value="local">Local</option>
//         </select>
//       </div>

//       {loading ? (
//         <p>Loading tournaments...</p>
//       ) : error ? (
//         <p>{error}</p>
//       ) : (
//         <div className="tournament-list">
//           {tournaments.map((tournament) => (
//             <div key={tournament.id} className="tournament-card" onClick={() => window.location.href = `/tournaments/${tournament.id}/events`}>
//               <h3>{tournament.name}</h3>
//               <p>{tournament.startDate} - {tournament.endDate}</p>
//               <p>{tournament.venue}</p>
//               <p>{tournament.participants} participants</p>
//             </div>
//           ))}
//         </div>
//       )}
//     </div>
//   );
// };

// export default TournamentsPage;
