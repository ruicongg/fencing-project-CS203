import React, { useState, useEffect } from "react";
import axios from "axios";
import AdminTournamentCard from "./AdminTournamentCard";
import AdminEventsList from "./AdminEventsList";
import AdminEditTournament from "./AdminEditTournament";
import AdminCreateTournament from "./AdminCreateTournament";
import AdminEditEvent from "./AdminEditEvent";
import AdminCreateEvent from "./AdminCreateEvent";
import "../styles/AdminDashboard.css";

axios.defaults.baseURL = "http://localhost:8080";

const AdminDashboard = () => {
  const [activeTournaments, setActiveTournaments] = useState([]);
  const [completedTournaments, setCompletedTournaments] = useState([]);
  const [selectedTournament, setSelectedTournament] = useState(null);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [isEditingTournament, setIsEditingTournament] = useState(false);
  const [isCreatingTournament, setIsCreatingTournament] = useState(false);
  const [isEditingEvent, setIsEditingEvent] = useState(false);
  const [isCreatingEvent, setIsCreatingEvent] = useState(false);
  const [activeTab, setActiveTab] = useState("active");
  const [loading, setLoading] = useState(false);

  const handleError = (error, customMessage) => {
    console.error(customMessage, error);
  };

  // Fetch all tournaments and categorize them
  const fetchTournaments = async () => {
    try {
      setLoading(true);
      const response = await axios.get("/tournaments");
      const now = new Date();
      const active = response.data.filter(
        (t) => new Date(t.tournamentEndDate) > now
      );
      const completed = response.data.filter(
        (t) => new Date(t.tournamentEndDate) <= now
      );
      setActiveTournaments(active);
      setCompletedTournaments(completed);
    } catch (error) {
      handleError(error, "Error fetching tournaments");
    } finally {
      setLoading(false);
    }
  };

  // Fetch events for a specific tournament
  const fetchEventsForTournament = async (tournamentId) => {
    try {
      const response = await axios.get(`/tournaments/${tournamentId}/events`);
      setSelectedTournament((prev) => ({ ...prev, events: response.data }));
    } catch (error) {
      handleError(error, "Error fetching events for the tournament");
    }
  };

  useEffect(() => {
    fetchTournaments();
  }, []);

  const handleTabClick = (tab) => {
    setActiveTab(tab);
    setSelectedTournament(null); // Clear selected tournament on tab change
  };

  const handleDeleteTournament = async (tournamentId) => {
    try {
      await axios.delete(`/tournaments/${tournamentId}`);
      fetchTournaments();
    } catch (error) {
      handleError(error, "Error deleting tournament");
    }
  };

  const handleDeleteEvent = async (eventId) => {
    try {
      await axios.delete(
        `/tournaments/${selectedTournament.id}/events/${eventId}`
      );
      fetchEventsForTournament(selectedTournament.id); // Refresh events list after deletion
    } catch (error) {
      handleError(error, "Error deleting event");
    }
  };

  const handleSave = async (url, data, type) => {
    try {
      await axios.put(url, data);
      fetchTournaments();
      type === "tournament"
        ? setIsEditingTournament(false)
        : setIsEditingEvent(false);
    } catch (error) {
      handleError(error, `Error saving ${type}`);
    }
  };

  const handleAdd = async (url, data, type) => {
    try {
      await axios.post(url, data);

      if (type === "event" && selectedTournament) {
        await fetchEventsForTournament(selectedTournament.id); // Re-fetch events after adding a new event
      } else if (type === "tournament") {
        fetchTournaments();
      }

      type === "tournament"
        ? setIsCreatingTournament(false)
        : setIsCreatingEvent(false);
    } catch (error) {
      handleError(error, `Error adding ${type}`);
    }
  };

  return (
    <div className="admin-dashboard">
      <nav className="breadcrumb">
        <span>Tournaments</span>
      </nav>

      <h1>Admin Dashboard</h1>

      <div className="tabs">
        <button
          onClick={() => handleTabClick("active")}
          className={activeTab === "active" ? "active" : ""}
        >
          Active
        </button>
        <button
          onClick={() => handleTabClick("completed")}
          className={activeTab === "completed" ? "active" : ""}
        >
          Completed
        </button>
      </div>

      {activeTab === "active" && (
        <button
          className="new-tournament-button"
          onClick={() => setIsCreatingTournament(true)}
        >
          + New tournament
        </button>
      )}

      {loading && <p>Loading tournaments...</p>}

      <div className="tournament-cards">
        {!loading &&
          activeTab === "active" &&
          activeTournaments.length === 0 && <p>No active tournaments</p>}
        {!loading &&
          activeTab === "completed" &&
          completedTournaments.length === 0 && <p>No completed tournaments</p>}

        {(activeTab === "active"
          ? activeTournaments
          : completedTournaments
        ).map((tournament) => (
          <AdminTournamentCard
            key={tournament.id}
            tournament={tournament}
            onSelect={() => {
              setSelectedTournament(tournament);
              fetchEventsForTournament(tournament.id); // Fetch events when a tournament is selected
            }}
            onEdit={() => {
              setSelectedTournament(tournament);
              setIsEditingTournament(true);
            }}
            onDelete={() => handleDeleteTournament(tournament.id)}
          />
        ))}
      </div>

      {selectedTournament && (
        <div className="events-section">
          <h2>My events for {selectedTournament.name}</h2>
          {activeTab === "active" && (
            <button
              className="new-event-button"
              onClick={() => setIsCreatingEvent(true)}
            >
              + New event
            </button>
          )}
          <AdminEventsList
            events={selectedTournament.events || []} // Pass the events from selectedTournament
            tournamentId={selectedTournament?.id}
            onEditEvent={(event) => {
              setSelectedEvent(event);
              setIsEditingEvent(true);
            }}
            onDeleteEvent={handleDeleteEvent}
          />
        </div>
      )}

      {/* Modals */}
      {isEditingTournament && (
        <AdminEditTournament
          tournament={selectedTournament}
          onClose={() => setIsEditingTournament(false)}
          onSave={(updatedTournament) =>
            handleSave(
              `/tournaments/${updatedTournament.id}`,
              updatedTournament,
              "tournament"
            )
          }
        />
      )}

      {isCreatingTournament && (
        <AdminCreateTournament
          onClose={() => setIsCreatingTournament(false)}
          onAdd={(newTournament) =>
            handleAdd("/tournaments", newTournament, "tournament")
          }
        />
      )}

      {isEditingEvent && selectedEvent && (
        <AdminEditEvent
          event={selectedEvent}
          onClose={() => setIsEditingEvent(false)}
          onSave={(updatedEvent) =>
            handleSave(
              `/tournaments/${selectedTournament.id}/events/${updatedEvent.id}`,
              updatedEvent,
              "event"
            )
          }
        />
      )}

      {isCreatingEvent && (
        <AdminCreateEvent
          onClose={() => setIsCreatingEvent(false)}
          onAdd={(newEvent) =>
            handleAdd(
              `/tournaments/${selectedTournament.id}/events`,
              newEvent,
              "event"
            )
          }
        />
      )}
    </div>
  );
};

export default AdminDashboard;