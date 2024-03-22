import express from 'express'
import { checkBooking, searchBookingDetails, deleteBooking } from '../controllers/BookingController.js'

const router = express.Router()

//route for checking the booking if exists
router.post('/check-booking', checkBooking)

//route for searching the booking details of a reservation
router.post('/booking-details', searchBookingDetails)

//route for deleting the booking
router.post('/delete-booking', deleteBooking)

export { router as BookingRouter }