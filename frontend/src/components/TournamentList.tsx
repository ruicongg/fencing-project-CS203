import React from 'react'
import './TournamentList.css'

interface Tournament {
  id: number
  name: string
  tournamentStartDate: string
  tournamentEndDate: string
  venue: string
}

interface TournamentListProps {
  tournaments: Tournament[]
}

const TournamentList: React.FC<TournamentListProps> = ({ tournaments }) => {
  return (
    <div className="tournament-list">
      <h2>Tournaments</h2>
      {tournaments.length === 0 ? (
        <p>No tournaments available.</p>
      ) : (
        <ul>
          {tournaments.map((tournament) => (
            <li key={tournament.id} className="tournament-item">
              <h3>{tournament.name}</h3>
              <p>Start Date: {new Date(tournament.tournamentStartDate).toLocaleDateString()}</p>
              <p>End Date: {new Date(tournament.tournamentEndDate).toLocaleDateString()}</p>
              <p>Venue: {tournament.venue}</p>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default TournamentList