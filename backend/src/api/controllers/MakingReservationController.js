import { queryForOccupiedSeats, queryForAirplaneCapacity,
    insertPassengers, insertReservation, insertInHasTable, checkIfSeatsAreOk } from '../services/MakingReservationService.js'

//function that returns the not available seats in a flight
export const findNotAvailableSeats = async (req, res) => {
    const jsonArray = req.body

	try {
		const result = await queryForOccupiedSeats(jsonArray[0].flightId)
		
		res.json(result.rows)
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that returns the airplane capacity according to the airplane model
export const findAirplaneCapacity = async (req, res) => {
    const jsonArray = req.body

	try {
		const result = await queryForAirplaneCapacity(jsonArray[0].airplaneModel)
		
		res.json(result.rows)
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that inserts a new booking/reservation in the database
export const makeNewBooking = async (req, res) => {
	const jsonArray = req.body
	let passengersId, reservationId

    try {
		const result = await checkIfSeatsAreOk(jsonArray[0].seats)
		if(result) {
			passengersId = await insertPassengers(jsonArray[0].passengers)
			reservationId = await insertReservation(jsonArray[0].petSize,jsonArray[0].price)
			await insertInHasTable(jsonArray[0].seats,jsonArray[0].baggage,jsonArray[0].passengers,
				passengersId,reservationId,jsonArray[0].classTypeOutbound,jsonArray[0].classTypeInbound
			)
			res.json([{ success: true, bookingId: reservationId}])
		}
		else {
			res.json([{ success: false, bookingId: reservationId}])
		}
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}
