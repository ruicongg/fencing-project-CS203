import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/AdminMatchDetailsPage.css'; // Import the relevant CSS

const AdminMatchDetailsPage = () => {
  const { tournamentId, eventId, stageType, stageId, matchId } = useParams(); // Using stageType from URL
  const navigate = useNavigate();
  const [match, setMatch] = useState(null);
  const [player1Score, setPlayer1Score] = useState('');
  const [player2Score, setPlayer2Score] = useState('');
  const [loading, setLoading] = useState(true); // Loading state for fetching match details
  const [saving, setSaving] = useState(false); // State for handling saving process
  const [error, setError] = useState(null); // Error state for handling any issues

  useEffect(() => {
    const fetchMatchDetails = async () => {
      try {
        const response = await axios.get(
          `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/match/${matchId}`
        );
        setMatch(response.data);
        setPlayer1Score(response.data.player1Score || '');
        setPlayer2Score(response.data.player2Score || '');
      } catch (error) {
        setError('Error fetching match details.');
        console.error('Error fetching match details:', error);
      } finally {
        setLoading(false); // Set loading to false once data is fetched
      }
    };

    fetchMatchDetails();
  }, [tournamentId, eventId, stageType, stageId, matchId]);

  const handleSave = async () => {
    // Add form validation to ensure scores are valid
    if (!player1Score || !player2Score || isNaN(player1Score) || isNaN(player2Score)) {
      setError('Please enter valid scores for both players.');
      return;
    }

    setSaving(true); // Set saving state to true
    setError(null); // Clear any previous errors
    try {
      await axios.put(
        `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/match/${matchId}`,
        {
          player1Score: Number(player1Score), // Ensure scores are numbers
          player2Score: Number(player2Score)
        }
      );
      alert('Scores saved successfully!');
    } catch (error) {
      console.error('Error saving scores:', error);
      setError('Error saving scores. Please try again.');
    } finally {
      setSaving(false); // Reset saving state after operation
    }
  };

  if (loading) {
    return <p>Loading match details...</p>;
  }

  if (error) {
    return <p className="error-message">{error}</p>; // Display error message if any
  }

  return (
    <div className="match-details-page">
      <nav className="breadcrumb">
        <span onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}`)}>
          {stageType === 'groupStage' ? 'GroupStage' : 'KnockoutStage'}
        </span> &gt;
        <span>Match {matchId}</span>
      </nav>

      <h1>Match {match.id}</h1>
      <p>Player 1: {match.player1.username}</p> {/* Ensure player name or username */}
      <p>Player 2: {match.player2.username}</p>

      <div className="score-input">
        <label>
          Player 1 Score:
          <input
            type="number"
            value={player1Score}
            onChange={(e) => setPlayer1Score(e.target.value)}
          />
        </label>

        <label>
          Player 2 Score:
          <input
            type="number"
            value={player2Score}
            onChange={(e) => setPlayer2Score(e.target.value)}
          />
        </label>
      </div>

      <button onClick={handleSave} disabled={saving || !player1Score || !player2Score}>
        {saving ? 'Saving...' : 'Save'}
      </button>

      {error && <p className="error-message">{error}</p>}
    </div>
  );
};

export default AdminMatchDetailsPage;


// import React, { useState, useEffect } from 'react';
// import { useParams, useNavigate } from 'react-router-dom';
// import axios from 'axios';
// import '../styles/AdminMatchDetailsPage.css'; // Import the relevant CSS

// const AdminMatchDetailsPage = () => {
//   const { tournamentId, eventId, stageType, stageId, matchId } = useParams(); // Using stageType from URL
//   const navigate = useNavigate();
//   const [match, setMatch] = useState(null);
//   const [player1Score, setPlayer1Score] = useState('');
//   const [player2Score, setPlayer2Score] = useState('');

//   useEffect(() => {
//     const fetchMatchDetails = async () => {
//       try {
//         const response = await axios.get(
//           `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/match/${matchId}`
//         );
//         setMatch(response.data);
//         setPlayer1Score(response.data.player1Score || '');
//         setPlayer2Score(response.data.player2Score || '');
//       } catch (error) {
//         console.error('Error fetching match details:', error);
//       }
//     };

//     fetchMatchDetails();
//   }, [tournamentId, eventId, stageType, stageId, matchId]);

//   const handleSave = async () => {
//     try {
//       await axios.put(
//         `/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}/match/${matchId}`, 
//         {
//           player1Score,
//           player2Score
//         }
//       );
//       alert('Scores saved successfully!');
//     } catch (error) {
//       console.error('Error saving scores:', error);
//     }
//   };

//   if (!match) {
//     return <p>Loading match details...</p>;
//   }

//   return (
//     <div className="match-details-page">
//       <nav className="breadcrumb">
//         <span onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}`)}>
//           {stageType === 'groupStage' ? 'GroupStage' : 'KnockoutStage'}
//         </span> &gt;
//         <span>Match {matchId}</span>
//       </nav>

