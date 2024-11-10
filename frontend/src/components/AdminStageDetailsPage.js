import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/AdminStageDetailsPage.css'; // Import the relevant CSS

axios.defaults.baseURL = 'http://localhost:8080';

const AdminStageDetailsPage = () => {
  const { tournamentId, eventId, stageType, stageId } = useParams();
  const navigate = useNavigate();

  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMatches = async () => {
      try {
        const response = await axios.get(
          `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/matches`
        );
        setMatches(response.data);
      } catch (error) {
        setError('Error fetching matches.');
        console.error('Error fetching matches:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchMatches();
  }, [tournamentId, eventId, stageType, stageId]);

  const handleGenerateMatches = async () => {
    setGenerating(true);
    setError(null); // Clear previous error
    try {
      await axios.post(
        `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/matches`
      );
      const response = await axios.get(
        `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/matches`
      );
      setMatches(response.data);
    } catch (error) {
      setError('Error generating matches.');
      console.error('Error generating matches:', error);
    } finally {
      setGenerating(false);
    }
  };

  const renderMatches = () => (
    <ul className="match-list">
      {matches.map((match) => (
        <li key={match.id} onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/match/${match.id}`)}>
          Match {match.id}: @{match.player1.username} vs @{match.player2.username}
        </li>
      ))}
    </ul>
  );

  return (
    <div className={`${stageType}-details-page`}>
      {/* Breadcrumb Navigation */}
      <nav className="breadcrumb">
        <span onClick={() => navigate(`/admin/tournaments/${tournamentId}`)}>Tournaments</span> &gt;
        <span onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}`)}>Event</span> &gt;
        <span>{stageType === 'groupStage' ? 'GroupStage' : 'KnockoutStage'}</span>
      </nav>

      <h1>{stageType === 'groupStage' ? `Group Stage ${stageId}` : `Knockout Stage ${stageId}`}</h1>

      {/* Display loading, error, or content based on state */}
      {loading && <p>Loading matches...</p>}
      {error && <p className="error-message">{error}</p>}
      {!loading && matches.length === 0 && !error && (
        <button onClick={handleGenerateMatches} disabled={generating} className="generate-button">
          {generating ? 'Generating matches...' : 'Generate matches'}
        </button>
      )}
      {matches.length > 0 && renderMatches()}
      {!loading && matches.length === 0 && !error && <p>No matches available</p>}
    </div>
  );
};

export default AdminStageDetailsPage;


// import React, { useState, useEffect } from 'react';
// import { useParams, useNavigate } from 'react-router-dom';
// import axios from 'axios';
// import '../styles/AdminStageDetailsPage.css'; // Import the relevant CSS

// const AdminStageDetailsPage = () => {
//   const { tournamentId, eventId, stageType, stageId } = useParams(); // Use stageType and stageId from the URL
//   const navigate = useNavigate();
//   const [matches, setMatches] = useState([]);
  
//   useEffect(() => {
//     const fetchMatches = async () => {
//       try {
//         const response = await axios.get(
//           `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/matches`
//         );
//         setMatches(response.data);
//       } catch (error) {
//         console.error('Error fetching matches:', error);
//       }
//     };

//     fetchMatches();
//   }, [tournamentId, eventId, stageType, stageId]);

//   const handleGenerateMatches = async () => {
//     try {
//       await axios.post(
//         `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/matches`
//       );
//       const response = await axios.get(
//         `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/matches`
//       );
//       setMatches(response.data); // Refresh the match list after generation
//     } catch (error) {
//       console.error('Error generating matches:', error);
//     }
//   };

//   return (
//     <div className={`${stageType}-details-page`}>
//       <nav className="breadcrumb">
//         <span onClick={() => navigate(`/admin/tournaments/${tournamentId}`)}>Tournaments</span> &gt;
//         <span onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}`)}>Event</span> &gt;
//         <span>{stageType === 'groupStage' ? 'GroupStage' : 'KnockoutStage'}</span>
//       </nav>

//       <h1>{stageType === 'groupStage' ? `GroupStage ${stageId}` : `KnockoutStage ${stageId}`}</h1>

//       {matches.length === 0 && (
//         <button onClick={handleGenerateMatches}>Generate matches</button>
//       )}

//       {matches.length > 0 ? (
//         <ul>
//           {matches.map((match) => (
//             <li key={match.id} onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/match/${match.id}`)}>
//               Match {match.id}: {match.player1.name} vs {match.player2.name}
//             </li>
//           ))}
//         </ul>
//       ) : (
//         <p>No matches available</p>
//       )}
//     </div>
//   );
// };

// export default AdminStageDetailsPage;


// import React, { useState, useEffect } from 'react';
// import { useParams, useNavigate } from 'react-router-dom';
// import axios from 'axios';
// import './GroupStageDetailsPage.css';

// const GroupStageDetailsPage = () => {
//   const { tournamentId, eventId, groupStageId } = useParams(); // Get IDs from URL params
//   const [groupStage, setGroupStage] = useState(null);
//   const [matches, setMatches] = useState([]);
//   const navigate = useNavigate();

//   useEffect(() => {
//     // Fetch group stage details
//     const fetchGroupStageDetails = async () => {
//       try {
//         const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStageId}`);
//         setGroupStage(response.data);
//       } catch (error) {
//         console.error('Error fetching group stage details:', error);
//       }
//     };

//     // Fetch matches for group stage
//     const fetchMatches = async () => {
//       try {
//         const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStageId}/matches`);
//         setMatches(response.data);
//       } catch (error) {
//         console.error('Error fetching matches:', error);
//       }
//     };

//     fetchGroupStageDetails();
//     fetchMatches();
//   }, [tournamentId, eventId, groupStageId]);

//   const handleGenerateMatches = async () => {
//     try {
//       await axios.post(`/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStageId}/matches`);
//       // Fetch the updated matches after generating
//       const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStageId}/matches`);
//       setMatches(response.data);
//     } catch (error) {
//       console.error('Error generating matches:', error);
//     }
//   };

//   if (!groupStage) {
//     return <p>Loading group stage details...</p>;
//   }

//   return (
//     <div className="group-stage-details-page">
//       <nav className="breadcrumb">
//         <span onClick={() => navigate(`/admin/tournaments/${tournamentId}`)}>Tournaments</span> &gt;
//         <span onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}`)}>Event</span> &gt;
//         <span>GroupStage</span>
//       </nav>

//       <h1>GroupStage {groupStage.id}</h1>

//       <button onClick={handleGenerateMatches}>Generate matches</button>

//       {matches.length > 0 ? (
//         <ul>
//           {matches.map(match => (
//             <li key={match.id} onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStageId}/match/${match.id}`)}>
//               Match {match.id}
//             </li>
//           ))}
//         </ul>
//       ) : (
//         <p>No matches</p>
//       )}
//     </div>
//   );
// };

// export default GroupStageDetailsPage;

