import { queryOneWayFlights, queryReturnFlights, queryAirports } from '../services/FlightsAirportsService.js'

//function that searchs all airports
export const searchAirports = async (req, res) => {
    try {
		const result = await queryAirports()
		res.json(result.rows)
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}

//function that returns the flights with the previous functions queryOneWayFlights() and queryReturnFlights()
//and takes from the departure and arrival time the hours and minutes like '19:20'
export const searchFlights = async (req,res) => {
    const jsonArray = req.body
	let oneWayDirectResult = null, returnDirectResult = null,
	oneWayOneStopResult = null, returnOneStopResult = null
	
    try {
		[oneWayDirectResult, oneWayOneStopResult] = await queryOneWayFlights(jsonArray)
		if(jsonArray[0].returnDate!="") { 
			[returnDirectResult, returnOneStopResult]  = await queryReturnFlights(jsonArray)
		}
		if(oneWayDirectResult!=null&&oneWayDirectResult.length!=0) {
			for(let flight of oneWayDirectResult) {
				// Extracting only the time part from departure and arrival times hh:mm
				flight.departuretime = flight.departuretime.substring(0, 5)
				flight.arrivaltime = flight.arrivaltime.substring(0, 5)
			}
		}
		if(oneWayOneStopResult!=null&&oneWayOneStopResult.length!=0) {
			for(let flight of oneWayOneStopResult) {
				// Extracting only the time part from departure and arrival times hh:mm
				flight.first_flight_departuretime = flight.first_flight_departuretime.substring(0, 5)
				flight.second_flight_departuretime = flight.second_flight_departuretime.substring(0, 5)
				flight.first_flight_arrivaltime = flight.first_flight_arrivaltime.substring(0, 5)
				flight.second_flight_arrivaltime = flight.second_flight_arrivaltime.substring(0, 5)
			}
		}
		if(returnDirectResult!=null&&returnDirectResult.length!=0) {
			for(let flight of returnDirectResult) {
				// Extracting only the time part from departure and arrival times hh:mm
				flight.departuretime = flight.departuretime.substring(0, 5)
				flight.arrivaltime = flight.arrivaltime.substring(0, 5)
			}
		}
		if(returnOneStopResult!=null&&returnOneStopResult.length!=0) {
			for(let flight of returnOneStopResult) {
				// Extracting only the time part from departure and arrival times hh:mm
				flight.first_flight_departuretime = flight.first_flight_departuretime.substring(0, 5)
				flight.second_flight_departuretime = flight.second_flight_departuretime.substring(0, 5)
				flight.first_flight_arrivaltime = flight.first_flight_arrivaltime.substring(0, 5)
				flight.second_flight_arrivaltime = flight.second_flight_arrivaltime.substring(0, 5)
			}
		}

		res.json([{"oneWayDirectResult": oneWayDirectResult},{"oneWayOneStopResult": oneWayOneStopResult},
		{"returnDirectResult": returnDirectResult},{"returnOneStopResult": returnOneStopResult}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
}