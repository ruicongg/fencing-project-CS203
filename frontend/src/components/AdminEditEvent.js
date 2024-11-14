import axios from "axios";
import React, { useState, useEffect } from "react";
import "../styles/AdminEditEvent.css";

axios.defaults.baseURL = "https://parry-hub.com";

const AdminEditEvent = ({ event, tournamentId, onClose, onSave }) => {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [gender, setGender] = useState("");
  const [weapon, setWeapon] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    // Pre-fill values when event data is loaded
    if (event) {
      setStartDate(event.startDate);
      setEndDate(event.endDate);
      setGender(event.gender);
      setWeapon(event.weapon);
    }
  }, [event]);

  const handleSave = async () => {
    setIsSaving(true);
    setErrorMessage(""); // Clear any previous error message

    try {
      console.log(
        "Saving event with tournamentId:",
        tournamentId,
        "eventId:",
        event.id
      );
      const response = await axios.put(
        `/tournaments/${tournamentId}/events/${event.id}`,
        {
          ...event,
          startDate,
          endDate,
          gender,
          weapon,
        }
      );

      onSave(response.data); // Pass the updated event back to the parent component on successful save
    } catch (error) {
      console.log("Error saving event:", error);
      if (error.response) {
        // Display backend error message if available
        setErrorMessage(
          error.response.data.error || "An unexpected error occurred."
        );
      } else {
        setErrorMessage("Failed to save event. Please try again.");
      }
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <h2>Edit Event</h2>
        <div className="modal-content">
          {errorMessage && <p className="error-message">{errorMessage}</p>}
          <label>Event Date Time</label>
          <input
            type="datetime-local"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            disabled={isSaving}
          />
          to
          <input
            type="datetime-local"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
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
          <button
            onClick={onClose}
            disabled={isSaving}
            className="cancel-button"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            disabled={isSaving || !startDate || !endDate}
            className="add-button"
          >
            {isSaving ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminEditEvent;

// import React, { useState, useEffect } from 'react';
// import '../styles/AdminEditEvent.css';

// const AdminEditEvent = ({ event, onClose, onSave }) => {
//   const [startDateTime, setStartDateTime] = useState('');
//   const [endDateTime, setEndDateTime] = useState('');
//   const [gender, setGender] = useState('');
//   const [weapon, setWeapon] = useState('');
//   const [errorMessage, setErrorMessage] = useState('');

//   useEffect(() => {
//     // Pre-fill values when event data changes
//     if (event) {
//       setStartDateTime(event.startDateTime);
//       setEndDateTime(event.endDateTime);
//       setGender(event.gender);
//       setWeapon(event.weapon);
//     }
//   }, [event]);

//   const handleSave = () => {
//     // Validation: Check if the start date is before the end date
//     if (new Date(startDateTime) >= new Date(endDateTime)) {
//       setErrorMessage('Start date must be before the end date.');
//       return;
//     }

//     onSave({
//       ...event,
//       startDateTime,
//       endDateTime,
//       gender,
//       weapon
//     });

//     setErrorMessage('');  // Clear error message on successful save
//   };

//   return (
//     <div className="modal-backdrop">
//       <div className="modal">
//         <h2>Edit Event</h2>
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
//           <button onClick={handleSave}>Save</button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default AdminEditEvent;

// import React, { useState } from 'react';
// import './EditEventModal.css';

// const EditEventModal = ({ event, onClose, onSave }) => {
//   const [startDateTime, setStartDateTime] = useState(event.startDateTime);
//   const [endDateTime, setEndDateTime] = useState(event.endDateTime);
//   const [gender, setGender] = useState(event.gender);
//   const [weapon, setWeapon] = useState(event.weapon);

//   const handleSave = () => {
//     onSave({
//       ...event,
//       startDateTime,
//       endDateTime,
//       gender,
//       weapon
//     });
//   };

//   return (
//     <div className="modal-backdrop">
//       <div className="modal">
//         <h2>Edit Event</h2>
//         <div className="modal-content">
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
//           <button onClick={handleSave}>Save</button>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default EditEventModal;
