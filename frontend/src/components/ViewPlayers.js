import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/ViewPlayers.css';

axios.defaults.baseURL = 'http://localhost:8080';

const ViewPlayers = ({ onClose, eventId, tournamentId }) => {
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true); // Loading state
  const [error, setError] = useState(null); // Error state

  useEffect(() => {
    const fetchPlayers = async () => {
      try {
        const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/players`);
        setPlayers(response.data);
      } catch (error) {
        setError('Error fetching players. Please try again later.');
        console.error('Error fetching players:', error);
      } finally {
        setLoading(false); // Ensure loading stops after fetching data or encountering an error
      }
    };

    fetchPlayers();
  }, [eventId, tournamentId]);

  return (
    <div className="modal-backdrop" role="dialog" aria-labelledby="players-title" aria-describedby="players-list">
      <div className="modal">
        <h2 id="players-title">Players</h2>

        <div className="players-list" id="players-list">
          {/* Show loading message */}
          {loading && <p>Loading players...</p>}

          {/* Show error message if any */}
          {error && <p className="error-message">{error}</p>}

          {/* Display list of players or empty state message */}
          {!loading && !error && (
            players.length > 0 ? (
              <ul>
                {players.map(player => (
                  <li key={player.id}>
                    Player ID: {player.id} @{player.username || 'Unknown'} {/* Handle missing username */}
                  </li>
                ))}
              </ul>
            ) : (
              <p>No players registered for this event.</p>
            )
          )}
        </div>

        <div className="modal-actions">
          <button onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
};

export default ViewPlayers;



// import React, { useState, useEffect } from 'react';
// import './ViewPlayersModal.css';

// const ViewPlayersModal = ({ onClose, eventId, tournamentId }) => {
//   const [players, setPlayers] = useState([]);

//   useEffect(() => {
//     const fetchPlayers = async () => {
//       try {
//         // Fetch players registered for this event
//         const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}/players`);
//         setPlayers(response.data);
//       } catch (error) {
//         console.error('Error fetching players:', error);
//       }
//     };

//     fetchPlayers();
//   }, [eventId, tournamentId]);

//   return (
//     <div className="modal-backdrop">
//       <div className="modal">
//         <h2>Players</h2>
//         <div className="players-list">
//           {players.length > 0 ? (
//             <ul>
//               {players.map(player => (
//                 <li key={player.id}>
//                   Player ID: {player.id} @{player.username}
//                 </li>
//               ))}
//             </ul>
//           ) : (
//             <p>No players registered for this event.</p>
//           )}
//         </div>
//         <div className="modal-actions">
//           <button onClick={onClose}>Close</button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default ViewPlayersModal;
