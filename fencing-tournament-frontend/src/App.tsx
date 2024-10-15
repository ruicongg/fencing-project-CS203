import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import Tournaments from './pages/Tournaments'
import Events from './pages/Events'
import Players from './pages/Players'
import './App.css'

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <main className="container">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/tournaments" element={<Tournaments />} />
            <Route path="/events" element={<Events />} />
            <Route path="/players" element={<Players />} />
          </Routes>
        </main>
      </div>
    </Router>
  )
}

export default App
