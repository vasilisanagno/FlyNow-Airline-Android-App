import { queryForSearchingWifi, queryForUpdatingWifi } from '../services/WifiService.js'

//function that searchs the wifi-package inside a reservation
export const searchWifi = async (req, res) => {
    const jsonArray = req.body
	
	try {
		const result = await queryForSearchingWifi(jsonArray[0].bookingid.toUpperCase())
	
		res.json([{ wifiOnBoard: result.rows[0].wifionboard}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that updates the wifi-package inside a reservation
export const updateWifi = async (req, res) => {
    const jsonArray = req.body
	
	try {
		await queryForUpdatingWifi(jsonArray[0].wifiOnBoard,jsonArray[0].price,jsonArray[0].bookingid.toUpperCase())
	
		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}