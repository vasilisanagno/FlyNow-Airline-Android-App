import { queriesForSearchingCheckInDetails, queryForUpdatingCheckIn } from '../services/CheckInService.js'

//function to search all the necessary details for the check-in of a specific reservation
export const searchCheckInDetails = async (req, res) => {
	const jsonArray = req.body
	
	try {
		const jsonResponse = await queriesForSearchingCheckInDetails(jsonArray)

        res.json(jsonResponse)
	} catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
	}
}

//function to update the check-in for all passengers in a reservation for one stop or direct flights
export const updateCheckIn = async (req, res) => {
    const jsonArray = req.body
	
	try {
        const result = await queryForUpdatingCheckIn(jsonArray)
        if(result===true) {
            res.json([{ success: true}])
        }
        else {
            res.json([{ success: false}])
        }
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}
