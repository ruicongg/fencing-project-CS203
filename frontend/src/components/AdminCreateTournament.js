import React, { useState } from "react";

import "../styles/shared/Modal.css";
import { FormField } from "./shared/FormField";
import { DateRangeField } from "./shared/DateRangeField";
const AdminCreateTournament = ({ onClose, onAdd }) => {
  // Helper function to format date to YYYY-MM-DD
  const formatDate = (date) => {
    return date.toISOString().split('T')[0];
  };

  // Calculate initial dates
  const today = new Date();
  const registrationEnd = new Date(today);
  registrationEnd.setDate(today.getDate() + 30);
  const tournamentStart = new Date(today);
  tournamentStart.setDate(today.getDate() + 60);
  const tournamentEnd = new Date(today);
  tournamentEnd.setDate(today.getDate() + 90);

  // Initialize state with calculated dates
  const [name, setName] = useState("");
  const [tournamentStartDate, setStartDate] = useState(formatDate(tournamentStart));
  const [tournamentEndDate, setEndDate] = useState(formatDate(tournamentEnd));
  const [venue, setVenue] = useState("");
  const [registrationStartDate, setRegistrationStart] = useState(formatDate(today));
  const [registrationEndDate, setRegistrationEnd] = useState(formatDate(registrationEnd));
  const [errorMessage, setErrorMessage] = useState("");
  const [isSaving, setIsSaving] = useState(false);

  const handleAdd = async () => {
    if (
      !name ||
      !tournamentStartDate ||
      !tournamentEndDate ||
      !venue ||
      !registrationStartDate ||
      !registrationEndDate
    ) {
      setErrorMessage("All fields are required.");
      return;
    }

    if (new Date(registrationStartDate) >= new Date(registrationEndDate)) {
      setErrorMessage(
        "Registration start date must be before registration end date."
      );
      return;
    }

    if (new Date(tournamentStartDate) < new Date(registrationEndDate)) {
      setErrorMessage('Tournament start date cannot be before registration start date.');
      return;
    }

    if (new Date(tournamentStartDate) >= new Date(tournamentEndDate)) {
      setErrorMessage(
        "Tournament start date must be before tournament end date."
      );
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

      setErrorMessage("");
      onClose();
    } catch (error) {
      setErrorMessage("Failed to add tournament. Please try again.");
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div
      className="modal-backdrop"
      role="dialog"
      aria-modal="true"
      aria-labelledby="create-tournament-title"
    >
      <div className="modal">
        <h2 id="create-tournament-title">Create New Tournament</h2>
        <div className="modal-content">
          {errorMessage && <p className="error-message">{errorMessage}</p>}


          <FormField
            id="tournament-name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            disabled={isSaving}
            placeholder="Tournament Name"
            aria-required="true"
          />


          <DateRangeField
            label="Registration Dates"
            startValue={registrationStartDate}
            endValue={registrationEndDate}
            onStartChange={(e) => setRegistrationStart(e.target.value)}
            onEndChange={(e) => setRegistrationEnd(e.target.value)}
            disabled={isSaving}
          />

          <DateRangeField
            label="Tournament Dates"
            startValue={tournamentStartDate}
            endValue={tournamentEndDate}
            onStartChange={(e) => setStartDate(e.target.value)}
            onEndChange={(e) => setEndDate(e.target.value)}
            disabled={isSaving}
          />


          <FormField
            id="venue"
            type="text"
            value={venue}
            onChange={(e) => setVenue(e.target.value)}
            disabled={isSaving}
            placeholder="Venue"
            aria-required="true"
          />

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
            {isSaving ? "Adding..." : "Add"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminCreateTournament;
