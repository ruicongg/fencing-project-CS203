import React, { useState, useEffect } from "react";
import axios from "axios";
import AdminTournamentCard from "./AdminTournamentCard";
import AdminEventsList from "./AdminEventsList";
import AdminEditTournament from "./AdminEditTournament";
import AdminCreateTournament from "./AdminCreateTournament";
import AdminEditEvent from "./AdminEditEvent";
import AdminCreateEvent from "./AdminCreateEvent";
import "../styles/AdminDashboard.css";

axios.defaults.baseURL = "http://localhost:8080";

const AdminDashboard = () => {
  const [activeTournaments, setActiveTournaments] = useState([]);
  const [completedTournaments, setCompletedTournaments] = useState([]);
  const [selectedTournament, setSelectedTournament] = useState(null);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [isEditingTournament, setIsEditingTournament] = useState(false);
  const [isCreatingTournament, setIsCreatingTournament] = useState(false);
  const [isEditingEvent, setIsEditingEvent] = useState(false);
  const [isCreatingEvent, setIsCreatingEvent] = useState(false);
  const [activeTab, setActiveTab] = useState("active");
  const [loading, setLoading] = useState(false);

  const handleError = (error, customMessage) => {
    console.error(customMessage, error);
  };

  // Fetch all tournaments and categorize them
  const fetchTournaments = async () => {
    try {
      setLoading(true);
      const response = await axios.get("/tournaments");
      const now = new Date();
      const active = response.data.filter(
        (t) => new Date(t.tournamentEndDate) > now
      );
      const completed = response.data.filter(
        (t) => new Date(t.tournamentEndDate) <= now
      );
      setActiveTournaments(active);
      setCompletedTournaments(completed);
    } catch (error) {
      handleError(error, "Error fetching tournaments");
    } finally {
      setLoading(false);
    }
  };

  // Fetch events for a specific tournament
  const fetchEventsForTournament = async (tournamentId) => {
    try {
      const response = await axios.get(`/tournaments/${tournamentId}/events`);
      setSelectedTournament((prev) => ({ ...prev, events: response.data }));
    } catch (error) {
      handleError(error, "Error fetching events for the tournament");
    }
  };

  useEffect(() => {
    fetchTournaments();
  }, []);

  const handleTabClick = (tab) => {
    setActiveTab(tab);
    setSelectedTournament(null); // Clear selected tournament on tab change
  };

  const handleDeleteTournament = async (tournamentId) => {
    try {
      await axios.delete(`/tournaments/${tournamentId}`);
      fetchTournaments();
    } catch (error) {
      handleError(error, "Error deleting tournament");
    }
  };

  const handleDeleteEvent = async (eventId) => {
    try {
      await axios.delete(
        `/tournaments/${selectedTournament.id}/events/${eventId}`
      );
      fetchEventsForTournament(selectedTournament.id); // Refresh events list after deletion
    } catch (error) {
      handleError(error, "Error deleting event");
    }
  };

  const handleSave = async (url, data, type) => {
    try {
      await axios.put(url, data);
      fetchTournaments();
      type === "tournament"
        ? setIsEditingTournament(false)
        : setIsEditingEvent(false);
    } catch (error) {
      handleError(error, `Error saving ${type}`);
    }
  };

  const handleAdd = async (url, data, type) => {
    try {
      await axios.post(url, data);

      if (type === "event" && selectedTournament) {
        await fetchEventsForTournament(selectedTournament.id); // Re-fetch events after adding a new event
      } else if (type === "tournament") {
        fetchTournaments();
      }

      type === "tournament"
        ? setIsCreatingTournament(false)
        : setIsCreatingEvent(false);
    } catch (error) {
      handleError(error, `Error adding ${type}`);
    }
  };

  return (
    <div className="admin-dashboard">
      <nav className="breadcrumb">
        <span>Tournaments</span>
      </nav>

      <h1>Admin Dashboard</h1>

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

      {activeTab === "active" && (
        <button
          className="new-tournament-button"
          onClick={() => setIsCreatingTournament(true)}
        >
          + New tournament
        </button>
      )}

      {loading && <p>Loading tournaments...</p>}

      <div className="tournament-cards">
        {!loading &&
          activeTab === "active" &&
          activeTournaments.length === 0 && <p>No active tournaments</p>}
        {!loading &&
          activeTab === "completed" &&
          completedTournaments.length === 0 && <p>No completed tournaments</p>}

        {(activeTab === "active"
          ? activeTournaments
          : completedTournaments
        ).map((tournament) => (
          <AdminTournamentCard
            key={tournament.id}
            tournament={tournament}
            onSelect={() => {
              setSelectedTournament(tournament);
              fetchEventsForTournament(tournament.id); // Fetch events when a tournament is selected
            }}
            onEdit={() => {
              setSelectedTournament(tournament);
              setIsEditingTournament(true);
            }}
            onDelete={() => handleDeleteTournament(tournament.id)}
          />
        ))}
      </div>

      {selectedTournament && (
        <div className="events-section">
          <h2>My events for {selectedTournament.name}</h2>
          {activeTab === "active" && (
            <button
              className="new-event-button"
              onClick={() => setIsCreatingEvent(true)}
            >
              + New event
            </button>
          )}
          <AdminEventsList
            events={selectedTournament.events || []} // Pass the events from selectedTournament
            tournamentId={selectedTournament?.id}
            onEditEvent={(event) => {
              setSelectedEvent(event);
              setIsEditingEvent(true);
            }}
            onDeleteEvent={handleDeleteEvent}
          />
        </div>
      )}

      {/* Modals */}
      {isEditingTournament && (
        <AdminEditTournament
          tournament={selectedTournament}
          onClose={() => setIsEditingTournament(false)}
          onSave={(updatedTournament) =>
            handleSave(
              `/tournaments/${updatedTournament.id}`,
              updatedTournament,
              "tournament"
            )
          }
        />
      )}

      {isCreatingTournament && (
        <AdminCreateTournament
          onClose={() => setIsCreatingTournament(false)}
          onAdd={(newTournament) =>
            handleAdd("/tournaments", newTournament, "tournament")
          }
        />
      )}

      {isEditingEvent && selectedEvent && (
        <AdminEditEvent
          event={selectedEvent}
          onClose={() => setIsEditingEvent(false)}
          onSave={(updatedEvent) =>
            handleSave(
              `/tournaments/${selectedTournament.id}/events/${updatedEvent.id}`,
              updatedEvent,
              "event"
            )
          }
        />
      )}

      {isCreatingEvent && (
        <AdminCreateEvent
          onClose={() => setIsCreatingEvent(false)}
          onAdd={(newEvent) =>
            handleAdd(
              `/tournaments/${selectedTournament.id}/events`,
              newEvent,
              "event"
            )
          }
        />
      )}
    </div>
  );
};

