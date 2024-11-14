import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, useParams, useNavigate } from 'react-router-dom';
import ViewPlayersModal from './ViewPlayers';
import '../styles/shared/index.css';

axios.defaults.baseURL = 'http://localhost:8080';

const AdminEventDetailsPage = () => {
  const { tournamentId, eventId } = useParams();
  const navigate = useNavigate();

  const [event, setEvent] = useState(null);
  const [groupStages, setGroupStages] = useState([]);
  const [knockoutStages, setKnockoutStages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isPlayersModalOpen, setIsPlayersModalOpen] = useState(false);
  const [successfulGeneration, setSuccessfulGeneration] = useState({
    groupStage: null,
    knockoutStage: null,
  });

  useEffect(() => {
    const fetchEventData = async () => {
      try {
        const eventResponse = await axios.get(`/tournaments/${tournamentId}/events/${eventId}`);
        setEvent(eventResponse.data);
        await fetchGroupStages();
        await fetchKnockoutStages();
      } catch (error) {
        setError('Failed to load event details. Please try again.');
        console.error('Error fetching event details:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchEventData();
  }, [tournamentId, eventId]);

  const fetchGroupStages = async () => {
    try {
      const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStages`);
      setGroupStages(response.data);
    } catch (error) {
      setError('Error fetching group stages: ' + (error.response?.data?.error || 'Unknown error'));
      console.error('Error fetching group stages: ' + (error.response?.data?.error || 'Unknown error'));
    }
  };

  const fetchKnockoutStages = async () => {
    try {
      const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
      setKnockoutStages(response.data);
    } catch (error) {

      setError('Error fetching knockout stages: ' + (error.response?.data?.error || 'Unknown error'));
      console.error('Error fetching knockout stages: ' + (error.response?.data?.error || 'Unknown error'));
    }
  };

  const handleGenerateGroupStageMatches = async (groupStageId) => {
    try {
      await axios.post(`/tournaments/${tournamentId}/events/${eventId}/groupStage/matches`);
      setGroupStages(prevStages =>
        prevStages.map(stage =>
          stage.id === groupStageId ? { ...stage, matches: [{}] } : stage
        )
      );
      setSuccessfulGeneration({ ...successfulGeneration, groupStage: groupStageId });
      setError(null);  // Clear any existing error on success
    } catch (error) {
      setError('Error generating matches for group stage. ' + (error.response?.data?.error || 'Unknown error'));
      console.error('Error generating group stage matches:', error);
    }
  };

  const handleGenerateKnockoutStageMatches = async (knockoutStageId) => {
    try {
      await axios.post(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage/${knockoutStageId}/matches`);
      setKnockoutStages(prevStages =>
        prevStages.map(stage =>
          stage.id === knockoutStageId ? { ...stage, matches: [{}] } : stage
        )
      );
      setSuccessfulGeneration({ ...successfulGeneration, knockoutStage: knockoutStageId });
    } catch (error) {
      setError('Error generating matches for knockout stage. ' + (error.response?.data?.error || 'Unknown error'));
      console.error('Error generating knockout stage matches:', error);
    }
  };

  const handleGenerateGroupStages = async () => {
    try {
      await axios.post(`/tournaments/${tournamentId}/events/${eventId}/groupStage`);
      await fetchGroupStages();
      setError(null);  // Clear any existing error on success
    } catch (error) {
      setError('Error generating group stages: ' + (error.response?.data?.error || 'Unknown error'));
      console.error('Error generating group stages:', error);
    }
  };

  const handleGenerateKnockoutStages = async () => {
    try {
      await axios.post(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
      await fetchKnockoutStages();
    } catch (error) {
      setError('Error generating knockout stage. ' + (error.response?.data?.error || 'Unknown error'));
      console.error('Error generating knockout stage:', error);
    }
  };

  const handleDeleteKnockoutStage = async (knockoutStageId) => {
    try {
      await axios.delete(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage/${knockoutStageId}`);
      await fetchKnockoutStages();
    } catch (error) {
      setError('Error deleting knockout stage. ' + (error.response?.data?.error || 'Unknown error'));
      console.error('Error deleting knockout stage:', error);
    }
  };

  const openPlayersModal = () => setIsPlayersModalOpen(true);
  const closePlayersModal = () => setIsPlayersModalOpen(false);

  if (loading) return <p>Loading event details...</p>;
  // if (!event) return <p>Event not found.</p>;

  return (
    <div className="dashboard">
      {/* Error Message Container */}
      {error && (
        <div className="error-container">
          <svg className="error-icon" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
          </svg>
          <span className="error-message">{error}</span>
        </div>
      )}
  
      {/* Breadcrumb Navigation */}
      <div className="breadcrumb">
        <Link to="/admin/dashboard">Tournaments</Link>
        <span className="separator">/</span>
        <a className="active">Event</a>
      </div>
  
      {/* Page Title */}
      <h1 className="dashboard-title">Event Details</h1>
  
      {/* Event Details Section */}
      <div className="section-container">
        
          <p className="section-title"><strong>Event ID:</strong> {event.id}</p>
          <div className="section-content">
            <p><strong>Date:</strong> {new Date(event.startDate).toLocaleString()} to {new Date(event.endDate).toLocaleString()}</p>
            <p><strong>Gender:</strong> {event.gender}</p>
            <p><strong>Weapon:</strong> {event.weapon}</p>
          </div>
          <button onClick={openPlayersModal} className="view-button">View Players</button>
        
      {/* Group Stages Section */}
      <div className="section-container">
        <h2 className="section-title">Group Stages</h2>
        <div className="section-content">
          {groupStages.length > 0 ? (
            <ul>
              {groupStages.map(groupStage => (
                <li 
                key={groupStage.id}
                onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStage.id}`)}
                className="list-item"
                >
                  <div className="item-content">
                    Group Stage ID: {groupStage.id}  
                      {groupStage.matches && groupStage.matches.length === 0 ? (
                        <button
                          onClick={() => handleGenerateGroupStageMatches(groupStage.id)}
                          className="add-button"
                        >
                          Generate Matches
                        </button>
                      ) : successfulGeneration.groupStage === groupStage.id ? (
                        <p>Matches creation successful.</p>
                      ) : null}
                    </div>
                  
                </li>
              ))}
            </ul>
          ) : (
            <div>
              <p>No Group Stages have been generated yet. Click below to create them.</p>
              <button onClick={handleGenerateGroupStages} className="add-button">Generate Group Stages</button>
            </div>
          )}
        </div>
      </div>
  
      {/* Knockout Stages Section */}
      <div className="section-container">
        <h2 className="section-title">Knockout Stages</h2>
        
        <div className="section-content">
        <div>
            <button 
            onClick={handleGenerateKnockoutStages} 
            className="add-button"
            >
              + New Knockout Stage
            </button>
        </div>
          {knockoutStages.length > 0 ? (
            <ul>
              {knockoutStages.map(knockoutStage => (
                <li 
                key={knockoutStage.id} 
                onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/knockoutStage/${knockoutStage.id}`)}
                className="list-item"
                >
                  <div className="item-content">
                    Knockout Stage ID: {knockoutStage.id} 
                    <button
                      onClick={(e) => {
                          e.stopPropagation(); // Prevent navigation when deleting
                          handleDeleteKnockoutStage(knockoutStage.id);
                        }}
                          className="delete-button"
                    >
                      Delete
                    </button>
                    
                      {knockoutStage.matches && knockoutStage.matches.length === 0 ? (
                        <button
                          onClick={() => handleGenerateKnockoutStageMatches(knockoutStage.id)}
                          className="add-button"
                        >
                          Generate Matches
                        </button>
                      ) : successfulGeneration.knockoutStage === knockoutStage.id ? (
                        <p>Matches creation successful.</p>
                      ) : null}
                    </div>
                  
                </li>
              ))}
            </ul>
          ) : (
              <p>No Knockout Stages have been generated yet. Click below to create them.</p>
          )}
        </div>
      </div>
  
      {/* Players Modal */}
      {isPlayersModalOpen && (
        <ViewPlayersModal onClose={closePlayersModal} eventId={eventId} tournamentId={tournamentId} />
      )}
    </div>
    </div>
  );
  
};

export default AdminEventDetailsPage;



//   const [event, setEvent] = useState(null);
//   const [groupStages, setGroupStages] = useState([]);
//   const [knockoutStages, setKnockoutStages] = useState([]);
//   const [isPlayersModalOpen, setIsPlayersModalOpen] = useState(false);
//   const [loading, setLoading] = useState(true); // To handle loading state
//   const [error, setError] = useState(null); // Error state for failed API requests

//   useEffect(() => {
//     const fetchEventData = async () => {
//       try {
//         const [eventResponse, groupStagesResponse, knockoutStagesResponse] = await Promise.all([
//           axios.get(`/tournaments/${tournamentId}/events/${eventId}`),
//           // axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStage`),
//           // axios.get(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`)
//         ]);

//         setEvent(eventResponse.data);
//         setGroupStages(groupStagesResponse.data);
//         setKnockoutStages(knockoutStagesResponse.data);
//       } catch (error) {
//         setError('Failed to load event details. Please try again.');
//         console.error('Error fetching event details:', error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchEventData();
//   }, [tournamentId, eventId]);

//   const handleGenerateKnockoutStages = async () => {
//     try {
//       await axios.post(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
//       const { data } = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
//       setKnockoutStages(data); // Update knockout stages after generating
//     } catch (error) {
//       setError('Error generating knockout stages.');
//       console.error('Error generating knockout stages:', error);
//     }
//   };

//   const handleGenerateGroupStages = async () => {
//     try {
//       await axios.post(`/tournaments/${tournamentId}/events/${eventId}/groupStage`);
//       const { data } = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStage`);
//       setGroupStages(data); // Update group stages after generating
//     } catch (error) {
//       setError('Error generating group stages.');
//       console.error('Error generating group stages:', error);
//     }
//   };

//   const openPlayersModal = () => setIsPlayersModalOpen(true);
//   const closePlayersModal = () => setIsPlayersModalOpen(false);

//   if (loading) {
//     return <p>Loading event details...</p>;
//   }

//   if (error) {
//     return <p>{error}</p>; // Show error message if API calls fail
//   }

//   if (!event) {
//     return <p>Event not found.</p>;
//   }

//   return (
//     <div className="event-details-page">
//       {/* Breadcrumb Navigation */}
//       <nav className="breadcrumb">
//         <Link to="/admin/dashboard">Tournaments</Link> &gt; <span>Event</span>
//       </nav>

//       {/* Event Details */}
//       <h1>Event Details</h1>
//       <div className="event-info">
//         <p><strong>Event Name:</strong> {event.name}</p>
//         <p><strong>Date:</strong> {new Date(event.startDate).toLocaleString()} to {new Date(event.endDate).toLocaleString()}</p> {/* Formatted dates */}
//         <p><strong>Gender:</strong> {event.gender}</p>
//         <p><strong>Weapon:</strong> {event.weapon}</p>
//         <button onClick={openPlayersModal}>View Players</button>
//       </div>

//       {/* Group Stages Tab */}
//       <div className="group-stages-section">
//         <h2>Group Stages</h2>
//         {groupStages.length > 0 ? (
//           <ul>
//             {groupStages.map(groupStage => (
//               <li key={groupStage.id}>
//                 Group Stage {groupStage.id}
//                 <button onClick={() => navigate(`/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStage.id}`)}>
//                   View
//                 </button>
//               </li>
//             ))}
//           </ul>
//         ) : (
//           <div>
//             <p>No Group Stages have been generated yet. Click below to create them.</p>
//             <button onClick={handleGenerateGroupStages}>Generate Group Stages</button>
//           </div>
//         )}
//       </div>

//       {/* Knockout Stages Tab */}
//       <div className="knockout-stages-section">
//         <h2>Knockout Stages</h2>
//         {knockoutStages.length > 0 ? (
//           <ul>
//             {knockoutStages.map(knockoutStage => (
//               <li key={knockoutStage.id}>
//                 Knockout Stage {knockoutStage.id}
//                 <button onClick={() => navigate(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage/${knockoutStage.id}`)}>
//                   View
//                 </button>
//               </li>
//             ))}
//           </ul>
//         ) : (
//           <div>
//             <p>No Knockout Stages have been generated yet. Click below to create them.</p>
//             <button onClick={handleGenerateKnockoutStages}>+ New Knockout Stage</button>
//           </div>
//         )}
//       </div>

//       {/* View Players Modal */}
//       {isPlayersModalOpen && (
//         <ViewPlayersModal onClose={closePlayersModal} eventId={eventId} tournamentId={tournamentId} />
//       )}
//     </div>
//   );
// };

// export default AdminEventDetailsPage;


// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import { Link, useParams, useNavigate } from 'react-router-dom';
// import ViewPlayersModal from './ViewPlayers';
// import '../styles/AdminEventDetailsPage.css';

// const AdminEventDetailsPage = () => {
//   const { tournamentId, eventId } = useParams(); 
//   const navigate = useNavigate();

//   const [event, setEvent] = useState(null);
//   const [groupStages, setGroupStages] = useState([]);
//   const [knockoutStages, setKnockoutStages] = useState([]);
//   const [isPlayersModalOpen, setIsPlayersModalOpen] = useState(false);
//   const [loading, setLoading] = useState(true); // To handle loading state

//   useEffect(() => {
//     // Fetch all data in parallel
//     const fetchEventData = async () => {
//       try {
//         const [eventResponse, groupStagesResponse, knockoutStagesResponse] = await Promise.all([
//           axios.get(`/tournaments/${tournamentId}/events/${eventId}`),
//           axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStage`),
//           axios.get(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`)
//         ]);

//         setEvent(eventResponse.data);
//         setGroupStages(groupStagesResponse.data);
//         setKnockoutStages(knockoutStagesResponse.data);
//       } catch (error) {
//         console.error('Error fetching event details:', error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchEventData();
//   }, [tournamentId, eventId]);

//   const handleGenerateKnockoutStages = async () => {
//     try {
//       await axios.post(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
//       const { data } = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
//       setKnockoutStages(data); // Update knockout stages after generating
//     } catch (error) {
//       console.error('Error generating knockout stages:', error);
//     }
//   };

//   const handleGenerateGroupStages = async () => {
//     try {
//       await axios.post(`/tournaments/${tournamentId}/events/${eventId}/groupStage`);
//       const { data } = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStage`);
//       setGroupStages(data); // Update group stages after generating
//     } catch (error) {
//       console.error('Error generating group stages:', error);
//     }
//   };

//   const openPlayersModal = () => {
//     setIsPlayersModalOpen(true);
//   };

//   const closePlayersModal = () => {
//     setIsPlayersModalOpen(false);
//   };

//   if (loading) {
//     return <p>Loading event details...</p>;
//   }

//   if (!event) {
//     return <p>Event not found.</p>;
//   }

//   return (
//     <div className="event-details-page">
//       {/* Breadcrumb Navigation */}
//       <nav className="breadcrumb">
//         <Link to="/admin/dashboard">Tournaments</Link> &gt; <span>Event</span>
//       </nav>

//       {/* Event Details */}
//       <h1>Event Details</h1>
//       <div className="event-info">
//         <p><strong>Event Name:</strong> {event.name}</p>
//         <p><strong>Date:</strong> {event.startDateTime} to {event.endDateTime}</p>
//         <p><strong>Gender:</strong> {event.gender}</p>
//         <p><strong>Weapon:</strong> {event.weapon}</p>
//         <button onClick={openPlayersModal}>View Players</button>
//       </div>

//       {/* Group Stages Tab */}
//       <div className="group-stages-section">
//         <h2>Group Stages</h2>
//         {groupStages.length > 0 ? (
//           <ul>
//             {groupStages.map(groupStage => (
//               <li key={groupStage.id}>
//                 Group Stage {groupStage.id}
//                 <button onClick={() => navigate(`/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStage.id}`)}>
//                   View
//                 </button>
//               </li>
//             ))}
//           </ul>
//         ) : (
//           <div>
//             <p>No GroupStages</p>
//             <button onClick={handleGenerateGroupStages}>Generate GroupStages</button>
//           </div>
//         )}
//       </div>

//       {/* Knockout Stages Tab */}
//       <div className="knockout-stages-section">
//         <h2>Knockout Stages</h2>
//         {knockoutStages.length > 0 ? (
//           <ul>
//             {knockoutStages.map(knockoutStage => (
//               <li key={knockoutStage.id}>
//                 Knockout Stage {knockoutStage.id}
//                 <button onClick={() => navigate(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage/${knockoutStage.id}`)}>
//                   View
//                 </button>
//               </li>
//             ))}
//           </ul>
//         ) : (
//           <div>
//             <p>No KnockoutStages</p>
//             <button onClick={handleGenerateKnockoutStages}>+ New KnockoutStage</button>
//           </div>
//         )}
//       </div>

//       {/* View Players Modal */}
//       {isPlayersModalOpen && (
//         <ViewPlayersModal onClose={closePlayersModal} eventId={eventId} tournamentId={tournamentId} />
//       )}
//     </div>
//   );
// };

// export default AdminEventDetailsPage;



