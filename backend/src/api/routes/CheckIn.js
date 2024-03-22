import express from 'express'
import { searchCheckInDetails, updateCheckIn } from '../controllers/CheckInController.js'

const router = express.Router()

//route for searching check-in details in a specific reservation
router.post('/checkin-details', searchCheckInDetails)

//route for updating the check-in for all passengers in a specific reservation
router.post('/update-checkin', updateCheckIn)

export { router as CheckInRouter }