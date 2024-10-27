import React, { useState, useEffect } from 'react';
import axios from 'axios';
import jwtDecode from 'jwt-decode';  // Optional: if you want to check token expiration
import { useNavigate } from 'react-router-dom'; // Optional: use for navigation
import '../styles/TournamentEventsPage.css';

const TournamentEventsPage = ({ match }) => {
  const [events, setEvents] = useState([]);
  const [filters, setFilters] = useState({
    gender: 'all',
    weapon: 'all',
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const tournamentId = match.params.tournamentId; // Extract tournament ID from the URL
  const navigate = useNavigate(); // For navigation if needed

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = await axios.get(`/tournaments/${tournamentId}/events`, { params: filters });
        setEvents(response.data);
      } catch (error) {
        setError('Failed to load events');
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, [filters, tournamentId]);

  const handleJoin = async (eventId) => {
    try {
      const token = localStorage.getItem('token');  // Get the JWT token
      if (!token) {
        console.error('No token found, please log in');
        return;
      }

      // Optionally, check token expiration before proceeding
      const decodedToken = jwtDecode(token);
      if (decodedToken.exp * 1000 < Date.now()) {
        alert('Session expired, please log in again');
        navigate('/login');  // Redirect to login
        return;
      }
      
      // Send the JWT token in the Authorization header
      const response = await axios.post(`/events/${eventId}/addPlayer`, {}, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      
      if (response.status === 200) {
        setEvents(events.map(event =>
          event.id === eventId ? { ...event, joined: true } : event
        ));
      }
    } catch (error) {
      console.error('Failed to join event', error);
      alert('An error occurred while trying to join the event. Please try again.');
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  return (
    <div className="tournament-events-page">
      <h1>Events in Tournament</h1>
      <div className="filter-by">
        <label>Gender</label>
        <select onChange={(e) => setFilters({ ...filters, gender: e.target.value })}>
          <option value="all">All</option>
          <option value="male">Male</option>
          <option value="female">Female</option>
        </select>

        <label>Weapon</label>
        <select onChange={(e) => setFilters({ ...filters, weapon: e.target.value })}>
          <option value="all">All</option>
          <option value="foil">Foil</option>
          <option value="epee">Épée</option>
          <option value="sabre">Sabre</option>
        </select>
      </div>

      {loading ? (
        <p>Loading events...</p>
      ) : error ? (
        <p>{error}</p>
      ) : (
        <div className="event-list">
          {events.length > 0 ? (
            events.map((event) => (
              <div key={event.id} className="event-card">
                <h3>{event.name}</h3>
                <p>Start: {formatDate(event.startDate)}</p>
                <p>End: {formatDate(event.endDate)}</p>
                <p>Gender: {event.gender}</p>
                <p>Weapon: {event.weapon}</p>
                <button
                  className={event.joined ? 'joined' : 'join'}
                  disabled={event.joined}
                  onClick={() => handleJoin(event.id)}
                >
                  {event.joined ? 'Joined' : 'Join'}
                </button>
              </div>
            ))
          ) : (
            <p>No events found for this tournament.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default TournamentEventsPage;


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

