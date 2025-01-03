import axios from "axios";
import React, { useState } from "react";
import "../styles/AdminCreateEvent.css";
import { DateTimeRangeField } from "./shared/DateTimeRangeField";
import "../styles/shared/Modal.css";

axios.defaults.baseURL = "http://localhost:8080";

const AdminCreateEvent = ({ tournamentId, onClose, onAdd }) => {
  const formatDateTime = (date) => {
    // Get timezone offset in minutes and convert to milliseconds
    const tzOffset = date.getTimezoneOffset() * 60000;

    // Create new date adjusted for timezone
    const localDate = new Date(date.getTime() - tzOffset);

    // Return in ISO format but only up to minutes
    return localDate.toISOString().slice(0, 16);
  };

  // Calculate initial dates

  const eventStartDateTime = new Date();
  eventStartDateTime.setDate(eventStartDateTime.getDate() + 60);
  eventStartDateTime.setHours(8, 0, 0, 0);
  const eventEndDateTime = new Date();
  eventEndDateTime.setDate(eventEndDateTime.getDate() + 90);
  eventEndDateTime.setHours(17, 0, 0, 0);

  const [startDate, setStartDate] = useState(
    formatDateTime(eventStartDateTime)
  );
  const [endDate, setEndDate] = useState(formatDateTime(eventEndDateTime));
  const [gender, setGender] = useState("MALE");
  const [weapon, setWeapon] = useState("FOIL");
  const [errorMessage, setErrorMessage] = useState("");
  const [isSaving, setIsSaving] = useState(false); // Add loading state
  const handleAdd = async () => {
    // Set loading state
    setIsSaving(true);
    setErrorMessage(""); // Clear any previous error

    try {
      // Call the onAdd function with the new event details
      const response = await axios.post(`/tournaments/${tournamentId}/events`, {
        startDate,
        endDate,
        gender,
        weapon,
      });
      onAdd(response.data); // Pass the newly created event data back
      onClose(); // Close the modal
    } catch (error) {
      console.error("Error creating event:", error);
      if (error.response && error.response.data && error.response.data.error) {
        setErrorMessage(error.response.data.error);
      } else {
        setErrorMessage("Failed to add event. Please try again.");
      }
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <h2>Create New Event</h2>
        <div className="modal-content">
          {errorMessage && <p className="error-message">{errorMessage}</p>}

          <DateTimeRangeField
            label="Event Start Date Time"
            startValue={startDate}
            endValue={endDate}
            onStartChange={(e) => setStartDate(e.target.value)}
            onEndChange={(e) => setEndDate(e.target.value)}
            disabled={isSaving}
          />

          <label>Gender</label>
          <select
            value={gender}
            onChange={(e) => setGender(e.target.value)}
            disabled={isSaving}
          >
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
          </select>

          <label>Weapon</label>
          <select
            value={weapon}
            onChange={(e) => setWeapon(e.target.value)}
            disabled={isSaving}
          >
            <option value="FOIL">Foil</option>
            <option value="EPEE">Épée</option>
            <option value="SABER">Saber</option>
          </select>
        </div>
        <div className="modal-actions">
          <button onClick={onClose} disabled={isSaving}>
            Cancel
          </button>
          <button
            onClick={handleAdd}
            disabled={isSaving || !startDate || !endDate}
          >
            {isSaving ? "Adding..." : "Add"}
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
