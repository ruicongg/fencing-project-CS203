import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, useParams, useNavigate } from 'react-router-dom';
import ViewPlayersModal from './ViewPlayers';
import '../styles/AdminEventDetailsPage.css';

axios.defaults.baseURL = 'http://localhost:8080';

const AdminEventDetailsPage = () => {
  const { tournamentId, eventId } = useParams();
  useEffect(() => {
    console.log('Params:', { tournamentId, eventId });
  }, [tournamentId, eventId]);
  const navigate = useNavigate();

  const [event, setEvent] = useState(null);
  const [groupStages, setGroupStages] = useState([]); // Separate state for group stages
  const [knockoutStages, setKnockoutStages] = useState([]); // Separate state for knockout stages
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isPlayersModalOpen, setIsPlayersModalOpen] = useState(false);

  useEffect(() => {
    const fetchEventData = async () => {
      try {
        const eventResponse = await axios.get(`/tournaments/${tournamentId}/events/${eventId}`);
        setEvent(eventResponse.data);
        // Fetch group stages and knockout stages separately
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

  // Function to fetch group stages
  const fetchGroupStages = async () => {
    try {
      const groupStagesResponse = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStages`);
      setGroupStages(groupStagesResponse.data);
    } catch (error) {
      console.error('Error fetching group stages:', error);
    }
  };

  // Function to fetch knockout stages
  const fetchKnockoutStages = async () => {
    try {
      const knockoutStagesResponse = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
      setKnockoutStages(knockoutStagesResponse.data);
    } catch (error) {
      console.error('Error fetching knockout stages:', error);
    }
  };

  const handleGenerateKnockoutStages = async () => {
    try {
      await axios.post(`/tournaments/${tournamentId}/events/${eventId}/knockoutStage`);
      await fetchKnockoutStages(); // Fetch the updated knockout stages after creation
    } catch (error) {
      setError('Error generating knockout stages.');
      console.error('Error generating knockout stages:', error);
    }
  };

  const handleGenerateGroupStages = async () => {
    try {
      await axios.post(`/tournaments/${tournamentId}/events/${eventId}/groupStage`);
      await fetchGroupStages(); // Fetch the updated group stages after creation
    } catch (error) {
      setError('Error generating group stages.');
      console.error('Error generating group stages:', error);
    }
  };

  const openPlayersModal = () => setIsPlayersModalOpen(true);
  const closePlayersModal = () => setIsPlayersModalOpen(false);

  if (loading) {
    return <p>Loading event details...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  if (!event) {
    return <p>Event not found.</p>;
  }

  return (
    <div className="event-details-page">
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
              </li>
            ))}
          </ul>
        ) : (
          <div>
            <p>No Knockout Stages have been generated yet. Click below to create them.</p>
          </div>
        )}
        <button onClick={handleGenerateKnockoutStages}>+ New Knockout Stage</button>
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



