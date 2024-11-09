import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { format } from 'date-fns';
import PropTypes from 'prop-types';
import '../styles/AdminEventsList.css';

axios.defaults.baseURL = 'http://localhost:8080';

const AdminEventsList = ({ tournamentId, onEditEvent, onDeleteEvent }) => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = await axios.get(`/tournaments/${tournamentId}/events`);
        setEvents(response.data);
      } catch (error) {
        setError('Failed to fetch events.');
        console.error('Error fetching events:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, [tournamentId]);

  const handleEventClick = (event) => {
    navigate(`/admin/tournaments/${tournamentId}/events/${event.id}`);
  };

  const handleDeleteClick = async (event, eventId) => {
    event.stopPropagation(); // Prevent the click from triggering the onClick for the event item
    try {
      await axios.delete(`/tournaments/${tournamentId}/events/${eventId}`);
      setEvents((prevEvents) => prevEvents.filter((e) => e.id !== eventId)); // Update state after deletion
    } catch (error) {
      console.error('Error deleting event:', error);
      setError('Failed to delete the event.');
    }
  };

  if (loading) {
    return <p className="loading">Loading events...</p>;
  }

  if (error) {
    return <p className="error">{error}</p>;
  }

  return (
    <div className="events-list">
      {events.length > 0 ? (
        events.map((event) => (
          <div key={event.id} className="event-item" onClick={() => handleEventClick(event)}>
            <h4>ID: {event.id}</h4>
            <p>Gender: {event.gender || 'N/A'}</p>
            <p>Weapon: {event.weapon || 'N/A'}</p>
            <p>
              {format(new Date(event.startDate), 'PPPpp')} - {format(new Date(event.endDate), 'PPPpp')}
            </p>
            <button
              onClick={(e) => { e.stopPropagation(); onEditEvent(event); }}
              className="edit-button"
            >
              Edit
            </button>
            <button
              onClick={(e) => handleDeleteClick(e, event.id)}
              className="delete-button"
            >
              Delete
            </button>
          </div>
        ))
      ) : (
        <p>No events found for this tournament.</p>
      )}
    </div>
  );
};

AdminEventsList.propTypes = {
  tournamentId: PropTypes.string.isRequired,
  onEditEvent: PropTypes.func.isRequired,
  onDeleteEvent: PropTypes.func, // Prop validation for delete functionality
};

export default AdminEventsList;


// import React, { useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';
// import axios from 'axios';
// import { format } from 'date-fns'; // Added for date formatting
// import PropTypes from 'prop-types'; // Added prop-types for validation
// import '../styles/AdminEventsList.css';

// const AdminEventsList = ({ tournamentId, onEditEvent }) => {
//   const [events, setEvents] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const navigate = useNavigate();

//   useEffect(() => {
//     const fetchEvents = async () => {
//       try {
//         const response = await axios.get(`/tournaments/${tournamentId}/events`);
//         setEvents(response.data);
//       } catch (error) {
//         setError('Failed to fetch events.');
//         console.error('Error fetching events:', error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchEvents();
//   }, [tournamentId]);

//   const handleEventClick = (event) => {
//     navigate(`/admin/tournaments/${tournamentId}/events/${event.id}`);
//   };

//   if (loading) {
//     return <p className="loading">Loading events...</p>;
//   }

//   if (error) {
//     return <p className="error">{error}</p>;
//   }

//   return (
//     <div className="events-list">
//       {events.length > 0 ? (
//         events.map(event => (
//           <div key={event.id} className="event-item" onClick={() => handleEventClick(event)}>
//             <h4>{event.name}</h4>
//             <p>{format(new Date(event.startDateTime), 'PPPpp')} - {format(new Date(event.endDateTime), 'PPPpp')}</p>
//             <button onClick={(e) => { e.stopPropagation(); onEditEvent(event); }}>Edit</button>
//           </div>
//         ))
//       ) : (
//         <p>No events found for this tournament.</p>
//       )}
//     </div>
//   );
// };

// AdminEventsList.propTypes = {
//   tournamentId: PropTypes.string.isRequired,
//   onEditEvent: PropTypes.func.isRequired,
// };

// export default AdminEventsList;


// import React, { useState, useEffect } from 'react'; // Added necessary imports
// import { useNavigate } from 'react-router-dom';
// import axios from 'axios'; // Import axios
// import '../styles/AdminEventsList.css';

// const AdminEventsList = ({ tournamentId, onEditEvent }) => {
//   const [events, setEvents] = useState([]);
//   const [loading, setLoading] = useState(true); // Added loading state
//   const [error, setError] = useState(null); // Added error state
//   const navigate = useNavigate();

//   useEffect(() => {
//     const fetchEvents = async () => {
//       try {
//         const response = await axios.get(`/tournaments/${tournamentId}/events`);
//         setEvents(response.data);
//       } catch (error) {
//         setError('Failed to fetch events.');
//         console.error('Error fetching events:', error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchEvents();
//   }, [tournamentId]);

//   const handleEventClick = (event) => {
//     navigate(`/admin/tournaments/${tournamentId}/events/${event.id}`);
//   };

//   if (loading) {
//     return <p>Loading events...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>; // Display error message if something goes wrong
//   }

//   return (
//     <div className="events-list">
//       {events.length > 0 ? (
//         events.map(event => (
//           <div key={event.id} className="event-item" onClick={() => handleEventClick(event)}>
//             <h4>{event.name}</h4>
//             <p>{event.startDateTime} - {event.endDateTime}</p>
//             <button onClick={(e) => { e.stopPropagation(); onEditEvent(event); }}>Edit</button>
//           </div>
//         ))
//       ) : (
//         <p>No events found for this tournament.</p>
//       )}
//     </div>
//   );
// };

// export default AdminEventsList;

// import React from 'react';
// import { useNavigate } from 'react-router-dom';
// import './EventsList.css';

// const EventsList = ({ tournamentId, onEditEvent }) => {
//   const [events, setEvents] = useState([]);
//   const navigate = useNavigate();

//   useEffect(() => {
//     const fetchEvents = async () => {
//       try {
//         const response = await axios.get(`/tournaments/${tournamentId}/events`);
//         setEvents(response.data);
//       } catch (error) {
//         console.error('Error fetching events:', error);
//       }
//     };

//     fetchEvents();
//   }, [tournamentId]);

//   const handleEventClick = (event) => {
//     navigate(`/admin/tournaments/${tournamentId}/events/${event.id}`);
//   };

//   return (
//     <div className="events-list">
//       {events.length > 0 ? (
//         events.map(event => (
//           <div key={event.id} className="event-item" onClick={() => handleEventClick(event)}>
//             <h4>{event.name}</h4>
//             <p>{event.startDateTime} - {event.endDateTime}</p>
//             <button onClick={(e) => { e.stopPropagation(); onEditEvent(event); }}>Edit</button>
//           </div>
//         ))
//       ) : (
//         <p>No events found for this tournament.</p>
//       )}
//     </div>
//   );
// };

// export default EventsList;
