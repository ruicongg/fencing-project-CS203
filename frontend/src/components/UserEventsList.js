import React, { useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import '../styles/UserEventsList.css';

axios.defaults.baseURL = 'http://localhost:8080';

const EventsList = ({ events, showWithdrawButton, onWithdraw }) => {
  const [withdrawing, setWithdrawing] = useState(null); // Track which event is being withdrawn
  const [error, setError] = useState(null); // Error state for handling withdraw errors
  const token = localStorage.getItem('token');

  const handleWithdraw = async (event) => {
    const eventId = event.id;
    const tournamentId = event.tournament.id;
    const username = jwtDecode(token)?.sub;

    setWithdrawing(eventId); // Set the event being withdrawn
    setError(null); // Reset error state before attempting withdrawal

    try {
      await axios.delete(
        `/tournaments/${tournamentId}/events/${eventId}/players`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      onWithdraw(eventId); // Inform the parent component that the event was withdrawn
    } catch (error) {
      console.error('Error withdrawing from event:', error);
      const errorMessage = error.response?.data?.message || 'Failed to withdraw from the event.';
      setError(errorMessage);
    } finally {
      setWithdrawing(null); // Reset after action completes
    }
  };

  const isUpcomingAndWithinRegistrationPeriod = (event) => {
    const now = new Date();
    const startDate = new Date(event.startDate);
    const registrationEndDate = new Date(event.tournament.registrationEndDate);

    registrationEndDate.setHours(23, 59, 59, 999);

    return now < startDate && now < registrationEndDate;
  };

  if (!events || events.length === 0) {
    return <p>No events found.</p>;
  }

  return (
    <div className="events-list">
      {error && <p className="error-message">{error}</p>}
      {events.map(event => (
        <div key={event.id} className="event-item">
          <h2>{event.tournament.name}</h2>
          <h4>Venue: {event.tournament.venue}</h4>
          <p>{new Date(event.startDate).toLocaleString()} - {new Date(event.endDate).toLocaleString()}</p>
          <p><strong>Weapon:</strong> {event.weapon}</p>

          {showWithdrawButton && isUpcomingAndWithinRegistrationPeriod(event) && (
            <button
              className="withdraw-button"
              onClick={() => handleWithdraw(event)} // Pass entire event object
              disabled={withdrawing === event.id} // Disable the button while withdrawing
              aria-live="polite"
              aria-disabled={withdrawing === event.id}
            >
              {withdrawing === event.id ? 'Withdrawing...' : 'Withdraw'}
            </button>
          )}
        </div>
      ))}
    </div>
  );
};

export default EventsList;

// const EventsList = ({ events, showWithdrawButton, onWithdraw }) => {
  
//   const [withdrawing, setWithdrawing] = useState(null); // Track which event is being withdrawn

//   const handleWithdraw = async (eventId) => {
//     setWithdrawing(eventId); // Set the event being withdrawn
//     try {
//       await axios.post(`/events/${eventId}/withdraw`);
//       onWithdraw(eventId); // Inform the parent component that the event was withdrawn
//     } catch (error) {
//       console.error('Error withdrawing from event:', error);
//       alert('Failed to withdraw from the event.');
//     } finally {
//       setWithdrawing(null); // Reset after action completes
//     }
//   };

//   // Helper function to check if the event is upcoming and within the registration period
//   const isUpcomingAndWithinRegistrationPeriod = (event) => {
//     const now = new Date();
//     const startDate = new Date(event.startDateTime);
//     const registrationEndDate = new Date(event.registrationEndDate);

//     return now < startDate && now < registrationEndDate;
//   };

//   if (!events || events.length === 0) {
//     return <p>No events found.</p>;
//   }

//   return (
//     <div className="events-list">
//       {events.map(event => (
//         <div key={event.id} className="event-item">
//           <h4>{event.name}</h4>
//           <p>{new Date(event.startDateTime).toLocaleString()} - {new Date(event.endDateTime).toLocaleString()}</p>
//           <p><strong>Weapon:</strong> {event.weapon}</p>

//           {/* Show "Withdraw" button for upcoming events within the registration period */}
//           {showWithdrawButton && isUpcomingAndWithinRegistrationPeriod(event) && (
//             <button
//               className="withdraw-button"
//               onClick={() => handleWithdraw(event.id)}
//               disabled={withdrawing === event.id} // Disable the button while withdrawing
//             >
//               {withdrawing === event.id ? 'Withdrawing...' : 'Withdraw'}
//             </button>
//           )}
//         </div>
//       ))}
//     </div>
//   );
// };

// export default EventsList;




// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import '../styles/UserEventsList.css';

// const EventsList = ({ tournamentId, showWithdrawButton }) => {
//   const [events, setEvents] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);

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

//   const handleWithdraw = async (eventId) => {
//     try {
//       await axios.post(`/events/${eventId}/withdraw`); // Ensure this matches your backend route
//       alert('You have withdrawn from the event.');
//       // Optionally, remove the withdrawn event from the list or refresh the data
//       setEvents(prevEvents => prevEvents.filter(event => event.id !== eventId));
//     } catch (error) {
//       console.error('Error withdrawing from event:', error);
//       alert('Failed to withdraw from the event.');
//     }
//   };

//   // Helper function to check if the event is upcoming and within the registration period
//   const isUpcomingAndWithinRegistrationPeriod = (event) => {
//     const now = new Date();
//     const startDate = new Date(event.startDateTime);
//     const registrationEndDate = new Date(event.registrationEndDate); // Ensure this field exists

//     // Return true if the current date is before both the event start date and registration end date
//     return now < startDate && now < registrationEndDate;
//   };

//   if (loading) {
//     return <p>Loading events...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>;
//   }

//   return (
//     <div className="events-list">
//       {events.length > 0 ? (
//         events.map(event => (
//           <div key={event.id} className="event-item">
//             <h4>{event.name}</h4>
//             <p>{new Date(event.startDateTime).toLocaleString()} - {new Date(event.endDateTime).toLocaleString()}</p>
//             <p><strong>Weapon:</strong> {event.weapon}</p> {/* Add weapon detail */}

//             {/* Conditionally show the "Withdraw" button for upcoming events within the registration period */}
//             {showWithdrawButton && isUpcomingAndWithinRegistrationPeriod(event) && (
//               <button
//                 className="withdraw-button"
//                 onClick={() => handleWithdraw(event.id)}
//               >
//                 Withdraw
//               </button>
//             )}
//           </div>
//         ))
//       ) : (
//         <p>No events found for this tournament.</p>
//       )}
//     </div>
//   );
// };

// export default EventsList;


// import React, { useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';
// import axios from 'axios';
// import './EventsList.css';

// const EventsList = ({ tournamentId, showWithdrawButton }) => {
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
//     navigate(`/tournaments/${tournamentId}/events/${event.id}`);
//   };

//   const handleWithdraw = async (eventId) => {
//     try {
//       await axios.post(`/events/${eventId}/withdraw`); // Make sure backend route handles withdrawal
//       alert('You have withdrawn from the event.');
//       // Optionally, refresh the event list to reflect the updated status
//     } catch (error) {
//       console.error('Error withdrawing from event:', error);
//       alert('Failed to withdraw from the event.');
//     }
//   };

//   // Helper function to check if the event is upcoming and within the registration period
//   const isUpcomingAndWithinRegistrationPeriod = (event) => {
//     const now = new Date();
//     const startDate = new Date(event.startDateTime);
//     const registrationEndDate = new Date(event.registrationEndDate); // Assume this field is available

//     // Return true if the current date is before both the event start date and registration end date
//     return now < startDate && now < registrationEndDate;
//   };

//   if (loading) {
//     return <p>Loading events...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>;
//   }

//   return (
//     <div className="events-list">
//       {events.length > 0 ? (
//         events.map(event => (
//           <div key={event.id} className="event-item" onClick={() => handleEventClick(event)}>
//             <h4>{event.name}</h4>
//             <p>{event.startDateTime} - {event.endDateTime}</p>

//             {/* Conditionally show the "Withdraw" button for upcoming events within the registration period */}
//             {showWithdrawButton && isUpcomingAndWithinRegistrationPeriod(event) && (
//               <button
//                 className="withdraw-button"
//                 onClick={(e) => {
//                   e.stopPropagation(); // Prevent navigating to event details on button click
//                   handleWithdraw(event.id);
//                 }}
//               >
//                 Withdraw
//               </button>
//             )}
//           </div>
//         ))
//       ) : (
//         <p>No events found for this tournament.</p>
//       )}
//     </div>
//   );
// };

// export default EventsList;

