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
  const [userId, setUserId] = useState(null);
  const [filters, setFilters] = useState({
    time: 'this week',
    location: 'global',
    weapon: 'all',
  });
  const [selectedMatch, setSelectedMatch] = useState(null);

  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token || isTokenExpired(token)) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      navigate('/login');
    } else {
      const fetchUserId = async () => {
        try {
          const response = await axios.get('/users/id', {
            headers: { Authorization: `Bearer ${token}` },
          });
          setUserId(response.data);
        } catch (error) {
          setError('Failed to fetch user ID.');
          console.error('Error fetching user ID:', error);
        }
      };
      fetchUserId();
    }
  }, [token, navigate]);

  useEffect(() => {
    const fetchMatches = async () => {
      if (!userId) return; // Wait until userId is available
      try {
        const response = await axios.get(`/matches`, {
          headers: { Authorization: `Bearer ${token}` },
          params: filters,
        });
        const userMatches = response.data.filter(
          (match) => match.player1.id === userId || match.player2.id === userId
        );
        setMatches(userMatches);
      } catch (error) {
        setError('Failed to fetch upcoming matches.');
        console.error('Error fetching matches:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchMatches();
  }, [filters, token, userId]);

  const handleFilterChange = (filterName, value) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      [filterName]: value,
    }));
  };

  const isTokenExpired = (token) => {
    if (!token) return true;
    try {
      const decodedToken = jwtDecode(token);
      return decodedToken.exp < Date.now() / 1000;
    } catch {
      return true;
    }
  };

  const groupMatchesByDate = (matches) => {
    return matches.reduce((grouped, match) => {
      const date = new Date(match.event.startDate).toLocaleDateString('en-CA');
      grouped[date] = grouped[date] || [];
      grouped[date].push(match);
      return grouped;
    }, {});
  };

  const getStageType = (match) => {
    if (match.groupStage) return "Group Stage";
    if (match.knockoutStage) return "Knockout Stage";
    return "Unknown Stage";
  };

  const renderMatchesByDate = (groupedMatches) => {
    const today = new Date().toLocaleDateString('en-CA');
    const tomorrow = new Date(Date.now() + 86400000).toLocaleDateString('en-CA');

    const todayMatches = groupedMatches[today] || [];
    const tomorrowMatches = groupedMatches[tomorrow] || [];
    const otherDates = Object.keys(groupedMatches).filter(date => date !== today && date !== tomorrow);

    return (
      <div>
        {renderMatchSection("Today", today, todayMatches)}
        {renderMatchSection("Tomorrow", tomorrow, tomorrowMatches)}
        {otherDates.map(date => renderMatchSection(date, date, groupedMatches[date]))}
      </div>
    );
  };

  const renderMatchSection = (title, date, matches) => (
    <div className="date-section" key={date}>
      <h2>{title}</h2>
      {matches.length > 0 ? (
        matches.map((match) => {
          const opponent = match.player1.id === userId ? match.player2 : match.player1;
          const userScore = match.player1.id === userId ? match.player1Score : match.player2Score;
          const opponentScore = match.player1.id === userId ? match.player2Score : match.player1Score;
          const stageType = getStageType(match);

          return (
            <div key={match.id} className="match-item">
              <p>Match ID: {match.id}, {stageType}</p>
              <p>
                Event: {match.event.gender}, {match.event.weapon}, 
                {new Date(match.event.startDate).toLocaleString()} - 
                {new Date(match.event.endDate).toLocaleString()}
              </p>
              <p>Venue: {match.event.tournament.venue}</p>
              <p>Opponent: Player {opponent.id} @{opponent.username}</p>
              {(userScore !== 0 && opponentScore !== 0) && (
                <button onClick={() => setSelectedMatch(match)} className="view-results-button">
                  View results
                </button>
              )}
            </div>
          );
        })
      ) : (
        <p>No matches for {title.toLowerCase()}</p>
      )}
    </div>
  );

  if (loading) return <p>Loading matches...</p>;
  if (error) return <p>{error}</p>;

  const groupedMatches = groupMatchesByDate(matches);

  return (
    <div className="upcoming-matches-page">
      <h1>Upcoming Matches</h1>
      <div className="filter-by">
        <h3>Filter by</h3>
        <FilterDropdown label="Time" options={["this week", "this month", "this year"]} value={filters.time} onChange={(value) => handleFilterChange('time', value)} />
        <FilterDropdown label="Location" options={["global", "local"]} value={filters.location} onChange={(value) => handleFilterChange('location', value)} />
        <FilterDropdown label="Weapon" options={["all", "Foil", "Epee", "Sabre"]} value={filters.weapon} onChange={(value) => handleFilterChange('weapon', value)} />
      </div>
      {renderMatchesByDate(groupedMatches)}
      {selectedMatch && (
        <MatchResultsPopup match={selectedMatch} userId={userId} onClose={() => setSelectedMatch(null)} />
      )}
    </div>
  );
};

const FilterDropdown = ({ label, options, value, onChange }) => (
  <div>
    <label>{label}</label>
    <select value={value} onChange={(e) => onChange(e.target.value)}>
      {options.map(opt => <option key={opt} value={opt}>{opt}</option>)}
    </select>
  </div>
);

const MatchResultsPopup = ({ match, userId, onClose }) => {
  const userScore = match.player1.id === userId ? match.player1Score : match.player2Score;
  const opponentScore = match.player1.id === userId ? match.player2Score : match.player1Score;
  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h3>Winner: Player {match.winner?.id} @{match.winner?.username}</h3>
        <p>Your score: {userScore}</p>
        <p>Opponent's score: {opponentScore}</p>
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
};

export default UpcomingMatchesPage;