export default AdminDashboard;

// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import AdminTournamentCard from './AdminTournamentCard';
// import AdminEventsList from './AdminEventsList';
// import AdminEditTournament from './AdminEditTournament';
// import AdminCreateTournament from './AdminCreateTournament';
// import AdminEditEvent from './AdminEditEvent';
// import AdminCreateEvent from './AdminCreateEvent';
// import '../styles/AdminDashboard.css';

// const AdminDashboard = () => {
//   const [activeTournaments, setActiveTournaments] = useState([]);
//   const [completedTournaments, setCompletedTournaments] = useState([]);
//   const [selectedTournament, setSelectedTournament] = useState(null);
//   const [selectedEvent, setSelectedEvent] = useState(null); // Holds selected event for editing
//   const [isEditingTournament, setIsEditingTournament] = useState(false);
//   const [isCreatingTournament, setIsCreatingTournament] = useState(false);
//   const [isEditingEvent, setIsEditingEvent] = useState(false);
//   const [isCreatingEvent, setIsCreatingEvent] = useState(false);
//   const [activeTab, setActiveTab] = useState('active');
//   const [loading, setLoading] = useState(false); // New loading state

//   // Error handling function
//   const handleError = (error, customMessage) => {
//     console.error(customMessage, error);
//     // Implement additional error handling here (e.g., toast notifications)
//   };

//   // Fetch tournaments and categorize them
//   const fetchTournaments = async () => {
//     try {
//       setLoading(true);
//       const response = await axios.get('/tournaments');
//       const now = new Date();
//       const active = response.data.filter(t => new Date(t.endDate) > now);
//       const completed = response.data.filter(t => new Date(t.endDate) <= now);
//       setActiveTournaments(active);
//       setCompletedTournaments(completed);
//     } catch (error) {
//       handleError(error, 'Error fetching tournaments');
//     } finally {
//       setLoading(false);
//     }
//   };

