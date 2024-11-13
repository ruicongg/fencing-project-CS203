import React, { useState, useEffect } from 'react';
import { useLocation, useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/shared/index.css';
import '../styles/AdminStageDetailsPage.css'; // Import the relevant CSS

axios.defaults.baseURL = 'http://localhost:8080';

const AdminStageDetailsPage = () => {
  const { tournamentId, eventId, stageId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  // Determine the stage type (knockoutStage or groupStage) based on the URL
  const stageType = location.pathname.includes("knockoutStage") ? "knockoutStage" : "groupStage";
  console.log('Params:', { tournamentId, eventId, stageType, stageId });

  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState(null);

  // Fetch matches for the stage
  useEffect(() => {
    const fetchMatches = async () => {
      setLoading(true);  // Start loading
      setError(null);    // Clear any previous errors

      try {
        // Define the endpoint based on the stageType
        const endpoint = stageType === 'groupStage'
          ? `/tournaments/${tournamentId}/events/${eventId}/groupStage/${stageId}/matches`
          : `/tournaments/${tournamentId}/events/${eventId}/knockoutStage/${stageId}/matches`;
        
        console.log('Fetching from endpoint:', endpoint);
        
        const response = await axios.get(endpoint);
        setMatches(response.data);
      } catch (error) {
        setError('Error fetching matches.');
        console.error('Error fetching matches:', error);
      } finally {
        setLoading(false); // End loading
      }
    };

    fetchMatches();
  }, [tournamentId, eventId, stageType, stageId]);

  // Generate matches if there are none
  const handleGenerateMatches = async () => {
    setGenerating(true);
    setError(null);

    try {
      // Use the correct endpoint for generating matches based on stageType
      const endpoint = stageType === 'groupStage'
        ? `/tournaments/${tournamentId}/events/${eventId}/groupStage/matches`
        : `/tournaments/${tournamentId}/events/${eventId}/knockoutStage/${stageId}/matches`;

      console.log('Creating matches from endpoint:', endpoint);
      
      // Generate matches
      await axios.post(endpoint);
      
      // Fetch the newly generated matches
      const fetchEndpoint = `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/matches`;
      const response = await axios.get(fetchEndpoint);
      setMatches(response.data);
    } catch (error) {
      setError('Error generating matches.');
      console.error('Error generating matches:', error);
    } finally {
      setGenerating(false);
    }
  };

  const renderMatches = () => (
    <ul className="section-container">
      {matches.map((match) => (
        <div key={match.id} onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/match/${match.id}`)}>
          <div className="modal">
            <div className="list-item">
            <div className="item-title"> Match ID: {match.id}</div>
            </div>
            <p><strong>Player 1:</strong> @{match.player1.username}</p>
            <p><strong>Player 2:</strong> @{match.player2.username}</p>
          </div>
          
        </div>
      ))}
    </ul>
  );

  return (
    <div className="dashboard">
      {/* Error Message Container */}
      {error && (
        <div className="error-container">
          <svg className="error-icon" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
          </svg>
          <span className="error-message">{error}</span>
          <button className="close-error-button" onClick={() => setError(null)}>✕</button>
        </div>
      )}
      
      {/* Breadcrumb Navigation */}
      <nav className="breadcrumb">
        <span onClick={() => navigate(`/admin/tournaments/${tournamentId}`)}>Tournaments </span> 
        <span className="separator">/</span>
        <span onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}`)}>Event </span> 
        <span className="separator">/</span>
        <a className="active">{stageType === 'groupStage' ? 'GroupStage' : 'KnockoutStage'}</a>
      </nav>

      <h1 className="dashboard-title">{stageType === 'groupStage' ? `Group Stage ID: ${stageId}` : `Knockout Stage ID: ${stageId}`}</h1>
      
        {/* Display loading, error, or content based on state */}
        {loading && <p>Loading matches...</p>}
        {error && <p className="error-message">{error}</p>}
        {!loading && matches.length === 0 && !error && (
          <button onClick={handleGenerateMatches} disabled={generating} className="add-button">
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

