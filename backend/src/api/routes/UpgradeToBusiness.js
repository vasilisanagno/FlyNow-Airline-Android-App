import express from 'express'
import { searchClass, updateClass } from '../controllers/UpgradeToBusinessController.js'

const router = express.Router()

//route for searching the current class of a specific reservation
router.post('/upgrade-to-business', searchClass)

//route for updating class to business in a specific reservation
router.post('/update-business', updateClass)

export { router as UpgradeToBusinessRouter }