//   useEffect(() => {
//     fetchTournaments();
//   }, []);

//   const handleTabClick = (tab) => setActiveTab(tab);

//   // Unified function to handle both tournament save and event save
//   const handleSave = async (url, data, type) => {
//     try {
//       await axios.put(url, data);
//       await fetchTournaments(); // Refresh tournaments after save
//       type === 'tournament' ? setIsEditingTournament(false) : setIsEditingEvent(false);
//     } catch (error) {
//       handleError(error, `Error saving ${type}`);
//     }
//   };

//   // Unified function to handle both tournament add and event add
//   const handleAdd = async (url, data, type) => {
//     try {
//       await axios.post(url, data);
//       await fetchTournaments(); // Refresh tournaments after adding
//       type === 'tournament' ? setIsCreatingTournament(false) : setIsCreatingEvent(false);
//     } catch (error) {
//       handleError(error, `Error adding ${type}`);
//     }
//   };

//   return (
//     <div className="admin-dashboard">
//       {/* Breadcrumb Navigation */}
//       <nav className="breadcrumb">
//         <span>Tournaments</span> {/* Static breadcrumb as we are already on the Tournaments page */}
//       </nav>

//       <h1>Admin Dashboard</h1>

//       {/* Tabs for Active and Completed Tournaments */}
//       <div className="tabs">
//         <button onClick={() => handleTabClick('active')} className={activeTab === 'active' ? 'active' : ''}>
//           Active
//         </button>
//         <button onClick={() => handleTabClick('completed')} className={activeTab === 'completed' ? 'active' : ''}>
//           Completed
//         </button>
//       </div>

//       <button className="new-tournament-button" onClick={() => setIsCreatingTournament(true)}>
//         + New tournament
//       </button>

//       {/* Show loading state */}
//       {loading && <p>Loading tournaments...</p>}

//       <div className="tournament-cards">
//         {!loading && activeTab === 'active' && activeTournaments.length === 0 && <p>No active tournaments</p>}
//         {!loading && activeTab === 'completed' && completedTournaments.length === 0 && <p>No completed tournaments</p>}

//         {(activeTab === 'active' ? activeTournaments : completedTournaments).map(tournament => (
//           <AdminTournamentCard
//             key={tournament.id}
//             tournament={tournament}
//             onSelect={() => setSelectedTournament(tournament)}  // Selects the tournament to display events
//             onEdit={() => {
//               setSelectedTournament(tournament);
//               setIsEditingTournament(true);
//             }}
//           />
//         ))}
//       </div>

//       {selectedTournament && (
//         <div className="events-section">
//           <h2>My events for {selectedTournament.name}</h2>
//           <button className="new-event-button" onClick={() => setIsCreatingEvent(true)}>+ New event</button>
//           <AdminEventsList
//             tournamentId={selectedTournament.id}
//             onEditEvent={(event) => {
//               setSelectedEvent(event);
//               setIsEditingEvent(true);
//             }} // Pass the handler to EventsList
//           />
//         </div>
//       )}

//       {/* Modals */}
//       {isEditingTournament && (
//         <AdminEditTournament
//           tournament={selectedTournament}
//           onClose={() => setIsEditingTournament(false)}
//           onSave={(updatedTournament) => handleSave(`/tournaments/${updatedTournament.id}`, updatedTournament, 'tournament')}
//         />
//       )}

//       {isCreatingTournament && (
//         <AdminCreateTournament
//           onClose={() => setIsCreatingTournament(false)}
//           onAdd={(newTournament) => handleAdd('/tournaments', newTournament, 'tournament')}
//         />
//       )}

//       {isEditingEvent && selectedEvent && (
//         <AdminEditEvent
//           event={selectedEvent}
//           onClose={() => setIsEditingEvent(false)}
//           onSave={(updatedEvent) => handleSave(`/tournaments/${selectedTournament.id}/events/${updatedEvent.id}`, updatedEvent, 'event')}
//         />
//       )}

//       {isCreatingEvent && (
//         <AdminCreateEvent
//           onClose={() => setIsCreatingEvent(false)}
//           onAdd={(newEvent) => handleAdd(`/tournaments/${selectedTournament.id}/events`, newEvent, 'event')}
//         />
//       )}
//     </div>
//   );
// };