// const UpcomingMatchesPage = () => {
//   const [matches, setMatches] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const [userId, setUserId] = useState(null);
//   const [filters, setFilters] = useState({
//     time: 'this week',
//     location: 'global',
//     weapon: 'all',
//   });
//   const [selectedMatch, setSelectedMatch] = useState(null); // For showing results in a popup

//   const user = JSON.parse(localStorage.getItem('user')); // Fetch the current user
//   const userName = user?.username; // Get the username from the current user

//   const navigate = useNavigate();
//   const token = localStorage.getItem('token'); // Get JWT token from localStorage

//   useEffect(() => {
//     const fetchUserId = async () => {
//       try {
//         const response = await axios.get('/users/id', {
//           headers: { Authorization: `Bearer ${token}` },
//         });
//         setUserId(response.data);
//       } catch (error) {
//         console.error('Failed to fetch user ID:', error);
//         navigate('/login'); // Redirect if there’s an error
//       }
//     };

//     if (token) fetchUserId();
//   }, [token, navigate]);

//   useEffect(() => {
//     if (isTokenExpired(token)) {
//       // Token is expired, log the user out and redirect to login
//       localStorage.removeItem('token');
//       localStorage.removeItem('user');
//       navigate('/login');
//     } else {
//       const fetchMatches = async () => {
//         try {
//           const response = await axios.get(`/matches`, {
//             headers: {
//               Authorization: `Bearer ${token}`, // Send the token in the request headers
//             },
//             params: filters,
//           });
          
//           const userMatches = response.data.filter(
//             (match) => match.player1.id === userId || match.player2.id === userId
//           );
//           setMatches(userMatches);

//         } catch (error) {
//           setError('Failed to fetch upcoming matches.');
//           console.error('Error fetching matches:', error);

//         } finally {
//           setLoading(false);

//         }
//       };

//       fetchMatches();
//     }
//   }, [filters, token, navigate]);

//   const handleFilterChange = (filterName, value) => {
//     setFilters((prevFilters) => ({
//       ...prevFilters,
//       [filterName]: value,
//     }));
//   };

//   const isTokenExpired = (token) => {
//     if (!token) return true; // No token found
    
//     try {
//       const decodedToken = jwtDecode(token);
//       const currentTime = Date.now() / 1000; // Get current time in seconds
//       return decodedToken.exp < currentTime; // Compare token expiration time with the current time
//     } catch (error) {
//       console.error('Invalid token:', error);
//       return true; // Invalid token should be treated as expired
//     }
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

//   // Show the match results popup
//   const handleViewResults = (match) => {
//     setSelectedMatch(match);
//   };

//   // Close the match results popup
//   const handleClosePopup = () => {
//     setSelectedMatch(null);
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
//               const opponent = match.player1.id === userId ? match.player2 : match.player1;
//               const userScore = match.player1.id === userId ? match.player1Score : match.player2Score;
//               const opponentScore = match.player1.id === userId ? match.player2Score : match.player1Score;
//               const stageType = match.groupStage ? "Group Stage" : match.knockoutStage ? "Knockout Stage" : "Unknown Stage";

//               return (
//                 <div key={match.id} className="match-item">
//                   <p>Match ID: {match.id}, {stageType}</p>
//                   <p>
//                     Event: {match.event.gender}, {match.event.weapon}, 
//                     {new Date(match.event.startDate).toLocaleString()} - 
//                     {new Date(match.event.endDate).toLocaleString()}
//                   </p>
//                   <p>Venue: {match.event.tournament.venue}</p>
//                   <p>Opponent: Player {opponent.id} @{opponent.username}</p>

//                   {/* Conditionally show the View Results button if scores are set */}
//                   {(userScore !== 0 && opponentScore !== 0) && (
//                     <button onClick={() => handleViewResults(match)} className="view-results-button">
//                       View results
//                     </button>
//                   )}
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
//               const opponent = match.player1.id === userId ? match.player2 : match.player1;
//               const stageType = match.groupStage ? "Group Stage" : match.knockoutStage ? "Knockout Stage" : "Unknown Stage";

//               return (
//                 <div key={match.id} className="match-item">
//                   <p>Match ID: {match.id}, {stageType}</p>
//                   <p>
//                     Event: {match.event.gender}, {match.event.weapon}, 
//                     {new Date(match.event.startDate).toLocaleString()} - 
//                     {new Date(match.event.endDate).toLocaleString()}
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
//               const opponent = match.player1.id === userId ? match.player2 : match.player1;
//               const stageType = match.groupStage ? "Group Stage" : match.knockoutStage ? "Knockout Stage" : "Unknown Stage";

//               return (
//                 <div key={match.id} className="match-item">
//                   <p>Match ID: {match.id}, {stageType}</p>
//                   <p>
//                     Event: {match.event.gender}, {match.event.weapon}, 
//                     {new Date(match.event.startDate).toLocaleString()} - 
//                     {new Date(match.event.endDate).toLocaleString()}
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

//       {/* Results Popup */}
//       {selectedMatch && (
//         <div className="popup-overlay">
//           <div className="popup-content">
//             <h3>Winner: Player {selectedMatch.winner.id} @{selectedMatch.winner.username}</h3>
//             <p>Your score: {selectedMatch.player1.id === userId ? selectedMatch.player1Score : selectedMatch.player2Score}</p>
//             <p>Opponent's score: {selectedMatch.player1.id === userId ? selectedMatch.player2Score : selectedMatch.player1Score}</p>
//             <button onClick={handleClosePopup}>Close</button>
//           </div>
//         </div>
//       )}
//     </div>
//   );
// };

// export default UpcomingMatchesPage;

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
