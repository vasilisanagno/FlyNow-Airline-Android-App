import express from 'express'
import { searchBaggage, updateBaggage } from '../controllers/BaggageController.js'

const router = express.Router()

//route for searching baggage in a specific reservation
router.post('/baggage-from-more', searchBaggage)

//route for updating baggage in a specific reservation
router.post('/update-baggage', updateBaggage)

export { router as BaggageRouter }