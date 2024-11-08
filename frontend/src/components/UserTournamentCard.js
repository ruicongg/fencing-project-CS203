import React from 'react';
import '../styles/UserTournamentCard.css';
import { format } from 'date-fns'; // Optional if you need formatted dates

const TournamentCard = ({ tournament, onSelect, showStatus = true }) => {
  const formattedStartDate = new Date(tournament.startDate).toLocaleDateString();
  const formattedEndDate = new Date(tournament.endDate).toLocaleDateString();

  return (
    <div className="tournament-card" onClick={onSelect}>
      <h3>{tournament.name}</h3>
      <p>{formattedStartDate} - {formattedEndDate}</p>
      <p>{tournament.venue}</p>
      <p>{tournament.participants !== undefined ? tournament.participants : '0'} Participants</p>
      
      {/* Conditionally render the status button */}
      {showStatus && (
        <button className={tournament.status === 'Ongoing' ? 'ongoing' : 'upcoming'}>
          {tournament.status}
        </button>
      )}
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