// export default AdminDashboard;

// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import TournamentCard from './TournamentCard';
// import EventsList from './EventsList';
// import EditTournamentModal from './EditTournamentModal';
// import CreateTournamentModal from './CreateTournamentModal';
// import EditEventModal from './EditEventModal';
// import CreateEventModal from './CreateEventModal';
// import { Link } from 'react-router-dom';
// import './AdminDashboard.css';

// const AdminDashboard = () => {
//   const [activeTournaments, setActiveTournaments] = useState([]);
//   const [completedTournaments, setCompletedTournaments] = useState([]);
//   const [selectedTournament, setSelectedTournament] = useState(null);
//   const [selectedEvent, setSelectedEvent] = useState(null); // Holds selected event for editing
//   const [isEditingTournament, setIsEditingTournament] = useState(false);
//   const [isCreatingTournament, setIsCreatingTournament] = useState(false);
//   const [isEditingEvent, setIsEditingEvent] = useState(false);
//   const [isCreatingEvent, setIsCreatingEvent] = useState(false);
//   const [activeTab, setActiveTab] = useState('active');
//   const [loading, setLoading] = useState(false); // New loading state

//   // Error handling function
//   const handleError = (error, customMessage) => {
//     console.error(customMessage, error);
//     // Implement additional error handling here (e.g., toast notifications)
//   };

//   // Fetch tournaments and categorize them
//   const fetchTournaments = async () => {
//     try {
//       setLoading(true);
//       const response = await axios.get('/tournaments');
//       const now = new Date();
//       const active = response.data.filter(t => new Date(t.endDate) > now);
//       const completed = response.data.filter(t => new Date(t.endDate) <= now);
//       setActiveTournaments(active);
//       setCompletedTournaments(completed);
//     } catch (error) {
//       handleError(error, 'Error fetching tournaments');
//     } finally {
//       setLoading(false);
//     }
//   };

//   useEffect(() => {
//     fetchTournaments();
//   }, []);

//   const handleTabClick = (tab) => setActiveTab(tab);

//   // Unified function to handle both tournament save and event save
//   const handleSave = async (url, data, type) => {
//     try {
//       await axios.put(url, data);
//       await fetchTournaments(); // Refresh tournaments after save
//       type === 'tournament' ? setIsEditingTournament(false) : setIsEditingEvent(false);
//     } catch (error) {
//       handleError(error, `Error saving ${type}`);
//     }
//   };

//   // Unified function to handle both tournament add and event add
//   const handleAdd = async (url, data, type) => {
//     try {
//       await axios.post(url, data);
//       await fetchTournaments(); // Refresh tournaments after adding
//       type === 'tournament' ? setIsCreatingTournament(false) : setIsCreatingEvent(false);
//     } catch (error) {
//       handleError(error, `Error adding ${type}`);
//     }
//   };

//   return (
//     <div className="admin-dashboard">
//       {/* Breadcrumb Navigation */}
//       <nav className="breadcrumb">
//         <span>Tournaments</span> {/* Static breadcrumb as we are already on the Tournaments page */}
//       </nav>

//       <h1>Admin Dashboard</h1>

//       {/* Tabs for Active and Completed Tournaments */}
//       <div className="tabs">
//         <button onClick={() => handleTabClick('active')} className={activeTab === 'active' ? 'active' : ''}>
//           Active
//         </button>
//         <button onClick={() => handleTabClick('completed')} className={activeTab === 'completed' ? 'active' : ''}>
//           Completed
//         </button>
//       </div>

//       <button className="new-tournament-button" onClick={() => setIsCreatingTournament(true)}>
//         + New tournament
//       </button>

//       {/* Show loading state */}
//       {loading && <p>Loading tournaments...</p>}

//       <div className="tournament-cards">
//         {!loading && activeTab === 'active' && activeTournaments.length === 0 && <p>No active tournaments</p>}
//         {!loading && activeTab === 'completed' && completedTournaments.length === 0 && <p>No completed tournaments</p>}

//         {(activeTab === 'active' ? activeTournaments : completedTournaments).map(tournament => (
//           <TournamentCard
//             key={tournament.id}
//             tournament={tournament}
//             onEdit={() => {
//               setSelectedTournament(tournament);
//               setIsEditingTournament(true);
//             }}
//           />
//         ))}
//       </div>

