import React, { useState, useEffect } from "react";
import axios from "axios";
import EventsList from "./UserEventsList";
import { jwtDecode } from "jwt-decode";
import "../styles/UserEventsPage.css";

axios.defaults.baseURL = "https://parry-hub.com";

const EventsPage = () => {
  const [activeEvents, setActiveEvents] = useState([]);
  const [completedEvents, setCompletedEvents] = useState([]);
  const [activeTab, setActiveTab] = useState("active");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        // Decode username from JWT token
        const decodedToken = jwtDecode(token);
        const username = decodedToken.sub;

        // Fetch playerRanks by username
        const playerResponse = await axios.get(
          `/player/${username}/playerRanks`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        // Extract unique events from playerRanks
        const playerRanks = playerResponse.data || [];
        const uniqueEvents = [
          ...new Map(
            playerRanks.map((rank) => [rank.event.id, rank.event])
          ).values(),
        ];

        // Separate active and completed events based on end date
        const now = new Date();
        const active = uniqueEvents.filter(
          (event) => new Date(event.endDate) > now
        );
        const completed = uniqueEvents.filter(
          (event) => new Date(event.endDate) <= now
        );

        setActiveEvents(active);
        setCompletedEvents(completed);
      } catch (error) {
        setError("Failed to fetch events.");
        console.error("Error fetching events:", error);
      } finally {
        setLoading(false);
      }
    };

    if (token) {
      fetchEvents();
    }
  }, [token]);

  const handleWithdraw = (eventId) => {
    setActiveEvents((prevActiveEvents) =>
      prevActiveEvents.filter((event) => event.id !== eventId)
    );
  };

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  if (loading) {
    return <p>Loading events...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <div className="dashboard">
      {/* Error Message Container */}
      {error && (
        <div className="error-container">
          <svg className="error-icon" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
          </svg>
          <span className="error-message">{error}</span>
          <button className="close-error-button" onClick={() => setError(null)}>
            ✕
          </button>
        </div>
      )}

      <h1 className="dashboard-title">My Events</h1>

      <div className="tabs">
        <button
          onClick={() => handleTabClick("active")}
          className={activeTab === "active" ? "active" : ""}
        >
          Active
        </button>
        <button
          onClick={() => handleTabClick("completed")}
          className={activeTab === "completed" ? "active" : ""}
        >
          Completed
        </button>
      </div>

      {activeTab === "active" ? (
        <EventsList
          events={activeEvents}
          showWithdrawButton={true}
          onWithdraw={handleWithdraw}
        />
      ) : (
        <EventsList events={completedEvents} showWithdrawButton={false} />
      )}
    </div>
  );
};

export default EventsPage;

// const EventsPage = () => {
//   const [activeEvents, setActiveEvents] = useState([]);
//   const [completedEvents, setCompletedEvents] = useState([]);
//   const [activeTab, setActiveTab] = useState('active');
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const token = localStorage.getItem('token');

//   useEffect(() => {
//     const fetchUserIdAndEvents = async () => {
//       try {
//         // // Fetch user ID
//         // const userIdResponse = await axios.get('/users/id', {
//         //   headers: { Authorization: `Bearer ${token}` },
//         // });
//         // const userId = userIdResponse.data;
//         // console.log(userId)
//         // Fetch player data using the user ID
//         const playerResponse = await axios.get(`/player/${userId}`, {
//           headers: { Authorization: `Bearer ${token}` },
//         });
//         console.log(playerResponse)

//         // Extract unique events from playerRanks
//         const playerRanks = playerResponse.data?.playerRanks || [];
//         const uniqueEvents = [
//           ...new Map(playerRanks.map((rank) => [rank.event.id, rank.event])).values()
//         ];
//         console.log(uniqueEvents);

//         // Separate active and completed events based on end date
//         const now = new Date();
//         const active = uniqueEvents.filter(event => new Date(event.endDate) > now);
//         const completed = uniqueEvents.filter(event => new Date(event.endDate) <= now);

//         setActiveEvents(active);
//         setCompletedEvents(completed);
//       } catch (error) {
//         setError('Failed to fetch events.');
//         console.error('Error fetching events:', error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     if (token) {
//       fetchUserIdAndEvents();
//     }
//   }, [token]);

//   const handleWithdraw = (eventId) => {
//     // Filter out the withdrawn event from the active list
//     setActiveEvents((prevActiveEvents) =>
//       prevActiveEvents.filter((event) => event.id !== eventId)
//     );
//   };

//   const handleTabClick = (tab) => {
//     setActiveTab(tab);
//   };

//   if (loading) {
//     return <p>Loading events...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>;
//   }

//   return (
//     <div className="my-events-page">
//       <h1>My Events</h1>

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

