import { pool } from '../../config/DatabaseConfig.js'

//function that makes query to find the airports that are saved in database in ascending order
export async function queryAirports() {
	const result = await pool.query('SELECT * FROM airport ORDER BY city ASC')
	return result
}

//function that makes query to find the flights for one way in a destination
//and see if the restriction am flights or pm flights is true or the restriction direct
//otherwise returns both direct and one stop flights
//also checks if the number of passengers that select the user for the reservation is less or equal than
//the capacity of the airplane - the seats that is not available
export async function queryOneWayFlights(jsonArray) {
	let result, result1, rows = null, rows1 = null

	if(jsonArray[0].amFlights&&!jsonArray[0].pmFlights) {
		result = await pool.query(`
		SELECT f.flightid, TO_CHAR(f.flightdate, 'DD/MM/YYYY') AS flightdate, f.departuretime, f.arrivaltime, 
		f.economyprice, f.flexprice, f.businessprice, f.departureairport, 
		f.arrivalairport, a1.city as departurecity, a2.city as arrivalcity, 
		ap.model as airplanemodel, fd.duration AS flightduration
		FROM flight f, airport a1, airport a2, airplane ap, flight_duration fd, model m
		WHERE f.departuretime=fd.departuretime
		AND f.arrivaltime=fd.arrivaltime
		AND f.departureairport=a1.name
		AND f.arrivalairport=a2.name
		AND ap.airplaneid=f.airplane
		AND m.modelid=ap.model
		AND f.departureairport='${jsonArray[0].from}' 
		AND f.arrivalairport='${jsonArray[0].to}'
		AND f.flightdate=TO_DATE('${jsonArray[0].departureDate}', 'DD/MM/YYYY')
		AND EXTRACT(HOUR FROM f.departuretime) * 60 + EXTRACT(MINUTE FROM f.departuretime) >= 0
		AND EXTRACT(HOUR FROM f.departuretime) * 60 + EXTRACT(MINUTE FROM f.departuretime) <= 11 * 60 + 59
		AND ${jsonArray[0].passengersCount} <= m.capacity - (
			SELECT COUNT(DISTINCT(h.seatnumber))
			FROM has h
			WHERE f.flightid=h.flightid)
		`)

		rows = result.rows
	}
	else if(jsonArray[0].pmFlights&&!jsonArray[0].amFlights) {
		result = await pool.query(`
		SELECT f.flightid, TO_CHAR(f.flightdate, 'DD/MM/YYYY') AS flightdate, f.departuretime, f.arrivaltime, 
		f.economyprice, f.flexprice, f.businessprice, f.departureairport, 
		f.arrivalairport, a1.city as departurecity, a2.city as arrivalcity, 
		ap.model as airplanemodel, fd.duration AS flightduration
		FROM flight f, airport a1, airport a2, airplane ap, flight_duration fd, model m
		WHERE f.departuretime=fd.departuretime
		AND f.arrivaltime=fd.arrivaltime
		AND f.departureairport=a1.name
		AND f.arrivalairport=a2.name
		AND ap.airplaneid=f.airplane
		AND m.modelid=ap.model
		AND departureairport='${jsonArray[0].from}' 
		AND arrivalairport='${jsonArray[0].to}'
		AND flightdate=TO_DATE('${jsonArray[0].departureDate}', 'DD/MM/YYYY')
		AND EXTRACT(HOUR FROM f.departuretime) * 60 + EXTRACT(MINUTE FROM f.departuretime) >= 12 * 60
		AND EXTRACT(HOUR FROM f.departuretime) * 60 + EXTRACT(MINUTE FROM f.departuretime) <= 23 * 60 + 59
		AND ${jsonArray[0].passengersCount} <= m.capacity - (
			SELECT COUNT(DISTINCT(h.seatnumber))
			FROM has h
			WHERE f.flightid=h.flightid)
		`)

		rows = result.rows
	}
	else {
		result = await pool.query(`
		SELECT f.flightid, TO_CHAR(f.flightdate, 'DD/MM/YYYY') AS flightdate, f.departuretime, f.arrivaltime, 
		f.economyprice, f.flexprice, f.businessprice, f.departureairport, 
		f.arrivalairport, a1.city as departurecity, a2.city as arrivalcity, 
		ap.model as airplanemodel, fd.duration AS flightduration
		FROM flight f, airport a1, airport a2, airplane ap, flight_duration fd, model m
		WHERE f.departuretime=fd.departuretime
		AND f.arrivaltime=fd.arrivaltime
		AND f.departureairport=a1.name
		AND f.arrivalairport=a2.name
		AND ap.airplaneid=f.airplane
		AND m.modelid=ap.model
		AND departureairport='${jsonArray[0].from}' 
		AND arrivalairport='${jsonArray[0].to}'
		AND flightdate=TO_DATE('${jsonArray[0].departureDate}', 'DD/MM/YYYY')
		AND ${jsonArray[0].passengersCount} <= m.capacity - (
			SELECT COUNT(DISTINCT(h.seatnumber))
			FROM has h
			WHERE f.flightid=h.flightid)
		`)

		rows = result.rows

		if(!jsonArray[0].directFlights) {

			result1 = await pool.query(`
			SELECT 
			f1.flightid AS first_flightid, 
			TO_CHAR(f1.flightdate, 'DD/MM/YYYY') AS first_flightdate, 
			f1.departuretime AS first_flight_departuretime, 
			f1.arrivaltime AS first_flight_arrivaltime, 
			f1.economyprice AS first_flight_economyprice, 
			f1.flexprice AS first_flight_flexprice, 
			f1.businessprice AS first_flight_businessprice, 
			f1.departureairport AS first_flight_departureairport, 
			f1.arrivalairport AS first_flight_arrivalairport, 
			a1.city AS first_flight_departurecity, 
			a2.city AS first_flight_arrivalcity, 
			ap1.model AS first_flight_airplanemodel, 
			fd1.duration AS first_flightduration,
			
			f2.flightid AS second_flightid, 
			TO_CHAR(f2.flightdate, 'DD/MM/YYYY') AS second_flightdate, 
			f2.departuretime AS second_flight_departuretime, 
			f2.arrivaltime AS second_flight_arrivaltime, 
			f2.economyprice AS second_flight_economyprice, 
			f2.flexprice AS second_flight_flexprice, 
			f2.businessprice AS second_flight_businessprice, 
			f2.departureairport AS second_flight_departureairport, 
			f2.arrivalairport AS second_flight_arrivalairport, 
			a3.city AS second_flight_departurecity, 
			a4.city AS second_flight_arrivalcity, 
			ap2.model AS second_flight_airplanemodel, 
			fd2.duration AS second_flightduration

			FROM 
			flight f1
			JOIN airport a1 ON f1.departureairport = a1.name
			JOIN airport a2 ON f1.arrivalairport = a2.name
			JOIN airplane ap1 ON ap1.airplaneid = f1.airplane
			JOIN flight_duration fd1 ON f1.departuretime = fd1.departuretime AND f1.arrivaltime = fd1.arrivaltime
			JOIN flight f2 ON f2.departureairport = a2.name
			JOIN airport a3 ON f2.departureairport = a3.name
			JOIN airport a4 ON f2.arrivalairport = a4.name
			JOIN airplane ap2 ON ap2.airplaneid = f2.airplane
			JOIN flight_duration fd2 ON f2.departuretime = fd2.departuretime AND f2.arrivaltime = fd2.arrivaltime
			JOIN model m1 ON m1.modelid=ap1.model
			JOIN model m2 ON m2.modelid=ap2.model

			WHERE 
			f1.departureairport = '${jsonArray[0].from}'
			AND f1.arrivalairport = f2.departureairport
			AND f2.arrivalairport = '${jsonArray[0].to}'
			AND f1.flightdate = TO_DATE('${jsonArray[0].departureDate}', 'DD/MM/YYYY')
			AND f2.flightdate = f1.flightdate
			AND EXTRACT(HOUR FROM f2.departuretime) * 60 + EXTRACT(MINUTE FROM f2.departuretime) > 
			EXTRACT(HOUR FROM f1.arrivaltime) * 60 + EXTRACT(MINUTE FROM f1.arrivaltime)
			AND ${jsonArray[0].passengersCount} <= m1.capacity - (
				SELECT COUNT(DISTINCT(h.seatnumber))
				FROM has h
				WHERE f1.flightid=h.flightid)
			AND ${jsonArray[0].passengersCount} <= m2.capacity - (
				SELECT COUNT(DISTINCT(h.seatnumber))
				FROM has h
				WHERE f2.flightid=h.flightid)
			`)

			rows1 = result1.rows
		}
	}

	return [rows, rows1]
}

