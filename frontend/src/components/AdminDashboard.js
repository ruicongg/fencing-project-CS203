import React, { useState, useEffect } from "react";
import axios from "axios";
import AdminTournamentCard from "./AdminTournamentCard";
import AdminEventsList from "./AdminEventsList";
import AdminEditTournament from "./AdminEditTournament";
import AdminCreateTournament from "./AdminCreateTournament";
import AdminEditEvent from "./AdminEditEvent";
import AdminCreateEvent from "./AdminCreateEvent";
import "../styles/shared/index.css";

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
    <div className="dashboard">
      
      {/* Breadcrumb Navigation */}
      <nav className="breadcrumb">
        <span className="separator">/</span>
        <a className="active">Tournaments</a>
      </nav>
  
      {/* Dashboard Title */}
      <h1 className="dashboard-title">Admin Dashboard</h1>
  
      {/* Tabs for Active and Completed Tournaments */}
      <div className="tabs">
        <button 
          onClick={() => handleTabClick("active")}
          className={`tab ${activeTab === "active" ? "active" : ""}`}
        >
          Active
        </button>
        <button
          onClick={() => handleTabClick("completed")}
          className={`tab ${activeTab === "completed" ? "active" : ""}`}
        >
          Completed
        </button>
      </div>
  
      {/* Section for Tournaments */}
      <div className="section-container">
        {activeTab === "active" && (
          <button
            className="new-tournament-button"
            onClick={() => setIsCreatingTournament(true)}
          >
            + New tournament
          </button>
        )}
  
        {/* Loading Indicator */}
        {loading && <p className="loading-message">Loading tournaments...</p>}
  
        {/* Tournament Cards */}
        <div className="tournament-list-container">
          {!loading &&
            activeTab === "active" &&
            activeTournaments.length === 0 && <p className="no-tournaments-message">No active tournaments</p>}
          {!loading &&
            activeTab === "completed" &&
            completedTournaments.length === 0 && <p className="no-tournaments-message">No completed tournaments</p>}
    
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
      
  
      {/* Events Section for Selected Tournament */}
      {selectedTournament && (
        
          <div className="">
            <h2 className="section-title">My events for {selectedTournament.name}</h2>
            {activeTab === "active" && (
              <button
                className="add-button"
                onClick={() => setIsCreatingEvent(true)}
              >
                + New event
              </button>
            )}
            <p></p>
          
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
          tournamentId={selectedTournament.id}
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
          tournamentId={selectedTournament.id}
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
    </div>
  );
  
}

export default AdminDashboard;