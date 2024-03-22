import express from 'express'
import { searchPets, updatePets } from '../controllers/PetsController.js'

const router = express.Router()

//route for searching pet in a specific reservation
router.post('/pets-from-more', searchPets)

//route for updating pet in a specific reservation
router.post('/update-pets', updatePets)

export { router as PetsRouter }