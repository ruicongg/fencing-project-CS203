import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "../styles/shared/index.css"; // Import the relevant CSS

axios.defaults.baseURL = "http://localhost:8080";

const AdminMatchDetailsPage = () => {
  const { tournamentId, eventId, stageType, stageId, matchId } = useParams();
  const navigate = useNavigate();

  const [match, setMatch] = useState(null);
  const [player1Score, setPlayer1Score] = useState("");
  const [player2Score, setPlayer2Score] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMatchDetails = async () => {
      try {
        const response = await axios.get(
          `/tournaments/${tournamentId}/events/${eventId}/match/${matchId}`
        );
        setMatch(response.data);
        setPlayer1Score(response.data.player1Score || "");
        setPlayer2Score(response.data.player2Score || "");
      } catch (error) {
        setError("Error fetching match details.", error);
        // console.error('Error fetching match details:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchMatchDetails();
  }, [tournamentId, eventId, stageType, stageId, matchId]);

  const handleSave = async () => {
    if (
      !player1Score ||
      !player2Score ||
      isNaN(player1Score) ||
      isNaN(player2Score)
    ) {
      setError("Please enter valid scores for both players.");
      return;
    }

    setSaving(true);
    setError(null);
    try {
      await axios.put(
        `/tournaments/${tournamentId}/events/${eventId}/match/${matchId}`,
        {
          player1: match.player1,
          player2: match.player2,
          player1Score: Number(player1Score),
          player2Score: Number(player2Score),
        }
      );
      alert("Scores saved successfully!");
    } catch (error) {
      console.error("Error saving scores:", error);
      setError("Error saving scores. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  const handleScoreChange = (setter) => (e) => {
    setter(e.target.value);
    setError(null); // Clear error when user starts typing
  };

  if (loading) {
    return <p>Loading match details...</p>;
  }

  if (error) {
    return <p className="error-message">{error}</p>;
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

      <nav className="breadcrumb">
        <span onClick={() => navigate(`/admin/tournaments/${tournamentId}`)}>
          Tournaments
        </span>
        <span className="separator">/</span>
        <span
          onClick={() =>
            navigate(`/admin/tournaments/${tournamentId}/events/${eventId}`)
          }
        >
          Event
        </span>
        <span className="separator">/</span>
        <span
          onClick={() =>
            navigate(
              `/admin/tournaments/${tournamentId}/events/${eventId}/${stageType}/${stageId}`
            )
          }
        >
          {stageType === "groupStage" ? "Group Stage" : "Knockout Stage"}
        </span>
        <span className="separator">/</span>
        <a className="active">Match</a>
      </nav>

      <div className="section-container">
        <h1>Match ID: {match.id}</h1>
        <div className="modal">
          <div className="modal-action">
            <label>
              <h3>Player 1 @{match.player1.username}:</h3>
              <input
                type="number"
                value={player1Score}
                onChange={handleScoreChange(setPlayer1Score)}
                disabled={saving}
                placeholder="Enter Score"
              />
            </label>

            <label>
              <h3>Player 2 @{match.player2.username}:</h3>
              <input
                type="number"
                value={player2Score}
                onChange={handleScoreChange(setPlayer2Score)}
                disabled={saving}
                placeholder="Enter Score"
              />
            </label>
          </div>
        </div>

        <button
          onClick={handleSave}
          disabled={saving || !player1Score || !player2Score}
        >
          {saving ? "Saving..." : "Save"}
        </button>

        {error && <p className="error-message">{error}</p>}
      </div>
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
