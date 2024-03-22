import express from 'express'
import { validationOfCarFields, searchCars, rentCar } from '../controllers/CarsController.js'

const router = express.Router()

//route for the validation of car fields
router.post('/car-booking-exists', validationOfCarFields)

//route for the searching of cars
router.post('/cars', searchCars)

//route for making the rental of car
router.post('/renting-car', rentCar)

export { router as CarsRouter }