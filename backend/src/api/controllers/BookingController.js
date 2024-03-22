import { queriesForDeletingBooking, queriesForRetrievingBookingDetails, queryForCheckingBooking } from '../services/BookingService.js'

//function that checking the booking amnd returns true or false accordingly
export const checkBooking = async (req, res) => {
    const jsonArray = req.body

	try {
		const result = await queryForCheckingBooking(jsonArray)
		if(result.rows[0] != null){
			res.json([{ success: true}])
		}
		else{
			res.json([{ success: false}])
		}
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that calls the service function to make query and get all the details about a booking
export const searchBookingDetails = async (req, res) => {
    const jsonArray = req.body
		
	try {
		const jsonResponse = await queriesForRetrievingBookingDetails(jsonArray)

        res.json(jsonResponse)
	} catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
	}
}

//function that deletes the info relating to the reservation
export const deleteBooking = async (req, res) => {
	const jsonArray = req.body

    try {
		await queriesForDeletingBooking(jsonArray[0].bookingId.toUpperCase())
		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}