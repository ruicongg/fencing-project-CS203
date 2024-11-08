import React, { useState } from 'react';
import '../styles/AdminCreateEvent.css';

const AdminCreateEvent = ({ onClose, onAdd }) => {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [gender, setGender] = useState('MALE');
  const [weapon, setWeapon] = useState('FOIL');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSaving, setIsSaving] = useState(false); // Add loading state

  const handleAdd = async () => {
    // Validate form inputs
    if (!startDate || !endDate) {
      setErrorMessage('Both start and end times are required.');
      return;
    }

    if (new Date(startDate) >= new Date(endDate)) {
      setErrorMessage('Start time must be before end time.');
      return;
    }

    // If validation passes, start saving
    setIsSaving(true);

    try {
      // Call the onAdd function passed from the parent component
      await onAdd({
        startDate,
        endDate,
        gender,
        weapon
      });

      // Reset error and close modal after successfully adding
      setErrorMessage('');
      onClose();
    } catch (error) {
      setErrorMessage('Failed to add event. Please try again.');
    } finally {
      setIsSaving(false); // Stop saving
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <h2>Create New Event</h2>
        <div className="modal-content">
          {errorMessage && <p className="error-message">{errorMessage}</p>}
          <label>Event Start Date Time</label>
          <input
            type="datetime-local"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
          to
          <input
            type="datetime-local"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />

          <label>Gender</label>
          <select value={gender} onChange={(e) => setGender(e.target.value)}>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
          </select>

          <label>Weapon</label>
          <select value={weapon} onChange={(e) => setWeapon(e.target.value)}>
            <option value="FOIL">Foil</option>
            <option value="EPEE">Épée</option>
            <option value="SABRE">Sabre</option>
          </select>
        </div>
        <div className="modal-actions">
          <button onClick={onClose} disabled={isSaving}>Cancel</button>
          <button onClick={handleAdd} disabled={isSaving || !startDate || !endDate}>
            {isSaving ? 'Adding...' : 'Add'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminCreateEvent;


// import React, { useState } from 'react';
// import '../styles/AdminCreateEvent.css';

// const AdminCreateEvent = ({ onClose, onAdd }) => {
//   const [startDateTime, setStartDateTime] = useState('');
//   const [endDateTime, setEndDateTime] = useState('');
//   const [gender, setGender] = useState('Male');
//   const [weapon, setWeapon] = useState('Foil');
//   const [errorMessage, setErrorMessage] = useState('');

//   const handleAdd = () => {
//     // Validate form inputs
//     if (!startDateTime || !endDateTime) {
//       setErrorMessage('Both start and end times are required.');
//       return;
//     }

//     if (new Date(startDateTime) >= new Date(endDateTime)) {
//       setErrorMessage('Start time must be before end time.');
//       return;
//     }

//     // If validation passes, call the onAdd function
//     onAdd({
//       startDateTime,
//       endDateTime,
//       gender,
//       weapon
//     });

//     // Reset error and close modal
//     setErrorMessage('');
//     onClose();
//   };

//   return (
//     <div className="modal-backdrop">
//       <div className="modal">
//         <h2>Create New Event</h2>
//         <div className="modal-content">
//           {errorMessage && <p className="error-message">{errorMessage}</p>}
//           <label>Event Date Time</label>
//           <input
//             type="datetime-local"
//             value={startDateTime}
//             onChange={(e) => setStartDateTime(e.target.value)}
//           />
//           to
//           <input
//             type="datetime-local"
//             value={endDateTime}
//             onChange={(e) => setEndDateTime(e.target.value)}
//           />

//           <label>Gender</label>
//           <select value={gender} onChange={(e) => setGender(e.target.value)}>
//             <option value="Male">Male</option>
//             <option value="Female">Female</option>
//             <option value="Mixed">Mixed</option>
//           </select>

//           <label>Weapon</label>
//           <select value={weapon} onChange={(e) => setWeapon(e.target.value)}>
//             <option value="Foil">Foil</option>
//             <option value="Epee">Épée</option>
//             <option value="Sabre">Sabre</option>
//           </select>
//         </div>
//         <div className="modal-actions">
//           <button onClick={onClose}>Cancel</button>
//           <button onClick={handleAdd} disabled={!startDateTime || !endDateTime}>Add</button> {/* Disable if empty */}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default AdminCreateEvent;


// import React, { useState } from 'react';
// import './CreateEventModal.css';

// const CreateEventModal = ({ onClose, onAdd }) => {
//   const [startDateTime, setStartDateTime] = useState('');
//   const [endDateTime, setEndDateTime] = useState('');
//   const [gender, setGender] = useState('Male');
//   const [weapon, setWeapon] = useState('Foil');
//   const [errorMessage, setErrorMessage] = useState('');

//   const handleAdd = () => {
//     // Validate form inputs
//     if (!startDateTime || !endDateTime) {
//       setErrorMessage('Both start and end times are required.');
//       return;
//     }

//     if (new Date(startDateTime) >= new Date(endDateTime)) {
//       setErrorMessage('Start time must be before end time.');
//       return;
//     }

//     // If validation passes, call the onAdd function
//     onAdd({
//       startDateTime,
//       endDateTime,
//       gender,
//       weapon
//     });
    
//     // Reset error and close modal
//     setErrorMessage('');
//   };

//   return (
//     <div className="modal-backdrop">
//       <div className="modal">
//         <h2>Create New Event</h2>
//         <div className="modal-content">
//           {errorMessage && <p className="error-message">{errorMessage}</p>}
//           <label>Event Date Time</label>
//           <input
//             type="datetime-local"
//             value={startDateTime}
//             onChange={(e) => setStartDateTime(e.target.value)}
//           />
//           to
//           <input
//             type="datetime-local"
//             value={endDateTime}
//             onChange={(e) => setEndDateTime(e.target.value)}
//           />

//           <label>Gender</label>
//           <select value={gender} onChange={(e) => setGender(e.target.value)}>
//             <option value="Male">Male</option>
//             <option value="Female">Female</option>
//             <option value="Mixed">Mixed</option>
//           </select>

//           <label>Weapon</label>
//           <select value={weapon} onChange={(e) => setWeapon(e.target.value)}>
//             <option value="Foil">Foil</option>
//             <option value="Epee">Épée</option>
//             <option value="Sabre">Sabre</option>
//           </select>
//         </div>
//         <div className="modal-actions">
//           <button onClick={onClose}>Cancel</button>
//           <button onClick={handleAdd}>Add</button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default CreateEventModal;
