import React, { useState, useEffect } from 'react';
import '../styles/AdminEditTournament.css';

const AdminEditTournament = ({ tournament, onClose, onSave }) => {
  const [name, setName] = useState('');
  const [registrationStartDate, setRegistrationStart] = useState('');
  const [registrationEndDate, setRegistrationEnd] = useState('');
  const [tournamentStartDate, setTournamentStart] = useState('');
  const [tournamentEndDate, setTournamentEnd] = useState('');
  const [venue, setVenue] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSaving, setIsSaving] = useState(false); // New loading state

  useEffect(() => {
    // Pre-fill values when tournament data changes
    if (tournament) {
      setName(tournament.name);
      setRegistrationStart(tournament.registrationStartDate);
      setRegistrationEnd(tournament.registrationEndDate);
      setTournamentStart(tournament.tournamentStartDate);
      setTournamentEnd(tournament.tournamentEndDate);
      setVenue(tournament.venue);
    }
  }, [tournament]);

  const handleSave = async () => {
    // Validation: Check if registration dates and tournament dates are valid
    if (new Date(registrationStartDate) < new Date()) {
      setErrorMessage('Registration start date must be in the present or future.');
      return;
    }
    
    if (new Date(registrationStartDate) >= new Date(registrationEndDate)) {
      setErrorMessage('Registration start date must be before the registration end date.');
      return;
    }
    
    if (new Date(tournamentStartDate) < new Date(registrationEndDate)) {
      setErrorMessage('Tournament start date cannot be before registration start date.');
      return;
    }

    if (new Date(tournamentStartDate) >= new Date(tournamentEndDate)) {
      setErrorMessage('Tournament start date must be before the tournament end date.');
      return;
    }

    // Validation for empty fields
    if (!name || !venue) {
      setErrorMessage('All fields are required.');
      return;
    }

    setIsSaving(true); // Start loading state

    try {
      await onSave({
        ...tournament,
        name,
        registrationStartDate,
        registrationEndDate,
        tournamentStartDate,
        tournamentEndDate,
        venue,
      });

      setErrorMessage(''); // Clear error message on successful save
      onClose(); // Close modal after save
    } catch (error) {
      setErrorMessage('Error saving tournament. Please try again.');
    } finally {
      setIsSaving(false); // Stop loading state
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <h2>Edit Tournament</h2>
        <div className="modal-content">
          {errorMessage && <p className="error-message">{errorMessage}</p>}

          <label>Name</label>
          <input
            type="text"
            value={name}
            onChange={(e) => {
              setName(e.target.value);
              setErrorMessage(''); // Clear error message when editing
            }}
            disabled={isSaving}
          />

          <label>Registration Start Date</label>
          <input
            type="date"
            value={registrationStartDate}
            onChange={(e) => {
              setRegistrationStart(e.target.value);
              setErrorMessage(''); // Clear error message when editing
            }}
            disabled={isSaving}
          />

          <label>Registration End Date</label>
          <input
            type="date"
            value={registrationEndDate}
            onChange={(e) => {
              setRegistrationEnd(e.target.value);
              setErrorMessage(''); // Clear error message when editing
            }}
            disabled={isSaving}
          />

          <label>Tournament Start Date</label>
          <input
            type="date"
            value={tournamentStartDate}
            onChange={(e) => {
              setTournamentStart(e.target.value);
              setErrorMessage(''); // Clear error message when editing
            }}
            disabled={isSaving}
          />

          <label>Tournament End Date</label>
          <input
            type="date"
            value={tournamentEndDate}
            onChange={(e) => {
              setTournamentEnd(e.target.value);
              setErrorMessage(''); // Clear error message when editing
            }}
            disabled={isSaving}
          />

          <label>Venue</label>
          <input
            type="text"
            value={venue}
            onChange={(e) => {
              setVenue(e.target.value);
              setErrorMessage(''); // Clear error message when editing
            }}
            disabled={isSaving}
          />
        </div>
        <div className="modal-actions">
          <button onClick={onClose} disabled={isSaving}>
            Cancel
          </button>
          <button onClick={handleSave} disabled={isSaving || !name || !venue || !registrationStartDate || !registrationEndDate || !tournamentStartDate || !tournamentEndDate}>
            {isSaving ? 'Saving...' : 'Save'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminEditTournament;


// import React, { useState, useEffect } from 'react';
// import '../styles/AdminEditTournament.css';

// const AdminEditTournament = ({ tournament, onClose, onSave }) => {
//   const [name, setName] = useState('');
//   const [registrationStart, setRegistrationStart] = useState('');
//   const [registrationEnd, setRegistrationEnd] = useState('');
//   const [tournamentStart, setTournamentStart] = useState('');
//   const [tournamentEnd, setTournamentEnd] = useState('');
//   const [venue, setVenue] = useState('');
//   const [errorMessage, setErrorMessage] = useState('');

//   useEffect(() => {
//     // Pre-fill values when tournament data changes
//     if (tournament) {
//       setName(tournament.name);
//       setRegistrationStart(tournament.registrationStart);
//       setRegistrationEnd(tournament.registrationEnd);
//       setTournamentStart(tournament.startDate);
//       setTournamentEnd(tournament.endDate);
//       setVenue(tournament.venue);
//     }
//   }, [tournament]);

//   const handleSave = () => {
//     // Validation: Check if registration dates and tournament dates are valid
//     if (new Date(registrationStart) >= new Date(registrationEnd)) {
//       setErrorMessage('Registration start date must be before the registration end date.');
//       return;
//     }

//     if (new Date(tournamentStart) >= new Date(tournamentEnd)) {
//       setErrorMessage('Tournament start date must be before the tournament end date.');
//       return;
//     }

//     // Validation for empty fields
//     if (!name || !venue) {
//       setErrorMessage('All fields are required.');
//       return;
//     }

//     onSave({
//       ...tournament,
//       name,
//       registrationStart,
//       registrationEnd,
//       startDate: tournamentStart,
//       endDate: tournamentEnd,
//       venue,
//     });

//     setErrorMessage(''); // Clear error message on successful save
//   };

//   return (
//     <div className="modal-backdrop">
//       <div className="modal">
//         <h2>Edit Tournament</h2>
//         <div className="modal-content">
//           {errorMessage && <p className="error-message">{errorMessage}</p>}
//           <label>Name</label>
//           <input
//             type="text"
//             value={name}
//             onChange={(e) => setName(e.target.value)}
//           />

//           <label>Registration Start Date</label>
//           <input
//             type="date"
//             value={registrationStart}
//             onChange={(e) => setRegistrationStart(e.target.value)}
//           />

//           <label>Registration End Date</label>
//           <input
//             type="date"
//             value={registrationEnd}
//             onChange={(e) => setRegistrationEnd(e.target.value)}
//           />

//           <label>Tournament Start Date</label>
//           <input
//             type="date"
//             value={tournamentStart}
//             onChange={(e) => setTournamentStart(e.target.value)}
//           />

//           <label>Tournament End Date</label>
//           <input
//             type="date"
//             value={tournamentEnd}
//             onChange={(e) => setTournamentEnd(e.target.value)}
//           />

//           <label>Venue</label>
//           <input
//             type="text"
//             value={venue}
//             onChange={(e) => setVenue(e.target.value)}
//           />
//         </div>
//         <div className="modal-actions">
//           <button onClick={onClose}>Cancel</button>
//           <button onClick={handleSave}>Save</button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default AdminEditTournament;


// import React, { useState } from 'react';
// import './EditTournamentModal.css';

// const EditTournamentModal = ({ tournament, onClose, onSave }) => {
//   const [name, setName] = useState(tournament.name);
//   const [registrationStart, setRegistrationStart] = useState(tournament.registrationStart);
//   const [registrationEnd, setRegistrationEnd] = useState(tournament.registrationEnd);
//   const [tournamentStart, setTournamentStart] = useState(tournament.startDate);
//   const [tournamentEnd, setTournamentEnd] = useState(tournament.endDate);
//   const [venue, setVenue] = useState(tournament.venue);
//   const [errorMessage, setErrorMessage] = useState('');

//   const handleSave = () => {
//     // Add validation if needed
//     if (!name || !registrationStart || !registrationEnd || !tournamentStart || !tournamentEnd || !venue) {
//       setErrorMessage('All fields are required.');
//       return;
//     }

//     // Call onSave function passed as prop
//     onSave({
//       ...tournament,
//       name,
//       registrationStart,
//       registrationEnd,
//       startDate: tournamentStart,
//       endDate: tournamentEnd,
//       venue,
//     });

//     setErrorMessage('');  // Clear error after saving
//   };

//   return (
//     <div className="modal-backdrop">
//       <div className="modal">
//         <h2>Edit Tournament</h2>
//         <div className="modal-content">
//           {errorMessage && <p className="error-message">{errorMessage}</p>}
//           <label>Name</label>
//           <input
//             type="text"
//             value={name}
//             onChange={(e) => setName(e.target.value)}
//           />

//           <label>Registration Start Date</label>
//           <input
//             type="date"
//             value={registrationStart}
//             onChange={(e) => setRegistrationStart(e.target.value)}
//           />

//           <label>Registration End Date</label>
//           <input
//             type="date"
//             value={registrationEnd}
//             onChange={(e) => setRegistrationEnd(e.target.value)}
//           />

//           <label>Tournament Start Date</label>
//           <input
//             type="date"
//             value={tournamentStart}
//             onChange={(e) => setTournamentStart(e.target.value)}
//           />

//           <label>Tournament End Date</label>
//           <input
//             type="date"
//             value={tournamentEnd}
//             onChange={(e) => setTournamentEnd(e.target.value)}
//           />

//           <label>Venue</label>
//           <input
//             type="text"
//             value={venue}
//             onChange={(e) => setVenue(e.target.value)}
//           />
//         </div>
//         <div className="modal-actions">
//           <button onClick={onClose}>Cancel</button>
//           <button onClick={handleSave}>Save</button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default EditTournamentModal;
