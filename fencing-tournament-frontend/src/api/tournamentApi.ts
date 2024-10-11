const API_BASE_URL = 'http://localhost:8080'

export const getTournaments = async () => {
  const response = await fetch(`${API_BASE_URL}/tournaments`)
  if (!response.ok) {
    throw new Error('Failed to fetch tournaments')
  }
  return response.json()
}

export const addTournament = async (tournamentData) => {
  const response = await fetch(`${API_BASE_URL}/tournaments`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(tournamentData),
  })
  if (!response.ok) {
    throw new Error('Failed to add tournament')
  }
  return response.json()
}