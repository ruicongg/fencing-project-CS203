import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/UserEventsList.css';

const EventsList = ({ tournamentId, showWithdrawButton }) => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [withdrawing, setWithdrawing] = useState(null); // Track which event is being withdrawn

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

  const handleWithdraw = async (eventId) => {
    setWithdrawing(eventId); // Set the event being withdrawn
    try {
      await axios.post(`/events/${eventId}/withdraw`);
      // Dynamically update the event list without needing a page reload
      setEvents(prevEvents => prevEvents.filter(event => event.id !== eventId));
    } catch (error) {
      console.error('Error withdrawing from event:', error);
      alert('Failed to withdraw from the event.');
    } finally {
      setWithdrawing(null); // Reset after action completes
    }
  };

  // Helper function to check if the event is upcoming and within the registration period
  const isUpcomingAndWithinRegistrationPeriod = (event) => {
    const now = new Date();
    const startDate = new Date(event.startDateTime);
    const registrationEndDate = new Date(event.registrationEndDate);

    return now < startDate && now < registrationEndDate;
  };

  if (loading) {
    return <p>Loading events...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <div className="events-list">
      {events.length > 0 ? (
        events.map(event => (
          <div key={event.id} className="event-item">
            <h4>{event.name}</h4>
            <p>{new Date(event.startDateTime).toLocaleString()} - {new Date(event.endDateTime).toLocaleString()}</p>
            <p><strong>Weapon:</strong> {event.weapon}</p>

            {/* Show "Withdraw" button for upcoming events within the registration period */}
            {showWithdrawButton && isUpcomingAndWithinRegistrationPeriod(event) && (
              <button
                className="withdraw-button"
                onClick={() => handleWithdraw(event.id)}
                disabled={withdrawing === event.id} // Disable the button while withdrawing
              >
                {withdrawing === event.id ? 'Withdrawing...' : 'Withdraw'}
              </button>
            )}
          </div>
        ))
      ) : (
        <p>No events found for this tournament.</p>
      )}
    </div>
  );
};

export default EventsList;


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

