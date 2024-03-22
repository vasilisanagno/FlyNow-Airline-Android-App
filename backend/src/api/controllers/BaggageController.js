import { queriesForSearchingBaggage, queriesForUpdatingBaggage } from '../services/BaggageService.js'

//function that searchs the baggage inside a reservation for each passenger
export const searchBaggage = async (req, res) => {
    const jsonArray = req.body

	let passengers = []
	
	try {
		passengers = await queriesForSearchingBaggage(jsonArray[0].bookingId.toUpperCase())
		
		res.json(passengers)
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that updates the baggage in a reservation for the passsengers
export const updateBaggage = async (req, res) => {
    const jsonArray = req.body
	
	try {
		await queriesForUpdatingBaggage(jsonArray)
		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}