//       {selectedTournament && (
//         <div className="events-section">
//           <h2>My events for {selectedTournament.name}</h2>
//           <button className="new-event-button" onClick={() => setIsCreatingEvent(true)}>+ New event</button>
//           <EventsList
//             tournamentId={selectedTournament.id}
//             onEditEvent={(event) => {
//               setSelectedEvent(event);
//               setIsEditingEvent(true);
//             }} // Pass the handler to EventsList
//           />
//         </div>
//       )}

//       {/* Modals */}
//       {isEditingTournament && (
//         <EditTournamentModal
//           tournament={selectedTournament}
//           onClose={() => setIsEditingTournament(false)}
//           onSave={(updatedTournament) => handleSave(`/tournaments/${updatedTournament.id}`, updatedTournament, 'tournament')}
//         />
//       )}

//       {isCreatingTournament && (
//         <CreateTournamentModal
//           onClose={() => setIsCreatingTournament(false)}
//           onAdd={(newTournament) => handleAdd('/tournaments', newTournament, 'tournament')}
//         />
//       )}

//       {isEditingEvent && selectedEvent && (
//         <EditEventModal
//           event={selectedEvent}
//           onClose={() => setIsEditingEvent(false)}
//           onSave={(updatedEvent) => handleSave(`/tournaments/${selectedTournament.id}/events/${updatedEvent.id}`, updatedEvent, 'event')}
//         />
//       )}

//       {isCreatingEvent && (
//         <CreateEventModal
//           onClose={() => setIsCreatingEvent(false)}
//           onAdd={(newEvent) => handleAdd(`/tournaments/${selectedTournament.id}/events`, newEvent, 'event')}
//         />
//       )}
//     </div>
//   );
// };

// export default AdminDashboard;

// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import TournamentCard from './TournamentCard';
// import EventsList from './EventsList';
// import EditTournamentModal from './EditTournamentModal';
// import CreateTournamentModal from './CreateTournamentModal';
// import EditEventModal from './EditEventModal';
// import CreateEventModal from './CreateEventModal';
// import { Link } from 'react-router-dom';
// import './AdminDashboard.css';

// const AdminDashboard = () => {
//   const [activeTournaments, setActiveTournaments] = useState([]);
//   const [completedTournaments, setCompletedTournaments] = useState([]);
//   const [selectedTournament, setSelectedTournament] = useState(null);
//   const [selectedEvent, setSelectedEvent] = useState(null); // Holds selected event for editing
//   const [isEditingTournament, setIsEditingTournament] = useState(false);
//   const [isCreatingTournament, setIsCreatingTournament] = useState(false);
//   const [isEditingEvent, setIsEditingEvent] = useState(false);
//   const [isCreatingEvent, setIsCreatingEvent] = useState(false);
//   const [activeTab, setActiveTab] = useState('active');

//   useEffect(() => {
//     const fetchTournaments = async () => {
//       try {
//         const response = await axios.get('/tournaments');
//         const now = new Date();
//         const active = response.data.filter(t => new Date(t.endDate) > now);
//         const completed = response.data.filter(t => new Date(t.endDate) <= now);
//         setActiveTournaments(active);
//         setCompletedTournaments(completed);
//       } catch (error) {
//         console.error('Error fetching tournaments:', error);
//       }
//     };

//     fetchTournaments();
//   }, []);

//   const handleTabClick = (tab) => {
//     setActiveTab(tab);
//   };

//   const handleEditTournament = (tournament) => {
//     setSelectedTournament(tournament);
//     setIsEditingTournament(true);
//   };

//   const handleEditEvent = (event) => {
//     setSelectedEvent(event); // Set the selected event for editing
//     setIsEditingEvent(true);
//   };

//   const handleCreateEvent = () => {
//     setIsCreatingEvent(true);
//   };

//   const handleCreateTournament = () => {
//     setIsCreatingTournament(true);
//   };

//   const handleCloseModal = () => {
//     setIsEditingTournament(false);
//     setIsCreatingTournament(false);
//     setIsEditingEvent(false); // Close event modal
//     setIsCreatingEvent(false);
//     setSelectedTournament(null);
//     setSelectedEvent(null);
//   };

