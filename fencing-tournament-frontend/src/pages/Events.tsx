import { useState, useEffect } from 'react'
import './Events.css'

interface Event {
  id: number
  name: string
  startDate: string
  endDate: string
  gender: string
  weapon: string
}

const Events = () => {
  const [events, setEvents] = useState<Event[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchEvents()
  }, [])

  const fetchEvents = async () => {
    try {
      setIsLoading(true)
      // Replace with your actual API call
      const response = await fetch('http://localhost:8080/events')
      if (!response.ok) {
        throw new Error('Failed to fetch events')
      }
      const data = await response.json()
      setEvents(data)
      setIsLoading(false)
    } catch (err) {
      setError('Failed to fetch events')
      setIsLoading(false)
    }
  }

  if (isLoading) return <div className="loading">Loading...</div>
  if (error) return <div className="error">{error}</div>

  return (
    <div className="events-page">
      <h1>Events</h1>
      {events.length === 0 ? (
        <p>No events available.</p>
      ) : (
        <ul className="event-list">
          {events.map((event) => (
            <li key={event.id} className="event-item">
              <h3>{event.name}</h3>
              <p>Start Date: {new Date(event.startDate).toLocaleDateString()}</p>
              <p>End Date: {new Date(event.endDate).toLocaleDateString()}</p>
              <p>Gender: {event.gender}</p>
              <p>Weapon: {event.weapon}</p>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default Events