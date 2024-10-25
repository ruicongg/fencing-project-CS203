import { useState, useEffect } from 'react'
import './Players.css'

interface Player {
  id: number
  username: string
  email: string
  elo: number
}

const Players = () => {
  const [players, setPlayers] = useState<Player[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchPlayers()
  }, [])

  const fetchPlayers = async () => {
    try {
      setIsLoading(true)
      // Replace with your actual API call
      const response = await fetch('http://localhost:8080/players')
      if (!response.ok) {
        throw new Error('Failed to fetch players')
      }
      const data = await response.json()
      setPlayers(data)
      setIsLoading(false)
    } catch (err) {
      setError('Failed to fetch players')
      setIsLoading(false)
    }
  }

  if (isLoading) return <div className="loading">Loading...</div>
  if (error) return <div className="error">{error}</div>

  return (
    <div className="players-page">
      <h1>Players</h1>
      {players.length === 0 ? (
        <p>No players available.</p>
      ) : (
        <ul className="player-list">
          {players.map((player) => (
            <li key={player.id} className="player-item">
              <h3>{player.username}</h3>
              <p>Email: {player.email}</p>
              <p>ELO: {player.elo}</p>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default Players