//   const handleSaveTournament = async (updatedTournament) => {
//     try {
//       await axios.put(`/tournaments/${updatedTournament.id}`, updatedTournament);
//       setIsEditingTournament(false);
//       const response = await axios.get('/tournaments');
//       const now = new Date();
//       setActiveTournaments(response.data.filter(t => new Date(t.endDate) > now));
//       setCompletedTournaments(response.data.filter(t => new Date(t.endDate) <= now));
//     } catch (error) {
//       console.error('Error saving tournament:', error);
//     }
//   };

//   const handleSaveEvent = async (updatedEvent) => {
//     try {
//       await axios.put(`/tournaments/${selectedTournament.id}/events/${updatedEvent.id}`, updatedEvent);
//       setIsEditingEvent(false);
//       const response = await axios.get(`/tournaments/${selectedTournament.id}/events`);
//       setSelectedTournament({ ...selectedTournament, events: response.data });
//     } catch (error) {
//       console.error('Error saving event:', error);
//     }
//   };

//   const handleAddEvent = async (newEvent) => {
//     try {
//       await axios.post(`/tournaments/${selectedTournament.id}/events`, newEvent);
//       setIsCreatingEvent(false);
//       const response = await axios.get(`/tournaments/${selectedTournament.id}/events`);
//       setSelectedTournament({ ...selectedTournament, events: response.data });
//     } catch (error) {
//       console.error('Error adding event:', error);
//     }
//   };

//   const handleAddTournament = async (newTournament) => {
//     try {
//       await axios.post('/tournaments', newTournament);
//       setIsCreatingTournament(false);
//       const response = await axios.get('/tournaments');
//       const now = new Date();
//       setActiveTournaments(response.data.filter(t => new Date(t.endDate) > now));
//       setCompletedTournaments(response.data.filter(t => new Date(t.endDate) <= now));
//     } catch (error) {
//       console.error('Error adding tournament:', error);
//     }
//   };

//   return (
//     <div className="admin-dashboard">
//       {/* Breadcrumb Navigation */}
//       <nav className="breadcrumb">
//         <span>Tournaments</span> {/* Static breadcrumb as we are already on the Tournaments page */}
//       </nav>

//       <h1>Admin Dashboard</h1>
//       <div className="tabs">
//         <button onClick={() => handleTabClick('active')} className={activeTab === 'active' ? 'active' : ''}>
//           Active
//         </button>
//         <button onClick={() => handleTabClick('completed')} className={activeTab === 'completed' ? 'active' : ''}>
//           Completed
//         </button>
//       </div>

//       <button className="new-tournament-button" onClick={handleCreateTournament}>
//         + New tournament
//       </button>

//       <div className="tournament-cards">
//         {activeTab === 'active' && activeTournaments.length === 0 && <p>No active tournaments</p>}
//         {activeTab === 'completed' && completedTournaments.length === 0 && <p>No completed tournaments</p>}

//         {(activeTab === 'active' ? activeTournaments : completedTournaments).map(tournament => (
//           <TournamentCard
//             key={tournament.id}
//             tournament={tournament}
//             onEdit={() => handleEditTournament(tournament)}
//           />
//         ))}
//       </div>

//       {selectedTournament && (
//         <div className="events-section">
//           <h2>My events for {selectedTournament.name}</h2>
//           <button className="new-event-button" onClick={handleCreateEvent}>+ New event</button>
//           <EventsList
//             tournamentId={selectedTournament.id}
//             onEditEvent={handleEditEvent} // Pass the handler to EventsList
//           />
//         </div>
//       )}

//       {isEditingTournament && (
//         <EditTournamentModal
//           tournament={selectedTournament}
//           onClose={handleCloseModal}
//           onSave={handleSaveTournament}
//         />
//       )}

//       {isCreatingTournament && (
//         <CreateTournamentModal
//           onClose={handleCloseModal}
//           onAdd={handleAddTournament}
//         />
//       )}

//       {isEditingEvent && selectedEvent && (
//         <EditEventModal
//           event={selectedEvent}
//           onClose={handleCloseModal}
//           onSave={handleSaveEvent} // Handle saving edited event
//         />
//       )}

//       {isCreatingEvent && (
//         <CreateEventModal
//           onClose={handleCloseModal}
//           onAdd={handleAddEvent}
//         />
//       )}
//     </div>
//   );
// };

// export default AdminDashboard;
