import express from 'express'
import { searchFlights, searchAirports } from '../controllers/FlightsAirportsController.js'

const router = express.Router()

//route for searching airports
router.get('/airports', searchAirports)

//route for searching flights
router.post('/flights', searchFlights)

export { router as FlightsAirportsRouter }