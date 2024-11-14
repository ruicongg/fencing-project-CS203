import React from 'react';
import '../styles/UserTournamentCard.css';
import { format, isValid } from 'date-fns'; // Optional if you need formatted dates

const TournamentCard = ({ tournament, onSelect, showStatus = true }) => {
  const startDate = new Date(tournament.tournamentStartDate);
  const endDate = new Date(tournament.tournamentEndDate);
  const formattedStartDate = isValid(startDate) ? format(startDate, 'MMM d, yyyy') : 'Date not available';
  const formattedEndDate = isValid(endDate) ? format(endDate, 'MMM d, yyyy') : 'Date not available';

  // const participantCount = tournament.participants !== undefined ? tournament.participants : '0';

  return (
    <div className="list-item" onClick={onSelect}>
    <div className="item-content">
      <h2>{tournament.name}</h2>
      <p>{formattedStartDate} - {formattedEndDate}</p>
      <p><strong>Venue:</strong> {tournament.venue}</p>
      <h4>Registration deadline: {tournament.registrationEndDate}</h4>
    </div>
    </div>
  );
};

export default TournamentCard;


// import React from 'react';
// import '../styles/UserTournamentCard.css';

// const TournamentCard = ({ tournament, onSelect, showStatus = true }) => {
//   return (
//     <div className="tournament-card" onClick={onSelect}>
//       <h3>{tournament.name}</h3>
//       <p>{tournament.startDate} - {tournament.endDate}</p>
//       <p>{tournament.venue}</p>
//       <p>{tournament.participants || '0'} Participants</p>
//       {/* Conditionally render the status button */}
//       {showStatus && (
//         <button className={tournament.status === 'Ongoing' ? 'ongoing' : 'upcoming'}>
//           {tournament.status}
//         </button>
//       )}
//     </div>
//   );
// };

// export default TournamentCard;


// import React from 'react';
// import '../styles/TournamentCard.css';

// const TournamentCard = ({ tournament, isSelected, onSelect }) => {
//   return (
//     <div
//       className={`tournament-card ${isSelected ? 'selected' : ''}`}
//       onClick={onSelect}
//     >
//       <h3>{tournament.name}</h3>
//       <p>{tournament.startDate} - {tournament.endDate}</p>
//       <p>{tournament.venue}</p>
//       <p>{tournament.participants} Participants</p>
//       <button className={tournament.status === 'Ongoing' ? 'ongoing' : 'upcoming'}>
//         {tournament.status}
//       </button>
//     </div>
//   );
// };

// export default TournamentCard;
