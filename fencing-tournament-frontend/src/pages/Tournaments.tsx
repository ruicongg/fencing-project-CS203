import { useState, useEffect } from 'react'
import { getTournaments, addTournament } from '../api/tournamentApi'
import TournamentList from '../components/TournamentList'
import TournamentForm from '../components/TournamentForm'
import './Tournaments.css'

const Tournaments = () => {
  const [tournaments, setTournaments] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetchTournaments()
  }, [])

  const fetchTournaments = async () => {
    try {
      setIsLoading(true)
      const data = await getTournaments()
      setTournaments(data)
      setIsLoading(false)
    } catch (err) {
      setError('Failed to fetch tournaments')
      setIsLoading(false)
    }
  }

  const handleAddTournament = async (tournamentData) => {
    try {
      await addTournament(tournamentData)
      fetchTournaments()
    } catch (err) {
      setError('Failed to add tournament')
    }
  }

  if (isLoading) return <div className="loading">Loading...</div>
  if (error) return <div className="error">{error}</div>

  return (
    <div className="tournaments-page">
      <h1>Tournaments</h1>
      <TournamentForm onSubmit={handleAddTournament} />
      <TournamentList tournaments={tournaments} />
    </div>
  )
}

export default Tournaments