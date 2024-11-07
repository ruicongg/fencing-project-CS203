import React, { useState, useEffect } from 'react';
import axios from 'axios';
import EventsList from './UserEventsList';
import '../styles/UserEventsPage.css';

axios.defaults.baseURL = 'http://localhost:8080';

const EventsPage = () => {
  const [activeEvents, setActiveEvents] = useState([]);
  const [completedEvents, setCompletedEvents] = useState([]);
  const [activeTab, setActiveTab] = useState('active');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMyEvents = async () => {
      try {
        const response = await axios.get('/tournaments'); // Make sure this endpoint is implemented
        const now = new Date();

        const active = response.data.filter(event => new Date(event.endDateTime) > now);
        const completed = response.data.filter(event => new Date(event.endDateTime) <= now);

        setActiveEvents(active);
        setCompletedEvents(completed);
      } catch (error) {
        setError('Failed to fetch events.');
        console.error('Error fetching events:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchMyEvents();
  }, []);

  const handleWithdraw = (eventId) => {
    // Filter out the withdrawn event from the active list
    setActiveEvents((prevActiveEvents) =>
      prevActiveEvents.filter((event) => event.id !== eventId)
    );
  };

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  if (loading) {
    return <p>Loading events...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <div className="my-events-page">
      <h1>My Events</h1>

      {/* Tabs */}
      <div className="tabs">
        <button
          onClick={() => handleTabClick('active')}
          className={activeTab === 'active' ? 'active' : ''}
        >
          Active
        </button>
        <button
          onClick={() => handleTabClick('completed')}
          className={activeTab === 'completed' ? 'active' : ''}
        >
          Completed
        </button>
      </div>

      {/* Events List */}
      {activeTab === 'active' ? (
        <EventsList events={activeEvents} showWithdrawButton={true} onWithdraw={handleWithdraw} />
      ) : (
        <EventsList events={completedEvents} showWithdrawButton={false} />
      )}
    </div>
  );
};

export default EventsPage;
