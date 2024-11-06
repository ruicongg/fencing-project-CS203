import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import '../styles/LoginPage.css';
import '../styles/UserUpcomingMatchesPage.css';

axios.defaults.baseURL = 'http://localhost:8080';

const UpcomingMatchesPage = () => {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({
    time: 'this week',
    location: 'global',
    weapon: 'all',
  });
  const [selectedMatch, setSelectedMatch] = useState(null); // For showing results in a popup

  const user = JSON.parse(localStorage.getItem('user')); // Fetch the current user
  const userName = user?.username; // Get the username from the current user

  const navigate = useNavigate();
  const token = localStorage.getItem('token'); // Get JWT token from localStorage

  useEffect(() => {
    if (isTokenExpired(token)) {
      // Token is expired, log the user out and redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      navigate('/login');
    } else {
      const fetchMatches = async () => {
        try {
          const response = await axios.get(`/upcoming-matches`, {
            headers: {
              Authorization: `Bearer ${token}`, // Send the token in the request headers
            },
            params: filters,
          });
          setMatches(response.data);
        } catch (error) {
          setError('Failed to fetch upcoming matches.');
          console.error('Error fetching matches:', error);
        } finally {
          setLoading(false);
        }
      };

      fetchMatches();
    }
  }, [filters, token, navigate]);

  const handleFilterChange = (filterName, value) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      [filterName]: value,
    }));
  };

  const isTokenExpired = (token) => {
    if (!token) return true; // No token found
    
    try {
      const decodedToken = jwtDecode(token);
      const currentTime = Date.now() / 1000; // Get current time in seconds
      return decodedToken.exp < currentTime; // Compare token expiration time with the current time
    } catch (error) {
      console.error('Invalid token:', error);
      return true; // Invalid token should be treated as expired
    }
  };

  const groupMatchesByDate = (matches) => {
    const grouped = {};
    matches.forEach((match) => {
      const date = new Date(match.event.startDateTime).toDateString();
      if (!grouped[date]) {
        grouped[date] = [];
      }
      grouped[date].push(match);
    });
    return grouped;
  };

  // Show the match results popup
  const handleViewResults = (match) => {
    setSelectedMatch(match);
  };

  // Close the match results popup
  const handleClosePopup = () => {
    setSelectedMatch(null);
  };

  const renderMatchesByDate = (groupedMatches) => {
    const today = new Date().toDateString();
    const tomorrow = new Date();
    tomorrow.setDate(new Date().getDate() + 1);
    const tomorrowString = tomorrow.toDateString();

    // Today’s matches
    const todayMatches = groupedMatches[today] || [];

    // Tomorrow’s matches
    const tomorrowMatches = groupedMatches[tomorrowString] || [];

    // Matches after tomorrow
    const otherDates = Object.keys(groupedMatches).filter(
      (date) => date !== today && date !== tomorrowString
    );

    return (
      <div>
        {/* Today's Matches */}
        <div className="date-section">
          <h2>Today, {today}</h2>
          {todayMatches.length > 0 ? (
            todayMatches.map((match) => {
              const opponent = match.player1.username === userName ? match.player2 : match.player1;
              const userScore = match.player1.username === userName ? match.player1score : match.player2score;
              const opponentScore = match.player1.username === userName ? match.player2score : match.player1score;

              return (
                <div key={match.id} className="match-item">
                  <p>Match ID: {match.id}, {match.stageType}</p>
                  <p>
                    Event: {match.event.gender}, {match.event.weapon}, 
                    {new Date(match.event.startDateTime).toLocaleString()} - 
                    {new Date(match.event.endDateTime).toLocaleString()}
                  </p>
                  <p>Venue: {match.event.tournament.venue}</p>
                  <p>Opponent: Player {opponent.id} @{opponent.username}</p>

                  {/* Conditionally show the View Results button if scores are set */}
                  {(userScore !== 0 && opponentScore !== 0) && (
                    <button onClick={() => handleViewResults(match)} className="view-results-button">
                      View results
                    </button>
                  )}
                </div>
              );
            })
          ) : (
            <p>No matches for today</p>
          )}
        </div>

        {/* Tomorrow's Matches */}
        <div className="date-section">
          <h2>Tomorrow, {tomorrowString}</h2>
          {tomorrowMatches.length > 0 ? (
            tomorrowMatches.map((match) => {
              const opponent = match.player1.username === userName ? match.player2 : match.player1;
              return (
                <div key={match.id} className="match-item">
                  <p>Match ID: {match.id}, {match.stageType}</p>
                  <p>
                    Event: {match.event.gender}, {match.event.weapon}, 
                    {new Date(match.event.startDateTime).toLocaleString()} - 
                    {new Date(match.event.endDateTime).toLocaleString()}
                  </p>
                  <p>Venue: {match.event.tournament.venue}</p>
                  <p>Opponent: Player {opponent.id} @{opponent.username}</p>
                </div>
              );
            })
          ) : (
            <p>No matches for tomorrow</p>
          )}
        </div>

        {/* Matches after tomorrow */}
        {otherDates.length > 0 && otherDates.map((date) => (
          <div key={date} className="date-section">
            <h2>{date}</h2>
            {groupedMatches[date].map((match) => {
              const opponent = match.player1.username === userName ? match.player2 : match.player1;
              return (
                <div key={match.id} className="match-item">
                  <p>Match ID: {match.id}, {match.stageType}</p>
                  <p>
                    Event: {match.event.gender}, {match.event.weapon}, 
                    {new Date(match.event.startDateTime).toLocaleString()} - 
                    {new Date(match.event.endDateTime).toLocaleString()}
                  </p>
                  <p>Venue: {match.event.tournament.venue}</p>
                  <p>Opponent: Player {opponent.id} @{opponent.username}</p>
                </div>
              );
            })}
          </div>
        ))}
      </div>
    );
  };

  if (loading) {
    return <p>Loading matches...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  const groupedMatches = groupMatchesByDate(matches);

  return (
    <div className="upcoming-matches-page">
      <h1>Upcoming Matches</h1>

      {/* Filter By Section */}
      <div className="filter-by">
        <h3>Filter by</h3>
        <div>
          <label>Time</label>
          <select value={filters.time} onChange={(e) => handleFilterChange('time', e.target.value)}>
            <option value="this week">This week</option>
            <option value="this month">This month</option>
            <option value="this year">This year</option>
          </select>
        </div>
        <div>
          <label>Location</label>
          <select value={filters.location} onChange={(e) => handleFilterChange('location', e.target.value)}>
            <option value="global">Global</option>
            <option value="local">Local</option>
          </select>
        </div>
        <div>
          <label>Weapon</label>
          <select value={filters.weapon} onChange={(e) => handleFilterChange('weapon', e.target.value)}>
            <option value="all">All</option>
            <option value="Foil">Foil</option>
            <option value="Epee">Épée</option>
            <option value="Sabre">Sabre</option>
          </select>
        </div>
      </div>

      {/* Render Matches Grouped by Date */}
      {renderMatchesByDate(groupedMatches)}

      {/* Results Popup */}
      {selectedMatch && (
        <div className="popup-overlay">
          <div className="popup-content">
            <h3>Winner: Player {selectedMatch.winner.id} @{selectedMatch.winner.username}</h3>
            <p>Your score: {selectedMatch.player1.username === userName ? selectedMatch.player1score : selectedMatch.player2score}</p>
            <p>Opponent's score: {selectedMatch.player1.username === userName ? selectedMatch.player2score : selectedMatch.player1score}</p>
            <button onClick={handleClosePopup}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default UpcomingMatchesPage;

// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import '../styles/UserUpcomingMatchesPage.css';
// import { format } from 'date-fns';  // Importing date-fns for consistent formatting

// const UpcomingMatchesPage = () => {
//   const [matches, setMatches] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const [filters, setFilters] = useState({
//     time: 'this week',
//     location: 'global',
//     weapon: 'all',
//   });

//   const user = JSON.parse(localStorage.getItem('user')); // Fetch the current user
//   const userName = user?.username; // Get the username from the current user

//   useEffect(() => {
//     const fetchMatches = async () => {
//       try {
//         const response = await axios.get(`/user/upcoming-matches`, { params: filters });
//         setMatches(response.data);
//       } catch (error) {
//         setError('Failed to fetch upcoming matches.');
//         console.error('Error fetching matches:', error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchMatches();
//   }, [filters]); // Filters are applied on every update

//   const handleFilterChange = (filterName, value) => {
//     setFilters((prevFilters) => ({
//       ...prevFilters,
//       [filterName]: value,
//     }));
//   };

//   const groupMatchesByDate = (matches) => {
//     const grouped = {};
//     matches.forEach((match) => {
//       const date = new Date(match.event.startDateTime).toDateString(); // Grouping by match date
//       if (!grouped[date]) {
//         grouped[date] = [];
//       }
//       grouped[date].push(match);
//     });
//     return grouped;
//   };

//   const renderMatchesByDate = (groupedMatches) => {
//     const today = format(new Date(), 'EEEE, dd MMMM yyyy'); // Today formatted as "Today, <date>"
//     const tomorrow = new Date();
//     tomorrow.setDate(new Date().getDate() + 1);
//     const tomorrowString = format(tomorrow, 'EEEE, dd MMMM yyyy'); // "Tomorrow, <date>"

//     const todayMatches = groupedMatches[today] || [];
//     const tomorrowMatches = groupedMatches[tomorrowString] || [];
//     const otherDates = Object.keys(groupedMatches).filter(
//       (date) => date !== today && date !== tomorrowString
//     );

//     return (
//       <div>
//         {/* Today's Matches */}
//         <div className="date-section">
//           <h2>Today, {today}</h2>
//           {todayMatches.length > 0 ? (
//             todayMatches.map((match) => {
//               const opponent = match.player1.username === userName ? match.player2 : match.player1;
//               return (
//                 <div key={match.id} className="match-item">
//                   <p>Match ID: {match.id}, {match.stageType}</p>
//                   <p>
//                     Event: {match.event.gender}, {match.event.weapon}, 
//                     {format(new Date(match.event.startDateTime), 'dd MMMM yyyy, HH:mm')} - 
//                     {format(new Date(match.event.endDateTime), 'HH:mm')}
//                   </p>
//                   <p>Venue: {match.event.tournament.venue}</p>
//                   <p>Opponent: Player {opponent.id} @{opponent.username}</p>
//                 </div>
//               );
//             })
//           ) : (
//             <p>No matches for today</p>
//           )}
//         </div>

//         {/* Tomorrow's Matches */}
//         <div className="date-section">
//           <h2>Tomorrow, {tomorrowString}</h2>
//           {tomorrowMatches.length > 0 ? (
//             tomorrowMatches.map((match) => {
//               const opponent = match.player1.username === userName ? match.player2 : match.player1;
//               return (
//                 <div key={match.id} className="match-item">
//                   <p>Match ID: {match.id}, {match.stageType}</p>
//                   <p>
//                     Event: {match.event.gender}, {match.event.weapon}, 
//                     {format(new Date(match.event.startDateTime), 'dd MMMM yyyy, HH:mm')} - 
//                     {format(new Date(match.event.endDateTime), 'HH:mm')}
//                   </p>
//                   <p>Venue: {match.event.tournament.venue}</p>
//                   <p>Opponent: Player {opponent.id} @{opponent.username}</p>
//                 </div>
//               );
//             })
//           ) : (
//             <p>No matches for tomorrow</p>
//           )}
//         </div>

//         {/* Matches after tomorrow */}
//         {otherDates.length > 0 && otherDates.map((date) => (
//           <div key={date} className="date-section">
//             <h2>{date}</h2>
//             {groupedMatches[date].map((match) => {
//               const opponent = match.player1.username === userName ? match.player2 : match.player1;
//               return (
//                 <div key={match.id} className="match-item">
//                   <p>Match ID: {match.id}, {match.stageType}</p>
//                   <p>
//                     Event: {match.event.gender}, {match.event.weapon}, 
//                     {format(new Date(match.event.startDateTime), 'dd MMMM yyyy, HH:mm')} - 
//                     {format(new Date(match.event.endDateTime), 'HH:mm')}
//                   </p>
//                   <p>Venue: {match.event.tournament.venue}</p>
//                   <p>Opponent: Player {opponent.id} @{opponent.username}</p>
//                 </div>
//               );
//             })}
//           </div>
//         ))}
//       </div>
//     );
//   };

//   if (loading) {
//     return <p>Loading matches...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>;
//   }

//   const groupedMatches = groupMatchesByDate(matches);

//   return (
//     <div className="upcoming-matches-page">
//       <h1>Upcoming Matches</h1>

//       {/* Filter By Section */}
//       <div className="filter-by">
//         <h3>Filter by</h3>
//         <div>
//           <label>Time</label>
//           <select value={filters.time} onChange={(e) => handleFilterChange('time', e.target.value)}>
//             <option value="this week">This week</option>
//             <option value="this month">This month</option>
//             <option value="this year">This year</option>
//           </select>
//         </div>
//         <div>
//           <label>Location</label>
//           <select value={filters.location} onChange={(e) => handleFilterChange('location', e.target.value)}>
//             <option value="global">Global</option>
//             <option value="local">Local</option>
//           </select>
//         </div>
//         <div>
//           <label>Weapon</label>
//           <select value={filters.weapon} onChange={(e) => handleFilterChange('weapon', e.target.value)}>
//             <option value="all">All</option>
//             <option value="Foil">Foil</option>
//             <option value="Epee">Épée</option>
//             <option value="Sabre">Sabre</option>
//           </select>
//         </div>
//       </div>

//       {/* Render Matches Grouped by Date */}
//       {renderMatchesByDate(groupedMatches)}
//     </div>
//   );
// };

// export default UpcomingMatchesPage;


// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import './UpcomingMatchesPage.css';

// const UpcomingMatchesPage = () => {
//   const [matches, setMatches] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const [filters, setFilters] = useState({
//     time: 'this week',
//     location: 'global',
//     weapon: 'all',
//   });

//   const user = JSON.parse(localStorage.getItem('user')); // Fetch the current user
//   const userName = user?.username; // Get the username from the current user

//   useEffect(() => {
//     const fetchMatches = async () => {
//       try {
//         const response = await axios.get(`/user/upcoming-matches`, { params: filters });
//         setMatches(response.data);
//       } catch (error) {
//         setError('Failed to fetch upcoming matches.');
//         console.error('Error fetching matches:', error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchMatches();
//   }, [filters]);

//   const handleFilterChange = (filterName, value) => {
//     setFilters((prevFilters) => ({
//       ...prevFilters,
//       [filterName]: value,
//     }));
//   };

//   const groupMatchesByDate = (matches) => {
//     const grouped = {};
//     matches.forEach((match) => {
//       const date = new Date(match.event.startDateTime).toDateString();
//       if (!grouped[date]) {
//         grouped[date] = [];
//       }
//       grouped[date].push(match);
//     });
//     return grouped;
//   };

//   const renderMatchesByDate = (groupedMatches) => {
//     const today = new Date().toDateString();
//     const tomorrow = new Date();
//     tomorrow.setDate(new Date().getDate() + 1);
//     const tomorrowString = tomorrow.toDateString();

//     // Today’s matches
//     const todayMatches = groupedMatches[today] || [];

//     // Tomorrow’s matches
//     const tomorrowMatches = groupedMatches[tomorrowString] || [];

//     // Matches after tomorrow
//     const otherDates = Object.keys(groupedMatches).filter(
//       (date) => date !== today && date !== tomorrowString
//     );

//     return (
//       <div>
//         {/* Today's Matches */}
//         <div className="date-section">
//           <h2>Today, {today}</h2>
//           {todayMatches.length > 0 ? (
//             todayMatches.map((match) => {
//               const opponent = match.player1.username === userName ? match.player2 : match.player1;
//               return (
//                 <div key={match.id} className="match-item">
//                   <p>Match ID: {match.id}, {match.stageType}</p>
//                   <p>
//                     Event: {match.event.gender}, {match.event.weapon}, 
//                     {new Date(match.event.startDateTime).toLocaleString()} - 
//                     {new Date(match.event.endDateTime).toLocaleString()}
//                   </p>
//                   <p>Venue: {match.event.tournament.venue}</p>
//                   <p>Opponent: Player {opponent.id} @{opponent.username}</p>
//                 </div>
//               );
//             })
//           ) : (
//             <p>No matches for today</p>
//           )}
//         </div>

//         {/* Tomorrow's Matches */}
//         <div className="date-section">
//           <h2>Tomorrow, {tomorrowString}</h2>
//           {tomorrowMatches.length > 0 ? (
//             tomorrowMatches.map((match) => {
//               const opponent = match.player1.username === userName ? match.player2 : match.player1;
//               return (
//                 <div key={match.id} className="match-item">
//                   <p>Match ID: {match.id}, {match.stageType}</p>
//                   <p>
//                     Event: {match.event.gender}, {match.event.weapon}, 
//                     {new Date(match.event.startDateTime).toLocaleString()} - 
//                     {new Date(match.event.endDateTime).toLocaleString()}
//                   </p>
//                   <p>Venue: {match.event.tournament.venue}</p>
//                   <p>Opponent: Player {opponent.id} @{opponent.username}</p>
//                 </div>
//               );
//             })
//           ) : (
//             <p>No matches for tomorrow</p>
//           )}
//         </div>

//         {/* Matches after tomorrow */}
//         {otherDates.length > 0 && otherDates.map((date) => (
//           <div key={date} className="date-section">
//             <h2>{date}</h2>
//             {groupedMatches[date].map((match) => {
//               const opponent = match.player1.username === userName ? match.player2 : match.player1;
//               return (
//                 <div key={match.id} className="match-item">
//                   <p>Match ID: {match.id}, {match.stageType}</p>
//                   <p>
//                     Event: {match.event.gender}, {match.event.weapon}, 
//                     {new Date(match.event.startDateTime).toLocaleString()} - 
//                     {new Date(match.event.endDateTime).toLocaleString()}
//                   </p>
//                   <p>Venue: {match.event.tournament.venue}</p>
//                   <p>Opponent: Player {opponent.id} @{opponent.username}</p>
//                 </div>
//               );
//             })}
//           </div>
//         ))}
//       </div>
//     );
//   };

//   if (loading) {
//     return <p>Loading matches...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>;
//   }

//   const groupedMatches = groupMatchesByDate(matches);

//   return (
//     <div className="upcoming-matches-page">
//       <h1>Upcoming Matches</h1>

//       {/* Filter By Section */}
//       <div className="filter-by">
//         <h3>Filter by</h3>
//         <div>
//           <label>Time</label>
//           <select value={filters.time} onChange={(e) => handleFilterChange('time', e.target.value)}>
//             <option value="this week">This week</option>
//             <option value="this month">This month</option>
//             <option value="this year">This year</option>
//           </select>
//         </div>
//         <div>
//           <label>Location</label>
//           <select value={filters.location} onChange={(e) => handleFilterChange('location', e.target.value)}>
//             <option value="global">Global</option>
//             <option value="local">Local</option>
//           </select>
//         </div>
//         <div>
//           <label>Weapon</label>
//           <select value={filters.weapon} onChange={(e) => handleFilterChange('weapon', e.target.value)}>
//             <option value="all">All</option>
//             <option value="Foil">Foil</option>
//             <option value="Epee">Épée</option>
//             <option value="Sabre">Sabre</option>
//           </select>
//         </div>
//       </div>

//       {/* Render Matches Grouped by Date */}
//       {renderMatchesByDate(groupedMatches)}
//     </div>
//   );
// };

// export default UpcomingMatchesPage;
