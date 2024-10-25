import { useState } from 'react'
import './TournamentForm.css'

const TournamentForm = ({ onSubmit }) => {
  const [formData, setFormData] = useState({
    name: '',
    tournamentStartDate: '',
    tournamentEndDate: '',
    registrationStartDate: '',
    registrationEndDate: '',
    venue: '',
  })

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    onSubmit(formData)
    setFormData({
      name: '',
      tournamentStartDate: '',
      tournamentEndDate: '',
      registrationStartDate: '',
      registrationEndDate: '',
      venue: '',
    })
  }

  return (
    <form className="tournament-form" onSubmit={handleSubmit}>
      <h2>Add New Tournament</h2>
      <input
        type="text"
        name="name"
        value={formData.name}
        onChange={handleChange}
        placeholder="Tournament Name"
        required
      />
      <input
        type="date"
        name="tournamentStartDate"
        value={formData.tournamentStartDate}
        onChange={handleChange}
        required
      />
      <input
        type="date"
        name="tournamentEndDate"
        value={formData.tournamentEndDate}
        onChange={handleChange}
        required
      />
      <input
        type="date"
        name="registrationStartDate"
        value={formData.registrationStartDate}
        onChange={handleChange}
        required
      />
      <input
        type="date"
        name="registrationEndDate"
        value={formData.registrationEndDate}
        onChange={handleChange}
        required
      />
      <input
        type="text"
        name="venue"
        value={formData.venue}
        onChange={handleChange}
        placeholder="Venue"
        required
      />
    </form>
  )
}

export default TournamentForm