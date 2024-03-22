import express from 'express'
import { findNotAvailableSeats, findAirplaneCapacity, makeNewBooking } from '../controllers/MakingReservationController.js'

const router = express.Router()

//route for the occupied seats
router.post('/seats', findNotAvailableSeats)

//route for the airplane capacity
router.post('/airplane-capacity', findAirplaneCapacity)

//route for making new booking
router.post('/new-booking', makeNewBooking)

export { router as MakingReservationRouter }