import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, useParams, useNavigate } from 'react-router-dom';
import ViewPlayersModal from './ViewPlayers';
import '../styles/AdminEventDetailsPage.css';

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
      if (error.response?.data?.error) {
        const errorMsg = error.response.data.error;
        if (errorMsg.includes('not found')) {
          setError('Event or tournament no longer exists.');
        } else if (errorMsg.includes('permission')) {
          setError('You do not have permission to view group stages.');
        } else {
          setError(`Failed to load group stages: ${errorMsg}`);
        }
      } else {
        setError('Network error while loading group stages. Please try again.');
      }
    }
  };

  const fetchKnockoutStages = async () => {
    try {
      const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
      setKnockoutStages(response.data);
    } catch (error) {
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
      if (error.response?.data?.error) {
        const errorMsg = error.response.data.error;
        if (errorMsg.includes('players')) {
          setError('Cannot generate group matches: Not enough players registered (minimum 4 per group required).');
        } else if (errorMsg.includes('already generated')) {
          setError('Matches have already been generated for this group stage.');
        } else if (errorMsg.includes('groups')) {
          setError('Please generate group stages before generating matches.');
        } else {
          setError(`Failed to generate group matches: ${errorMsg}`);
        }
      } else {
        setError('Network error while generating group matches. Please try again.');
      }
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
      if (error.response?.data?.error) {
        const errorMsg = error.response.data.error;
        if (errorMsg.includes('group stage')) {
          setError('Cannot generate knockout matches: Group stage must be completed first.');
        } else if (errorMsg.includes('players')) {
          setError('Not enough qualified players to generate knockout matches.');
        } else if (errorMsg.includes('already')) {
          setError('Knockout matches have already been generated for this stage.');
        } else {
          setError(`Failed to generate knockout matches: ${errorMsg}`);
        }
      } else {
        setError('Network error while generating knockout matches. Please try again.');
      }
    }
  };

  const handleGenerateGroupStages = async () => {
    try {
      await axios.post(`/tournaments/${tournamentId}/events/${eventId}/groupStage`);
      await fetchGroupStages();
      setError(null);  // Clear any existing error on success
    } catch (error) {
      if (error.response?.data?.error) {
        const errorMsg = error.response.data.error;
        if (errorMsg.includes('players')) {
          setError('Cannot generate groups: Insufficient number of registered players.');
        } else if (errorMsg.includes('already')) {
          setError('Group stages have already been generated for this event.');
        } else {
          setError(`Failed to generate group stages: ${errorMsg}`);
        }
      } else {
        setError('Network error while generating group stages. Please try again.');
      }
    }
  };

  const handleGenerateKnockoutStages = async () => {
    try {
      await axios.post(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
      await fetchKnockoutStages();
    } catch (error) {
      if (error.response?.data?.error) {
        const errorMsg = error.response.data.error;
        if (errorMsg.includes('group')) {
          setError('Cannot generate knockout stages: Group stages must be completed first.');
        } else if (errorMsg.includes('already')) {
          setError('Knockout stages have already been generated for this event.');
        } else {
          setError(`Failed to generate knockout stages: ${errorMsg}`);
        }
      } else {
        setError('Network error while generating knockout stages. Please try again.');
      }
    }
  };

  const openPlayersModal = () => setIsPlayersModalOpen(true);
  const closePlayersModal = () => setIsPlayersModalOpen(false);

  if (loading) return <p>Loading event details...</p>;
  if (error) return <p>{error}</p>;
  if (!event) return <p>Event not found.</p>;

  return (
    <div className="event-details-page">
      {error && (
        <div className="error-container">
          <svg className="error-icon" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
          <span className="error-message">{error}</span>
          <button className="close-error-button" onClick={() => setError(null)}>✕</button>
        </div>
      )}

      <nav className="breadcrumb">
        <Link to="/admin/dashboard">Tournaments</Link> &gt; <span>Event</span>
      </nav>

      <h1>Event Details</h1>
      <div className="event-info">
        <p><strong>Event Name:</strong> {event.name}</p>
        <p><strong>Date:</strong> {new Date(event.startDate).toLocaleString()} to {new Date(event.endDate).toLocaleString()}</p>
        <p><strong>Gender:</strong> {event.gender}</p>
        <p><strong>Weapon:</strong> {event.weapon}</p>
        <button onClick={openPlayersModal}>View Players</button>
      </div>

      <div className="group-stages-section">
        <h2>Group Stages</h2>
        {groupStages.length > 0 ? (
          <ul>
            {groupStages.map(groupStage => (
              <li key={groupStage.id}>
                Group Stage ID: {groupStage.id}
                <button onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStage.id}`)}>
                  View
                </button>
                {groupStage.matches && groupStage.matches.length === 0 ? (
                  <button onClick={() => handleGenerateGroupStageMatches(groupStage.id)}>
                    Generate Matches
                  </button>
                ) : successfulGeneration.groupStage === groupStage.id ? (
                  <p>Matches creation successful.</p>
                ) : null}
              </li>
            ))}
          </ul>
        ) : (
          <div>
            <p>No Group Stages have been generated yet. Click below to create them.</p>
            <button onClick={handleGenerateGroupStages}>Generate Group Stages</button>
          </div>

        )}
      </div>

      <div className="knockout-stages-section">
        <h2>Knockout Stages</h2>
        {knockoutStages.length > 0 ? (
          <ul>
            {knockoutStages.map(knockoutStage => (
              <li key={knockoutStage.id}>
                Knockout Stage ID: {knockoutStage.id}
                <button onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/knockoutStage/${knockoutStage.id}`)}>
                  View
                </button>
                {knockoutStage.matches && knockoutStage.matches.length === 0 ? (
                  <button onClick={() => handleGenerateKnockoutStageMatches(knockoutStage.id)}>
                    Generate Matches
                  </button>
                ) : successfulGeneration.knockoutStage === knockoutStage.id ? (
                  <p>Matches creation successful.</p>
                ) : null}
              </li>
            ))}
          </ul>
        ) : (
          <div>
            <p>No Knockout Stages have been generated yet. Click below to create them.</p>
            <button onClick={handleGenerateKnockoutStages}>+ New Knockout Stage</button>
          </div>
        )}
      </div>

      {isPlayersModalOpen && (
        <ViewPlayersModal onClose={closePlayersModal} eventId={eventId} tournamentId={tournamentId} />
      )}
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



