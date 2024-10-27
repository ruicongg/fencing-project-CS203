import React, { useState } from 'react';
import '../styles/AdminCreateTournament.css';

const AdminCreateTournament = ({ onClose, onAdd }) => {
  const [name, setName] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [venue, setVenue] = useState('');
  const [registrationStart, setRegistrationStart] = useState('');
  const [registrationEnd, setRegistrationEnd] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSaving, setIsSaving] = useState(false); // Add a loading state

  const handleAdd = async () => {
    // Validation
    if (!name || !startDate || !endDate || !venue || !registrationStart || !registrationEnd) {
      setErrorMessage('All fields are required.');
      return;
    }

    if (new Date(registrationStart) >= new Date(registrationEnd)) {
      setErrorMessage('Registration start date must be before registration end date.');
      return;
    }

    if (new Date(startDate) >= new Date(endDate)) {
      setErrorMessage('Tournament start date must be before the tournament end date.');
      return;
    }

    // Set loading state
    setIsSaving(true);

    try {
      // If validation passes, call the onAdd function (which is asynchronous)
      await onAdd({
        name,
        startDate,
        endDate,
        venue,
        registrationStart,
        registrationEnd,
      });

      // Clear error and close modal after successful addition
      setErrorMessage('');
      onClose();
    } catch (error) {
      setErrorMessage('Failed to add tournament. Please try again.');
    } finally {
      setIsSaving(false); // Stop the loading state
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <h2>Create New Tournament</h2>
        <div className="modal-content">
          {errorMessage && <p className="error-message">{errorMessage}</p>}
          <label>Tournament Name</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            disabled={isSaving}
          />

          <label>Registration Dates</label>
          <input
            type="date"
            value={registrationStart}
            onChange={(e) => setRegistrationStart(e.target.value)}
            disabled={isSaving}
          /> 
          to
          <input
            type="date"
            value={registrationEnd}
            onChange={(e) => setRegistrationEnd(e.target.value)}
            disabled={isSaving}
          />

          <label>Tournament Dates</label>
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            disabled={isSaving}
          /> 
          to
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            disabled={isSaving}
          />

          <label>Venue</label>
          <input
            type="text"
            value={venue}
            onChange={(e) => setVenue(e.target.value)}
            disabled={isSaving}
          />
        </div>
        <div className="modal-actions">
          <button onClick={onClose} disabled={isSaving}>Cancel</button>
          <button
            onClick={handleAdd}
            disabled={
              !name ||
              !startDate ||
              !endDate ||
              !venue ||
              !registrationStart ||
              !registrationEnd ||
              isSaving
            }
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