//function that makes query to find the flights if the user has selected flights with return(so query for the return flights)
//in a destination and see if the restriction am flights or pm flights is true or the restriction direct
//otherwise returns both direct and one stop flights
//also checks if the number of passengers that select the user for the reservation is less or equal than
//the capacity of the airplane - the seats that is not available
export async function queryReturnFlights(jsonArray) {
	let result, result1, rows = null, rows1 = null

	if(jsonArray[0].amFlights&&!jsonArray[0].pmFlights) {
		result = await pool.query(`
		SELECT f.flightid, TO_CHAR(f.flightdate, 'DD/MM/YYYY') AS flightdate, f.departuretime, f.arrivaltime, 
		f.economyprice, f.flexprice, f.businessprice, f.departureairport, 
		f.arrivalairport, a1.city as departurecity, a2.city as arrivalcity, 
		ap.model as airplanemodel, fd.duration AS flightduration
		FROM flight f, airport a1, airport a2, airplane ap, flight_duration fd, model m
		WHERE f.departuretime=fd.departuretime
		AND f.arrivaltime=fd.arrivaltime
		AND f.departureairport=a1.name
		AND f.arrivalairport=a2.name
		AND ap.airplaneid=f.airplane
		AND m.modelid=ap.model
		AND f.departureairport='${jsonArray[0].to}' 
		AND f.arrivalairport='${jsonArray[0].from}'
		AND f.flightdate=TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY')
		AND EXTRACT(HOUR FROM f.departuretime) * 60 + EXTRACT(MINUTE FROM f.departuretime) >= 0
		AND EXTRACT(HOUR FROM f.departuretime) * 60 + EXTRACT(MINUTE FROM f.departuretime) <= 11 * 60 + 59
		AND ${jsonArray[0].passengersCount} <= m.capacity - (
			SELECT COUNT(DISTINCT(h.seatnumber))
			FROM has h
			WHERE f.flightid=h.flightid)
		`)

		rows = result.rows
	}
	else if(jsonArray[0].pmFlights&&!jsonArray[0].amFlights) {
		result = await pool.query(`
		SELECT f.flightid, TO_CHAR(f.flightdate, 'DD/MM/YYYY') AS flightdate, f.departuretime, f.arrivaltime, 
		f.economyprice, f.flexprice, f.businessprice, f.departureairport, 
		f.arrivalairport, a1.city as departurecity, a2.city as arrivalcity, 
		ap.model as airplanemodel, fd.duration AS flightduration
		FROM flight f, airport a1, airport a2, airplane ap, flight_duration fd, model m
		WHERE f.departuretime=fd.departuretime
		AND f.arrivaltime=fd.arrivaltime
		AND f.departureairport=a1.name
		AND f.arrivalairport=a2.name
		AND ap.airplaneid=f.airplane
		AND m.modelid=ap.model
		AND f.departureairport='${jsonArray[0].to}' 
		AND f.arrivalairport='${jsonArray[0].from}'
		AND f.flightdate=TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY')
		AND EXTRACT(HOUR FROM f.departuretime) * 60 + EXTRACT(MINUTE FROM f.departuretime) >= 12 * 60
		AND EXTRACT(HOUR FROM f.departuretime) * 60 + EXTRACT(MINUTE FROM f.departuretime) <= 23 * 60 + 59
		AND ${jsonArray[0].passengersCount} <= m.capacity - (
			SELECT COUNT(DISTINCT(h.seatnumber))
			FROM has h
			WHERE f.flightid=h.flightid)
		`)

		rows = result.rows
	}
	else {
		
		result = await pool.query(`
		SELECT f.flightid, TO_CHAR(f.flightdate, 'DD/MM/YYYY') AS flightdate, f.departuretime, f.arrivaltime, 
		f.economyprice, f.flexprice, f.businessprice, f.departureairport, 
		f.arrivalairport, a1.city as departurecity, a2.city as arrivalcity, 
		ap.model as airplanemodel, fd.duration AS flightduration
		FROM flight f, airport a1, airport a2, airplane ap, flight_duration fd, model m
		WHERE f.departuretime=fd.departuretime
		AND f.arrivaltime=fd.arrivaltime
		AND f.departureairport=a1.name
		AND f.arrivalairport=a2.name
		AND ap.airplaneid=f.airplane
		AND m.modelid=ap.model
		AND departureairport='${jsonArray[0].to}' 
		AND arrivalairport='${jsonArray[0].from}'
		AND flightdate=TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY')
		AND ${jsonArray[0].passengersCount} <= m.capacity - (
			SELECT COUNT(DISTINCT(h.seatnumber))
			FROM has h
			WHERE f.flightid=h.flightid)
		`)

		rows = result.rows

		if(!jsonArray[0].directFlights) {
			
			result1 = await pool.query(`
			SELECT 
			f1.flightid AS first_flightid, 
			TO_CHAR(f1.flightdate, 'DD/MM/YYYY') AS first_flightdate, 
			f1.departuretime AS first_flight_departuretime, 
			f1.arrivaltime AS first_flight_arrivaltime, 
			f1.economyprice AS first_flight_economyprice, 
			f1.flexprice AS first_flight_flexprice, 
			f1.businessprice AS first_flight_businessprice, 
			f1.departureairport AS first_flight_departureairport, 
			f1.arrivalairport AS first_flight_arrivalairport, 
			a1.city AS first_flight_departurecity, 
			a2.city AS first_flight_arrivalcity, 
			ap1.model AS first_flight_airplanemodel, 
			fd1.duration AS first_flightduration,
			
			f2.flightid AS second_flightid, 
			TO_CHAR(f2.flightdate, 'DD/MM/YYYY') AS second_flightdate, 
			f2.departuretime AS second_flight_departuretime, 
			f2.arrivaltime AS second_flight_arrivaltime, 
			f2.economyprice AS second_flight_economyprice, 
			f2.flexprice AS second_flight_flexprice, 
			f2.businessprice AS second_flight_businessprice, 
			f2.departureairport AS second_flight_departureairport, 
			f2.arrivalairport AS second_flight_arrivalairport, 
			a3.city AS second_flight_departurecity, 
			a4.city AS second_flight_arrivalcity, 
			ap2.model AS second_flight_airplanemodel, 
			fd2.duration AS second_flightduration

			FROM 
			flight f1
			JOIN airport a1 ON f1.departureairport = a1.name
			JOIN airport a2 ON f1.arrivalairport = a2.name
			JOIN airplane ap1 ON ap1.airplaneid = f1.airplane
			JOIN flight_duration fd1 ON f1.departuretime = fd1.departuretime AND f1.arrivaltime = fd1.arrivaltime
			JOIN flight f2 ON f2.departureairport = a2.name
			JOIN airport a3 ON f2.departureairport = a3.name
			JOIN airport a4 ON f2.arrivalairport = a4.name
			JOIN airplane ap2 ON ap2.airplaneid = f2.airplane
			JOIN flight_duration fd2 ON f2.departuretime = fd2.departuretime AND f2.arrivaltime = fd2.arrivaltime
			JOIN model m1 ON m1.modelid=ap1.model
			JOIN model m2 ON m2.modelid=ap2.model

			WHERE 
			f1.departureairport = '${jsonArray[0].to}'
			AND f1.arrivalairport = f2.departureairport
			AND f2.arrivalairport = '${jsonArray[0].from}'
			AND f1.flightdate = TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY')
			AND f2.flightdate = f1.flightdate
			AND EXTRACT(HOUR FROM f2.departuretime) * 60 + EXTRACT(MINUTE FROM f2.departuretime) > 
			EXTRACT(HOUR FROM f1.arrivaltime) * 60 + EXTRACT(MINUTE FROM f1.arrivaltime)
			AND ${jsonArray[0].passengersCount} <= m1.capacity - (
				SELECT COUNT(DISTINCT(h.seatnumber))
				FROM has h
				WHERE f1.flightid=h.flightid)
			AND ${jsonArray[0].passengersCount} <= m2.capacity - (
				SELECT COUNT(DISTINCT(h.seatnumber))
				FROM has h
				WHERE f2.flightid=h.flightid)
			`)

			rows1 = result1.rows
		}
	}

	return [rows, rows1]
}