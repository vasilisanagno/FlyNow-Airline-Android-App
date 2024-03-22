import { queryForSearchingPet, queryForUpdatingPet } from '../services/PetsService.js'

//function that searchs the pet that is in a specific reservation
export const searchPets = async (req, res) => {
    const jsonArray = req.body
	
	try {
		const result = await queryForSearchingPet(jsonArray[0].bookingid.toUpperCase())
		
		if(result.rows[0].petsize == null) {
			result.rows[0].petsize = ""
		}
		res.json([{ petSize: result.rows[0].petsize}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that updates the pet that is in a specific reservation
export const updatePets = async (req, res) => {
    const jsonArray = req.body
	
	try {
		await queryForUpdatingPet(jsonArray[0].petSize, jsonArray[0].price, jsonArray[0].bookingid.toUpperCase())
	
		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}