import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // Use navigate for routing
import '../styles/UserTournamentsPage.css'; // CSS file for the page
import TournamentCard from './UserTournamentCard'; 

axios.defaults.baseURL = 'http://localhost:8080';

const TournamentsPage = () => {
  const [tournaments, setTournaments] = useState([]);
  const [filters, setFilters] = useState({
    time: 'this week',
    location: 'global',
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTournaments = async () => {
      try {
        setLoading(true);
        const response = await axios.get('/tournaments', { params: filters });
        setTournaments(response.data);
        setError(null);
      } catch (error) {
        setError('Failed to load tournaments');
      } finally {
        setLoading(false);
      }
    };

    fetchTournaments();
  }, [filters]);

  const handleFilterChange = (filterName, value) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      [filterName]: value,
    }));
  };

  const handleTournamentClick = (tournamentId) => {
    navigate(`/tournaments/${tournamentId}/events`);
  };

  return (
    <div className="tournaments-page">
      <h1>Explore Tournaments</h1>

      {/* Filter Section */}
      <div className="filter-by">
        <label htmlFor="timeFilter">Time</label>
        <select
          id="timeFilter"
          value={filters.time}
          onChange={(e) => handleFilterChange('time', e.target.value)}
        >
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
        <div className="tournament-list">
          {tournaments.length > 0 ? (
            tournaments.map((tournament) => (
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
      )}
    </div>
  );
};

export default TournamentsPage;

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
