import React, { useState } from 'react';
import '../styles/AdminCreateTournament.css';

const AdminCreateTournament = ({ onClose, onAdd }) => {
  const [name, setName] = useState('');
  const [tournamentStartDate, setStartDate] = useState('');
  const [tournamentEndDate, setEndDate] = useState('');
  const [venue, setVenue] = useState('');
  const [registrationStartDate, setRegistrationStart] = useState('');
  const [registrationEndDate, setRegistrationEnd] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSaving, setIsSaving] = useState(false);

  const handleAdd = async () => {
    if (!name || !tournamentStartDate || !tournamentEndDate || !venue || !registrationStartDate || !registrationEndDate) {
      setErrorMessage('All fields are required.');
      return;
    }

    if (new Date(registrationStartDate) < new Date()) {
      setErrorMessage('Registration start date must be in the present or future.');
      return;
    }

    if (new Date(registrationStartDate) >= new Date(registrationEndDate)) {
      setErrorMessage('Registration start date must be before registration end date.');
      return;
    }

    if (new Date(tournamentStartDate) < new Date(registrationEndDate)) {
      setErrorMessage('Tournament start date cannot be before registration start date.');
      return;
    }

    if (new Date(tournamentStartDate) >= new Date(tournamentEndDate)) {
      setErrorMessage('Tournament start date must be before tournament end date.');
      return;
    }

    setIsSaving(true);

    try {
      await onAdd({
        name,
        tournamentStartDate,
        tournamentEndDate,
        venue,
        registrationStartDate,
        registrationEndDate,
      });

      setErrorMessage('');
      onClose();
    } catch (error) {
      setErrorMessage('Failed to add tournament. Please try again.');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="modal-backdrop" role="dialog" aria-modal="true" aria-labelledby="create-tournament-title">
      <div className="modal">
        <h2 id="create-tournament-title">Create New Tournament</h2>
        <div className="modal-content">
          {errorMessage && <p className="error-message">{errorMessage}</p>}
          
          <label htmlFor="tournament-name">Tournament Name</label>
          <input
            id="tournament-name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            disabled={isSaving}
            aria-required="true"
          />

          <label>Registration Dates</label>
          <div className="date-range">
            <input
              type="date"
              value={registrationStartDate}
              onChange={(e) => setRegistrationStart(e.target.value)}
              disabled={isSaving}
              aria-required="true"
            /> 
            to
            <input
              type="date"
              value={registrationEndDate}
              onChange={(e) => setRegistrationEnd(e.target.value)}
              disabled={isSaving}
              aria-required="true"
            />
          </div>

          <label>Tournament Dates</label>
          <div className="date-range">
            <input
              type="date"
              value={tournamentStartDate}
              onChange={(e) => setStartDate(e.target.value)}
              disabled={isSaving}
              aria-required="true"
            /> 
            to
            <input
              type="date"
              value={tournamentEndDate}
              onChange={(e) => setEndDate(e.target.value)}
              disabled={isSaving}
              aria-required="true"
            />
          </div>

          <label htmlFor="venue">Venue</label>
          <input
            id="venue"
            type="text"
            value={venue}
            onChange={(e) => setVenue(e.target.value)}
            disabled={isSaving}
            aria-required="true"
          />
        </div>
        
        <div className="modal-actions">
          <button onClick={onClose} disabled={isSaving} className="cancel-button">Cancel</button>
          <button
            onClick={handleAdd}
            disabled={
              !name ||
              !tournamentStartDate ||
              !tournamentEndDate ||
              !venue ||
              !registrationStartDate ||
              !registrationEndDate ||
              isSaving
            }
            className="add-button"
          >
            {isSaving ? 'Adding...' : 'Add'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminCreateTournament;


// import React, { useState } from 'react';
// import '../styles/AdminCreateTournament.css';

// const AdminCreateTournament = ({ onClose, onAdd }) => {
//   const [name, setName] = useState('');
//   const [startDate, setStartDate] = useState('');
//   const [endDate, setEndDate] = useState('');
//   const [venue, setVenue] = useState('');
//   const [registrationStart, setRegistrationStart] = useState('');
//   const [registrationEnd, setRegistrationEnd] = useState('');
//   const [errorMessage, setErrorMessage] = useState('');

//   const handleAdd = () => {
//     // Validation
//     if (!name || !startDate || !endDate || !venue || !registrationStart || !registrationEnd) {
//       setErrorMessage('All fields are required.');
//       return;
//     }

//     if (new Date(registrationStart) >= new Date(registrationEnd)) {
//       setErrorMessage('Registration start date must be before registration end date.');
//       return;
//     }

//     if (new Date(startDate) >= new Date(endDate)) {
//       setErrorMessage('Tournament start date must be before the tournament end date.');
//       return;
//     }

//     // If validation passes, call the onAdd function
//     onAdd({
//       name,
//       startDate,
//       endDate,
//       venue,
//       registrationStart,
//       registrationEnd
//     });

//     // Clear error and close modal
//     setErrorMessage('');
//     onClose();
//   };

//   return (
//     <div className="modal-backdrop">
//       <div className="modal">
//         <h2>Create New Tournament</h2>
//         <div className="modal-content">
//           {errorMessage && <p className="error-message">{errorMessage}</p>}
//           <label>Tournament Name</label>
//           <input type="text" value={name} onChange={(e) => setName(e.target.value)} />

//           <label>Registration Dates</label>
//           <input type="date" value={registrationStart} onChange={(e) => setRegistrationStart(e.target.value)} /> to
//           <input type="date" value={registrationEnd} onChange={(e) => setRegistrationEnd(e.target.value)} />

//           <label>Tournament Dates</label>
//           <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} /> to
//           <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />

//           <label>Venue</label>
//           <input type="text" value={venue} onChange={(e) => setVenue(e.target.value)} />
//         </div>
//         <div className="modal-actions">
//           <button onClick={onClose}>Cancel</button>
//           <button onClick={handleAdd} disabled={!name || !startDate || !endDate || !venue || !registrationStart || !registrationEnd}>
//             Add
//           </button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default AdminCreateTournament;


// import React, { useState } from 'react';
// import './CreateTournamentModal.css';

// const CreateTournamentModal = ({ onClose, onAdd }) => {
//   const [name, setName] = useState('');
//   const [startDate, setStartDate] = useState('');
//   const [endDate, setEndDate] = useState('');
//   const [venue, setVenue] = useState('');
//   const [registrationStart, setRegistrationStart] = useState('');
//   const [registrationEnd, setRegistrationEnd] = useState('');

//   const handleAdd = () => {
//     onAdd({
//       name,
//       startDate,
//       endDate,
//       venue,
//       registrationStart,
//       registrationEnd
//     });
//   };

//   return (
//     <div className="modal-backdrop">
//       <div className="modal">
//         <h2>Create New Tournament</h2>
//         <div className="modal-content">
//           <label>Tournament Name</label>
//           <input type="text" value={name} onChange={(e) => setName(e.target.value)} />

//           <label>Registration Dates</label>
//           <input type="date" value={registrationStart} onChange={(e) => setRegistrationStart(e.target.value)} /> to
//           <input type="date" value={registrationEnd} onChange={(e) => setRegistrationEnd(e.target.value)} />

//           <label>Tournament Dates</label>
//           <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} /> to
//           <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />

//           <label>Venue</label>
//           <input type="text" value={venue} onChange={(e) => setVenue(e.target.value)} />
//         </div>
//         <div className="modal-actions">
//           <button onClick={onClose}>Cancel</button>
//           <button onClick={handleAdd}>Add</button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default CreateTournamentModal;
