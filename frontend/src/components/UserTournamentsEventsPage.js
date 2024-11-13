import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import '../styles/UserTournamentsEventsPage.css'; // Import CSS for styling

axios.defaults.baseURL = 'http://localhost:8080';

const TournamentEventsPage = () => {
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [userEvents, setUserEvents] = useState([]); // Store events the user is registered in
  const [filters, setFilters] = useState({
    gender: 'all',
    weapon: 'all',
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { tournamentId } = useParams();
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  // Fetch user registered events
  useEffect(() => {
    const fetchUserEvents = async () => {
      if (!token) return;

      try {
        const username = jwtDecode(token).sub;
        const response = await axios.get(`/player/${username}/playerRanks`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const registeredEvents = response.data.map((rank) => rank.event.id);
        setUserEvents(registeredEvents);
      } catch (error) {
        console.error('Error fetching user events:', error);
      }
    };

    fetchUserEvents();
  }, [token]);

  // Fetch all events for the tournament
  useEffect(() => {
    const fetchEvents = async () => {
      setLoading(true);
      setError(null);

      try {
        const response = await axios.get(`/tournaments/${tournamentId}/events`);
        setEvents(response.data);
        setFilteredEvents(response.data); // Initialize with all events
      } catch (error) {
        setError('Failed to load events');
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, [tournamentId]);

  // Apply filters whenever filters or events change
  useEffect(() => {
    const applyFilters = () => {
      const { gender, weapon } = filters;

      const filtered = events.filter(event => {
        const genderMatch = gender === 'all' || event.gender === gender.toUpperCase();
        const weaponMatch = weapon === 'all' || event.weapon === weapon.toUpperCase();
        return genderMatch && weaponMatch;
      });

      setFilteredEvents(filtered);
    };

    applyFilters();
  }, [filters, events]);

  const handleFilterChange = (filterName, value) => {
    setFilters((prevFilters) => ({
      ...prevFilters,
      [filterName]: value,
    }));
  };

  const handleJoin = async (eventId) => {
    if (!token || isTokenExpired(token)) {
      alert('Session expired, please log in again');
      navigate('/login');
      return;
    }

    const username = jwtDecode(token)?.sub;

    try {
      const response = await axios.post(
        `/tournaments/${tournamentId}/events/${eventId}/players/${username}`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );

      if (response.status === 200) {
        // Update both `userEvents` and `filteredEvents` to reflect the joined status
        setUserEvents((prevUserEvents) => [...prevUserEvents, eventId]);
        setFilteredEvents((prevFilteredEvents) =>
          prevFilteredEvents.map((event) =>
            event.id === eventId ? { ...event, joined: true } : event
          )
        );
        alert('Successfully joined the event!');
      }
    } catch (error) {
      console.error('Failed to join event:', error);
      
      // Handle specific error cases from the backend
      if (error.response) {
        const errorMessage = error.response.data?.error || error.response.data?.message;
        
        if (error.response.status === 400) {
          if (errorMessage?.includes('gender')) {
            alert('You cannot join this event due to gender restrictions.');
          } else if (errorMessage?.includes('Registration')) {
            alert('Registration period for this event has ended or not yet started.');
          } else if (errorMessage?.includes('time conflict')) {
            alert('You cannot join this event as it conflicts with another event you are registered for.');
          } else {
            alert(errorMessage || 'Failed to join event. Please check event requirements.');
          }
        } else if (error.response.status === 409) {
          alert('You are already registered for this event.');
        } else {
          alert(errorMessage || 'An error occurred while trying to join the event. Please try again.');
        }
      } else {
        alert('Network error. Please check your connection and try again.');
      }
    }
  };

  const isTokenExpired = (token) => {
    try {
      const { exp } = jwtDecode(token);
      return exp * 1000 < Date.now();
    } catch (error) {
      console.error('Invalid token:', error);
      return true;
    }
  };

  const formatDate = (dateString) => new Date(dateString).toLocaleDateString();

  return (
    <div className="tournament-events-page">
      <h1>Events in Tournament</h1>
      
      {/* Filter Section */}
      <div className="filter-section">
        <label>Gender</label>
        <select
          value={filters.gender}
          onChange={(e) => handleFilterChange('gender', e.target.value)}
        >
          <option value="all">All</option>
          <option value="male">Male</option>
          <option value="female">Female</option>
        </select>

        <label>Weapon</label>
        <select
          value={filters.weapon}
          onChange={(e) => handleFilterChange('weapon', e.target.value)}
        >
          <option value="all">All</option>
          <option value="foil">Foil</option>
          <option value="epee">Épée</option>
          <option value="sabre">Saber</option>
        </select>
      </div>

      {/* Display loading, error, or events */}
      {loading ? (
        <p className="loading-message">Loading events...</p>
      ) : error ? (
        <p className="error-message">{error}</p>
      ) : (
        <div className="event-list">
          {filteredEvents.length > 0 ? (
            filteredEvents.map((event) => {
              const isJoined = userEvents.includes(event.id);

              return (
                <div key={event.id} className="event-card">
                  <h3>{event.name}</h3>
                  <p>Start: {formatDate(event.startDate)}</p>
                  <p>End: {formatDate(event.endDate)}</p>
                  <p>Gender: {event.gender}</p>
                  <p>Weapon: {event.weapon}</p>
                  <button
                    className={`join-button ${isJoined ? 'joined' : ''}`}
                    disabled={isJoined}
                    onClick={() => handleJoin(event.id)}
                  >
                    {isJoined ? 'Joined' : 'Join'}
                  </button>
                </div>
              );
            })
          ) : (
            <p className="no-events-message">No events found for this tournament.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default TournamentEventsPage;

// const TournamentEventsPage = () => {
//   const [events, setEvents] = useState([]);
//   const [filteredEvents, setFilteredEvents] = useState([]);
//   const [filters, setFilters] = useState({
//     gender: 'all',
//     weapon: 'all',
//   });
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const { tournamentId } = useParams();
//   const navigate = useNavigate();

//   useEffect(() => {
//     const fetchEvents = async () => {
//       setLoading(true);
//       setError(null);
//       try {
//         const response = await axios.get(`/tournaments/${tournamentId}/events`);
//         setEvents(response.data);
//         setFilteredEvents(response.data); // Initialize with all events
//       } catch (error) {
//         setError('Failed to load events');
//       } finally {
//         setLoading(false);
//       }
//     };
//     fetchEvents();
//   }, [tournamentId]);

//   useEffect(() => {
//     const applyFilters = () => {
//       const { gender, weapon } = filters;

//       const filtered = events.filter(event => {
//         const genderMatch = gender === 'all' || event.gender === gender.toUpperCase();
//         const weaponMatch = weapon === 'all' || event.weapon === weapon.toUpperCase();

//         return genderMatch && weaponMatch;
//       });

//       setFilteredEvents(filtered);
//     };

//     applyFilters();
//   }, [filters, events]);

//   const handleFilterChange = (filterName, value) => {
//     setFilters((prevFilters) => ({
//       ...prevFilters,
//       [filterName]: value,
//     }));
//   };

//   const handleJoin = async (eventId) => {
//     const token = localStorage.getItem('token');
//     if (!token || isTokenExpired(token)) {
//       alert('Session expired, please log in again');
//       navigate('/login');
//       return;
//     }
//     const username = jwtDecode(token)?.sub;

//     try {
//       const response = await axios.post(`/tournaments/${tournamentId}/events/${eventId}/players/${username}`, {}, {
//         headers: {
//           Authorization: `Bearer ${token}`,
//         },
//       });

//       if (response.status === 200) {
//         setFilteredEvents(filteredEvents.map(event =>
//           event.id === eventId ? { ...event, joined: true } : event
//         ));
//         alert('Successfully joined the event!');
//       }
//     } catch (error) {
//       console.error('Failed to join event', error);
//       alert('An error occurred while trying to join the event. Please try again.');
//     }
//   };

//   const isTokenExpired = (token) => {
//     try {
//       const { exp } = jwtDecode(token);
//       return exp * 1000 < Date.now();
//     } catch (error) {
//       console.error('Invalid token:', error);
//       return true;
//     }
//   };

//   const formatDate = (dateString) => new Date(dateString).toLocaleDateString();

//   return (
//     <div className="tournament-events-page">
//       <h1>Events in Tournament</h1>
      
//       {/* Filter Section */}
//       <div className="filter-section">
//         <label>Gender</label>
//         <select
//           value={filters.gender}
//           onChange={(e) => handleFilterChange('gender', e.target.value)}
//         >
//           <option value="all">All</option>
//           <option value="male">Male</option>
//           <option value="female">Female</option>
//         </select>

//         <label>Weapon</label>
//         <select
//           value={filters.weapon}
//           onChange={(e) => handleFilterChange('weapon', e.target.value)}
//         >
//           <option value="all">All</option>
//           <option value="foil">Foil</option>
//           <option value="epee">Épée</option>
//           <option value="sabre">Saber</option>
//         </select>
//       </div>

//       {/* Display loading, error, or events */}
//       {loading ? (
//         <p className="loading-message">Loading events...</p>
//       ) : error ? (
//         <p className="error-message">{error}</p>
//       ) : (
//         <div className="event-list">
//           {filteredEvents.length > 0 ? (
//             filteredEvents.map((event) => (
//               <div key={event.id} className="event-card">
//                 <h3>{event.name}</h3>
//                 <p>Start: {formatDate(event.startDate)}</p>
//                 <p>End: {formatDate(event.endDate)}</p>
//                 <p>Gender: {event.gender}</p>
//                 <p>Weapon: {event.weapon}</p>
//                 <button
//                   className={`join-button ${event.joined ? 'joined' : ''}`}
//                   disabled={event.joined}
//                   onClick={() => handleJoin(event.id)}
//                 >
//                   {event.joined ? 'Joined' : 'Join'}
//                 </button>
//               </div>
//             ))
//           ) : (
//             <p className="no-events-message">No events found for this tournament.</p>
//           )}
//         </div>
//       )}
//     </div>
//   );
// };

// export default TournamentEventsPage;


// const TournamentEventsPage = ({ match }) => {
//   const [events, setEvents] = useState([]);
//   const [filters, setFilters] = useState({
//     gender: 'all',
//     weapon: 'all',
//   });
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const tournamentId = match.params.tournamentId; // Extract tournament ID from the URL
//   const navigate = useNavigate(); // For navigation if needed

//   useEffect(() => {
//     const fetchEvents = async () => {
//       try {
//         const response = await axios.get(`/tournaments/${tournamentId}/events`, { params: filters });
//         setEvents(response.data);
//       } catch (error) {
//         setError('Failed to load events');
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchEvents();
//   }, [filters, tournamentId]);

//   const handleJoin = async (eventId) => {
//     try {
//       const token = localStorage.getItem('token');  // Get the JWT token
//       if (!token) {
//         console.error('No token found, please log in');
//         return;
//       }

//       // Optionally, check token expiration before proceeding
//       const decodedToken = jwtDecode(token);
//       if (decodedToken.exp * 1000 < Date.now()) {
//         alert('Session expired, please log in again');
//         navigate('/login');  // Redirect to login
//         return;
//       }
      
//       // Send the JWT token in the Authorization header
//       const response = await axios.post(`/events/${eventId}/addPlayer`, {}, {
//         headers: {
//           Authorization: `Bearer ${token}`,
//         },
//       });
      
//       if (response.status === 200) {
//         setEvents(events.map(event =>
//           event.id === eventId ? { ...event, joined: true } : event
//         ));
//       }
//     } catch (error) {
//       console.error('Failed to join event', error);
//       alert('An error occurred while trying to join the event. Please try again.');
//     }
//   };

//   const formatDate = (dateString) => {
//     return new Date(dateString).toLocaleDateString();
//   };

//   return (
//     <div className="tournament-events-page">
//       <h1>Events in Tournament</h1>
//       <div className="filter-by">
//         <label>Gender</label>
//         <select onChange={(e) => setFilters({ ...filters, gender: e.target.value })}>
//           <option value="all">All</option>
//           <option value="male">Male</option>
//           <option value="female">Female</option>
//         </select>

//         <label>Weapon</label>
//         <select onChange={(e) => setFilters({ ...filters, weapon: e.target.value })}>
//           <option value="all">All</option>
//           <option value="foil">Foil</option>
//           <option value="epee">Épée</option>
//           <option value="sabre">Sabre</option>
//         </select>
//       </div>

//       {loading ? (
//         <p>Loading events...</p>
//       ) : error ? (
//         <p>{error}</p>
//       ) : (
//         <div className="event-list">
//           {events.length > 0 ? (
//             events.map((event) => (
//               <div key={event.id} className="event-card">
//                 <h3>{event.name}</h3>
//                 <p>Start: {formatDate(event.startDate)}</p>
//                 <p>End: {formatDate(event.endDate)}</p>
//                 <p>Gender: {event.gender}</p>
//                 <p>Weapon: {event.weapon}</p>
//                 <button
//                   className={event.joined ? 'joined' : 'join'}
//                   disabled={event.joined}
//                   onClick={() => handleJoin(event.id)}
//                 >
//                   {event.joined ? 'Joined' : 'Join'}
//                 </button>
//               </div>
//             ))
//           ) : (
//             <p>No events found for this tournament.</p>
//           )}
//         </div>
//       )}
//     </div>
//   );
// };

// export default TournamentEventsPage;


// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import '../styles/TournamentEventsPage.css';

// const TournamentEventsPage = ({ match }) => {
//   const [events, setEvents] = useState([]);
//   const [filters, setFilters] = useState({
//     gender: 'all',
//     weapon: 'all',
//   });
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const tournamentId = match.params.tournamentId; // Extract tournament ID from the URL

//   useEffect(() => {
//     const fetchEvents = async () => {
//       try {
//         const response = await axios.get(`/tournaments/${tournamentId}/events`, { params: filters });
//         setEvents(response.data);
//       } catch (error) {
//         setError('Failed to load events');
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchEvents();
//   }, [filters]);

//   const handleJoin = async (eventId) => {
//     try {
//       const token = localStorage.getItem('token');  // Get the JWT token
//       if (!token) {
//         console.error('No token found, please log in');
//         return;
//       }
      
//       // Send the JWT token in the Authorization header
//       const response = await axios.post(`/events/${eventId}/addPlayer`, {}, {
//         headers: {
//           Authorization: `Bearer ${token}`,  // Include the token in the request headers
//         },
//       });
      
//       if (response.status === 200) {
//         setEvents(events.map(event =>
//           event.id === eventId ? { ...event, joined: true } : event
//         ));
//       }
//     } catch (error) {
//       console.error('Failed to join event', error);
//     }
//   };

//   return (
//     <div className="tournament-events-page">
//       <h1>Events in Tournament</h1>
//       <div className="filter-by">
//         <label>Gender</label>
//         <select onChange={(e) => setFilters({ ...filters, gender: e.target.value })}>
//           <option value="all">All</option>
//           <option value="male">Male</option>
//           <option value="female">Female</option>
//         </select>

//         <label>Weapon</label>
//         <select onChange={(e) => setFilters({ ...filters, weapon: e.target.value })}>
//           <option value="all">All</option>
//           <option value="foil">Foil</option>
//           <option value="epee">Épée</option>
//           <option value="sabre">Sabre</option>
//         </select>
//       </div>

//       {loading ? (
//         <p>Loading events...</p>
//       ) : error ? (
//         <p>{error}</p>
//       ) : (
//         <div className="event-list">
//           {events.map((event) => (
//             <div key={event.id} className="event-card">
//               <h3>Event {event.id}</h3>
//               <p>Start: {event.startDate}</p>
//               <p>End: {event.endDate}</p>
//               <p>Gender: {event.gender}</p>
//               <p>Weapon: {event.weapon}</p>
//               <button
//                 className={event.joined ? 'joined' : 'join'}
//                 disabled={event.joined}
//                 onClick={() => handleJoin(event.id)}
//               >
//                 {event.joined ? 'Joined' : 'Join'}
//               </button>
//             </div>
//           ))}
//         </div>
//       )}
//     </div>
//   );
// };

// export default TournamentEventsPage;