//       {/* Events List */}
//       {activeTab === 'active' ? (
//         <EventsList events={activeEvents} showWithdrawButton={true} onWithdraw={handleWithdraw} />
//       ) : (
//         <EventsList events={completedEvents} showWithdrawButton={false} />
//       )}
//     </div>
//   );
// };

// export default EventsPage;

// const EventsPage = () => {
//     const [activeEvents, setActiveEvents] = useState([]);
//     const [completedEvents, setCompletedEvents] = useState([]);
//     const [activeTab, setActiveTab] = useState('active');
//     const [loading, setLoading] = useState(true);
//     const [error, setError] = useState(null);
//     const userId = JSON.parse(localStorage.getItem('user'))?.id;
//     const token = localStorage.getItem('token');

//     useEffect(() => {
//       const fetchMyEvents = async () => {
//         try {
//           const response = await axios.get('/matches', {
//             headers: { Authorization: `Bearer ${token}` },
//           });

//           // Filter matches where the user is either player1 or player2
//           const userMatches = response.data.filter(
//             (match) => match.player1.id === userId || match.player2.id === userId
//           );

//           // Extract unique events from user matches
//           const uniqueEvents = [
//             ...new Map(userMatches.map((match) => [match.event.id, match.event])).values(),
//           ];

//           // Separate active and completed events based on end date
//           const now = new Date();
//           const active = uniqueEvents.filter(event => new Date(event.endDate) > now);
//           const completed = uniqueEvents.filter(event => new Date(event.endDate) <= now);

//           setActiveEvents(active);
//           setCompletedEvents(completed);
//         } catch (error) {
//           setError('Failed to fetch events.');
//           console.error('Error fetching events:', error);
//         } finally {
//           setLoading(false);
//         }
//       };

//       if (userId && token) {
//         fetchMyEvents();
//       }
//     }, [userId, token]);

//     const handleWithdraw = (eventId) => {
//       // Filter out the withdrawn event from the active list
//       setActiveEvents((prevActiveEvents) =>
//         prevActiveEvents.filter((event) => event.id !== eventId)
//       );
//     };

//     const handleTabClick = (tab) => {
//       setActiveTab(tab);
//     };

//     if (loading) {
//       return <p>Loading events...</p>;
//     }

//     if (error) {
//       return <p>{error}</p>;
//     }

//     return (
//       <div className="my-events-page">
//         <h1>My Events</h1>

//         {/* Tabs */}
//         <div className="tabs">
//           <button
//             onClick={() => handleTabClick('active')}
//             className={activeTab === 'active' ? 'active' : ''}
//           >
//             Active
//           </button>
//           <button
//             onClick={() => handleTabClick('completed')}
//             className={activeTab === 'completed' ? 'active' : ''}
//           >
//             Completed
//           </button>
//         </div>

//         {/* Events List */}
//         {activeTab === 'active' ? (
//           <EventsList events={activeEvents} showWithdrawButton={true} onWithdraw={handleWithdraw} />
//         ) : (
//           <EventsList events={completedEvents} showWithdrawButton={false} />
//         )}
//       </div>
//     );
//   };

//   export default EventsPage;

// const EventsPage = () => {
//   const [activeEvents, setActiveEvents] = useState([]);
//   const [completedEvents, setCompletedEvents] = useState([]);
//   const [activeTab, setActiveTab] = useState('active');
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);

//   useEffect(() => {
//     const fetchMyEvents = async () => {
//       try {
//         const response = await axios.get('/tournaments'); // Make sure this endpoint is implemented
//         const now = new Date();

//         const active = response.data.filter(event => new Date(event.endDate) > now);
//         const completed = response.data.filter(event => new Date(event.endDate) <= now);

//         setActiveEvents(active);
//         setCompletedEvents(completed);
//       } catch (error) {
//         setError('Failed to fetch events.');
//         console.error('Error fetching events:', error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchMyEvents();
//   }, []);

//   const handleWithdraw = (eventId) => {
//     // Filter out the withdrawn event from the active list
//     setActiveEvents((prevActiveEvents) =>
//       prevActiveEvents.filter((event) => event.id !== eventId)
//     );
//   };

//   const handleTabClick = (tab) => {
//     setActiveTab(tab);
//   };

//   if (loading) {
//     return <p>Loading events...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>;
//   }

//   return (
//     <div className="my-events-page">
//       <h1>My Events</h1>

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

//       {/* Events List */}
//       {activeTab === 'active' ? (
//         <EventsList events={activeEvents} showWithdrawButton={true} onWithdraw={handleWithdraw} />
//       ) : (
//         <EventsList events={completedEvents} showWithdrawButton={false} />
//       )}
//     </div>
//   );
// };

// export default EventsPage;