//       <h1>Match {match.id}</h1>
//       <p>Player 1: @{match.player1.username}</p>
//       <p>Player 2: @{match.player2.username}</p>

//       <div className="score-input">
//         <label>
//           Player 1 Score:
//           <input
//             type="number"
//             value={player1Score}
//             onChange={(e) => setPlayer1Score(e.target.value)}
//           />
//         </label>

//         <label>
//           Player 2 Score:
//           <input
//             type="number"
//             value={player2Score}
//             onChange={(e) => setPlayer2Score(e.target.value)}
//           />
//         </label>
//       </div>

//       <button onClick={handleSave}>Save</button>
//     </div>
//   );
// };

// export default AdminMatchDetailsPage;


// import React, { useState, useEffect } from 'react';
// import { useParams } from 'react-router-dom';
// import axios from 'axios';
// import './MatchDetailsPage.css';

// const MatchDetailsPage = () => {
//   const { tournamentId, eventId, groupStageId, matchId } = useParams(); // Get IDs from URL params
//   const [match, setMatch] = useState(null);
//   const [player1Score, setPlayer1Score] = useState('');
//   const [player2Score, setPlayer2Score] = useState('');

//   useEffect(() => {
//     // Fetch match details
//     const fetchMatchDetails = async () => {
//       try {
//         const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStageId}/match/${matchId}`);
//         setMatch(response.data);
//       } catch (error) {
//         console.error('Error fetching match details:', error);
//       }
//     };

//     fetchMatchDetails();
//   }, [tournamentId, eventId, groupStageId, matchId]);

//   const handleSave = async () => {
//     try {
//       await axios.put(`/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStageId}/match/${matchId}`, {
//         player1Score,
//         player2Score,
//       });
//       alert('Scores saved successfully!');
//     } catch (error) {
//       console.error('Error saving scores:', error);
//     }
//   };

//   if (!match) {
//     return <p>Loading match details...</p>;
//   }

//   return (
//     <div className="match-details-page">
//       <nav className="breadcrumb">
//         <span onClick={() => navigate(`/admin/tournaments/${tournamentId}/events/${eventId}/groupStage/${groupStageId}`)}>GroupStage</span> &gt;
//         <span>Match {matchId}</span>
//       </nav>

//       <h1>Match {match.id}</h1>
//       <p>Player 1: {match.player1.name}</p>
//       <p>Player 2: {match.player2.name}</p>

//       {/* Input Scores */}
//       <div className="score-input">
//         <label>
//           Player 1 Score:
//           <input
//             type="number"
//             value={player1Score}
//             onChange={(e) => setPlayer1Score(e.target.value)}
//           />
//         </label>

//         <label>
//           Player 2 Score:
//           <input
//             type="number"
//             value={player2Score}
//             onChange={(e) => setPlayer2Score(e.target.value)}
//           />
//         </label>
//       </div>

//       <button onClick={handleSave}>Save</button>
//     </div>
//   );
// };

// export default MatchDetailsPage;
