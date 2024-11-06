import React from 'react';
import { format } from 'date-fns'; // Importing date-fns for consistent date formatting
import '../styles/AdminTournamentCard.css';

const AdminTournamentCard = ({ tournament, onSelect, onEdit, onDelete }) => {
  // Date formatting using date-fns for better control over the format
  const formattedStartDate = format(new Date(tournament.tournamentStartDate), 'PPP');
  const formattedEndDate = format(new Date(tournament.tournamentEndDate), 'PPP');

  return (
    <div
      className="tournament-card"
      onClick={() => onSelect(tournament)}  // Handle entire card click to select tournament
      style={{ cursor: 'pointer' }} // Add pointer cursor to indicate clickability
    >
      <h3>{tournament.name}</h3>
      <p>{formattedStartDate} - {formattedEndDate}</p>
      <p>{tournament.venue}</p>
      <p>{tournament.participants || '0'} Participants</p>
      
      {/* Edit button with click propagation stopped */}
      <button
        onClick={(e) => {
          e.stopPropagation(); // Prevent the card click event from triggering
          onEdit(tournament);
        }}
        className="edit-button"
        aria-label={`Edit ${tournament.name}`} // Added for accessibility
      >
        Edit
      </button>

      {/* Delete button with click propagation stopped */}
      <button
        onClick={(e) => {
          e.stopPropagation(); // Prevent the card click event from triggering
          onDelete(tournament);
        }}
        className="delete-button"
        aria-label={`Delete ${tournament.name}`} // Added for accessibility
      >
        Delete
      </button>
    </div>
  );
};

export default AdminTournamentCard;


// import React from 'react';
// import { format } from 'date-fns';  // Importing date-fns for consistent date formatting
// import '../styles/AdminTournamentCard.css';

// const AdminTournamentCard = ({ tournament, onSelect, onEdit }) => {
//   // Date formatting using date-fns for better control over the format
//   const formattedStartDate = format(new Date(tournament.startDate), 'PPP');
//   const formattedEndDate = format(new Date(tournament.endDate), 'PPP');

//   return (
//     <div
//       className="tournament-card"
//       onClick={() => onSelect(tournament)}  // Handle entire card click to select tournament
//       style={{ cursor: 'pointer' }} // Add pointer cursor to indicate clickability
//     >
//       <h3>{tournament.name}</h3>
//       <p>{formattedStartDate} - {formattedEndDate}</p>
//       <p>{tournament.venue}</p>
//       <p>{tournament.participants || '0'} Participants</p>
      
//       {/* Edit button with click propagation stopped */}
//       <button
//         onClick={(e) => {
//           e.stopPropagation(); // Prevent the card click event from triggering
//           onEdit(tournament);
//         }}
//         className="edit-button"
//         aria-label={`Edit ${tournament.name}`} // Added for accessibility
//       >
//         Edit
//       </button>
//     </div>
//   );
// };

// export default AdminTournamentCard;


// import React from 'react';
// import '../styles/AdminTournamentCard.css';

// const AdminTournamentCard = ({ tournament, onSelect, onEdit }) => {
//   // Optional: Date formatting for better readability
//   const formattedStartDate = new Date(tournament.startDate).toLocaleDateString();
//   const formattedEndDate = new Date(tournament.endDate).toLocaleDateString();

//   return (
//     <div
//       className="tournament-card"
//       onClick={() => onSelect(tournament)}  // Handle entire card click to select tournament
//       style={{ cursor: 'pointer' }} // Add pointer cursor to indicate clickability
//     >
//       <h3>{tournament.name}</h3>
//       <p>{formattedStartDate} - {formattedEndDate}</p>
//       <p>{tournament.venue}</p>
//       <p>{tournament.participants || '0'} Participants</p>
      
//       {/* Edit button with click propagation stopped */}
//       <button
//         onClick={(e) => {
//           e.stopPropagation(); // Prevent the card click event from triggering
//           onEdit(tournament);
//         }}
//       >
//         Edit
//       </button>
//     </div>
//   );
// };

// export default AdminTournamentCard;


// import React from 'react';
// import '../styles/TournamentCard.css';

// const TournamentCard = ({ tournament, onEdit }) => {
//   // Optional: Date formatting for better readability
//   const formattedStartDate = new Date(tournament.startDate).toLocaleDateString();
//   const formattedEndDate = new Date(tournament.endDate).toLocaleDateString();

//   return (
//     <div className="tournament-card">
//       <h3>{tournament.name}</h3>
//       <p>{formattedStartDate} - {formattedEndDate}</p>
//       <p>{tournament.venue}</p>
//       <p>{tournament.participants || '0'} Participants</p> {/* Dynamic number of participants */}
//       <button
//         onClick={(e) => {
//           e.stopPropagation(); // Prevent propagation if card itself is clickable
//           onEdit();
//         }}
//       >
//         Edit
//       </button>
//     </div>
//   );
// };

// export default TournamentCard;

