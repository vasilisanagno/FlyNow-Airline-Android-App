import { queriesForSearchingClass, queriesForUpdatingClass } from '../services/UpgradeToBusinessService.js'

//function that searchs the class of the reservation
export const searchClass = async (req, res) => {
    const jsonArray = req.body
	
	try {
		const returnJson = await queriesForSearchingClass(jsonArray)
		
		res.json([returnJson])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that updates the class of the reservation in business class
export const updateClass = async (req, res) => {
    const jsonArray = req.body
	
	try {
		await queriesForUpdatingClass(jsonArray)

		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}