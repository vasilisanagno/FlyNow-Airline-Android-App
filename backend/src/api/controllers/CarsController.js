import { queriesForValidationOfCarData, queryForSearchingCars, insertNewRentalOfCar } from '../services/CarsService.js'

//function to validate the data that user typed in the fields
export const validationOfCarFields = async (req, res) => {
    const jsonArray = req.body

	try {
		const returnJson = await queriesForValidationOfCarData(jsonArray)
		
		res.json([returnJson])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that searchs cars and decode the bytes of the car image 
export const searchCars = async (req, res) => {
	const jsonArray = req.body
	
    try {
		const result = await queryForSearchingCars(jsonArray)
		let json = []
		for(let i=0; i<result.rows.length; i++) {
			const imageData = result.rows[i].carimage
			// Convert the bytea data to base64
			const base64ImageData = imageData.toString('base64')
			json.push({
				carimage: base64ImageData,
				company: result.rows[i].company,
				model: result.rows[i].model,
				price: result.rows[i].price,
				carid: result.rows[i].carid
			})
		}
        // Send the base64-encoded image data in the response with company and model of cars
        res.json(json)
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that makes a new rental of car
export const rentCar = async (req, res) => {
	const jsonArray = req.body
	
    try {
		await insertNewRentalOfCar(jsonArray)

        res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}
