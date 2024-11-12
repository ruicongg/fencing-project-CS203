import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/ViewPlayers.css';

axios.defaults.baseURL = 'http://localhost:8080';

const ViewPlayers = ({ onClose, eventId, tournamentId }) => {
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPlayers = async () => {
      try {
        // Fetch event data including rankings
        const response = await axios.get(`/tournaments/${tournamentId}/events/${eventId}`);
        const event = response.data;
        console.log(event.rankings);
  
        // Check if rankings is defined before proceeding
        if (!event.rankings) {
          console.error("rankings is undefined or null in the response:", event);
          setError('No players registered in this event.');
          return;
        }
  
        // Extract players from rankings
        const playerList = event.rankings.map((ranking) => {
          const player = ranking.player; // Get the player object from ranking
          return {
            id: player.id,
            username: player.username,
          };
        });
  
        setPlayers(playerList);
      } catch (error) {
        setError('Error fetching players. Please try again later.');
        console.error('Error fetching players:', error);
      } finally {
        setLoading(false);
      }
    };
  
    fetchPlayers();
  }, [eventId, tournamentId]);

  return (
    <div className="modal-backdrop" role="dialog" aria-labelledby="players-title" aria-describedby="players-list">
      <div className="modal">
        <h2 id="players-title">Players</h2>

        <div className="players-list" id="players-list">
          {loading && <p>Loading players...</p>}
          {error && <p className="error-message">{error}</p>}

          {!loading && !error && (
            players.length > 0 ? (
              <ul>
                {players.map(player => (
                  <li key={player.id}>
                    Player ID: {player.id} @{player.username || 'Unknown'}
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
