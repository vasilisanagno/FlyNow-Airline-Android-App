import { pool } from '../../config/DatabaseConfig.js'
import { generateReservationId } from '../utils/GenerateRandomReservationId.js'

//function that makes query to find the seats that are taken from others
export async function queryForOccupiedSeats(flightid) {
    const result = await pool.query(`SELECT DISTINCT seatnumber FROM has WHERE flightid='${flightid}'`)
    return result
}

//function that makes query to find the airplane capacity of the flight
export async function queryForAirplaneCapacity(airplaneModel) {
    const result = await pool.query(`SELECT capacity FROM model WHERE modelid='${airplaneModel}'`)
    return result
}

//function that inserts the passengers of the booking in the database
export async function insertPassengers(passengers) {
	let passengersId = []

	for(let i=0; i<passengers.length; i++) {
		try{
			await pool.query(`INSERT INTO passenger(firstname,lastname,email,birthdate,sex,phonenumber) 
			VALUES('${passengers[i].firstname}','${passengers[i].lastname}','${passengers[i].email}',
				'${passengers[i].birthdate}','${passengers[i].gender}','${passengers[i].phonenumber}');`)
		}catch (error) {
			if (error.code === '23505' && error.constraint === 'passenger_email_key') {
				console.log('Passenger with this email already exists. Continuing with other queries.')
			}
			else {
				// Handle other errors
				console.error('Error inserting passenger:', error)
			}
		}
		
		const result = await pool.query(`SELECT passengerid FROM passenger WHERE email='${passengers[i].email}';`)
		passengersId.push({[passengers[i].email]: result.rows[0].passengerid})
	}
	return passengersId
}

//function that generates the reservation id with function generateReservationId() 
//and checks if this reservation id exists already in the database and if exists
//makes again the reservation id and do again this process and finally insert the reservation to the database
export async function insertReservation(petSize,price) {
	let reservationId
	
	do {
		reservationId = generateReservationId()
		const result = await pool.query(`SELECT COUNT(*) FROM reservation WHERE reservationid='${reservationId}';`)
		const count = result.rows[0].count

		if (count === '0') {
			break
		}
	} while (true)

	if(petSize != "") {
		await pool.query(`INSERT INTO reservation(reservationid, petsize, wifionboard, price)
       		VALUES('${reservationId}','${petSize}',${0},${price});`
		)
	}
	else {
		await pool.query(`INSERT INTO reservation(reservationid, petsize, wifionboard, price)
       		VALUES('${reservationId}',${null},${0},${price});`
		)
	}
	
	return reservationId
}

//function that inserts data to has table that has info for all the stuff about booking
export async function insertInHasTable(seats,baggage,passengers,passengersId,reservationId,classTypeOutbound,classTypeInbound) {

	for(let i=0; i<seats.length; i++) {
		const foundPassenger = passengersId.find(passenger => Object.keys(passenger)[0] === passengers[i].email)
		const passengerId = foundPassenger[passengers[i].email]
		
		//outbound direct flight
		if(seats[i].outbound?.direct) {
			await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
				VALUES(${passengerId},'${seats[i].outbound.direct.flightid_1}','${reservationId}',
					'${seats[i].outbound.direct.seat_1}','${classTypeOutbound.toUpperCase()} CLASS','baggage23kg',${baggage[i].outbound.baggage23kg},${false});`
			)
			await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
				VALUES(${passengerId},'${seats[i].outbound.direct.flightid_1}','${reservationId}',
					'${seats[i].outbound.direct.seat_1}','${classTypeOutbound.toUpperCase()} CLASS','baggage32kg',${baggage[i].outbound.baggage32kg},${false});`
			)
		}
		//outbound one stop flight
		else if(seats[i].outbound?.oneStop) {
			await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
				VALUES(${passengerId},'${seats[i].outbound.oneStop.flightid_1}','${reservationId}',
					'${seats[i].outbound.oneStop.seat_1}','${classTypeOutbound.toUpperCase()} CLASS','baggage23kg',${baggage[i].outbound.baggage23kg},${false});`
			)
			await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
				VALUES(${passengerId},'${seats[i].outbound.oneStop.flightid_1}','${reservationId}',
					'${seats[i].outbound.oneStop.seat_1}','${classTypeOutbound.toUpperCase()} CLASS','baggage32kg',${baggage[i].outbound.baggage32kg},${false});`
			)
			await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
				VALUES(${passengerId},'${seats[i].outbound.oneStop.flightid_2}','${reservationId}',
					'${seats[i].outbound.oneStop.seat_2}','${classTypeOutbound.toUpperCase()} CLASS','baggage23kg',${baggage[i].outbound.baggage23kg},${false});`
			)
			await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
				VALUES(${passengerId},'${seats[i].outbound.oneStop.flightid_2}','${reservationId}',
					'${seats[i].outbound.oneStop.seat_2}','${classTypeOutbound.toUpperCase()} CLASS','baggage32kg',${baggage[i].outbound.baggage32kg},${false});`
			)
		}
		if(seats[i].inbound) {
			//inbound direct flight
			if(seats[i].inbound?.direct) {
				await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
					VALUES(${passengerId},'${seats[i].inbound.direct.flightid_1}','${reservationId}',
						'${seats[i].inbound.direct.seat_1}','${classTypeInbound.toUpperCase()} CLASS','baggage23kg',${baggage[i].inbound.baggage23kg},${false});`
				)
				await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
					VALUES(${passengerId},'${seats[i].inbound.direct.flightid_1}','${reservationId}',
						'${seats[i].inbound.direct.seat_1}','${classTypeInbound.toUpperCase()} CLASS','baggage32kg',${baggage[i].inbound.baggage32kg},${false});`
				)
			}
			//inbound one stop flight
			else if(seats[i].inbound?.oneStop) {
				await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
					VALUES(${passengerId},'${seats[i].inbound.oneStop.flightid_1}','${reservationId}',
						'${seats[i].inbound.oneStop.seat_1}','${classTypeInbound.toUpperCase()} CLASS','baggage23kg',${baggage[i].inbound.baggage23kg},${false});`
				)
				await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
					VALUES(${passengerId},'${seats[i].inbound.oneStop.flightid_1}','${reservationId}',
						'${seats[i].inbound.oneStop.seat_1}','${classTypeInbound.toUpperCase()} CLASS','baggage32kg',${baggage[i].inbound.baggage32kg},${false});`
				)
				await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
					VALUES(${passengerId},'${seats[i].inbound.oneStop.flightid_2}','${reservationId}',
						'${seats[i].inbound.oneStop.seat_2}','${classTypeInbound.toUpperCase()} CLASS','baggage23kg',${baggage[i].inbound.baggage23kg},${false});`
				)
				await pool.query(`INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
					VALUES(${passengerId},'${seats[i].inbound.oneStop.flightid_2}','${reservationId}',
						'${seats[i].inbound.oneStop.seat_2}','${classTypeInbound.toUpperCase()} CLASS','baggage32kg',${baggage[i].inbound.baggage32kg},${false});`
				)
			}
		}
	}

	return true
}

//function that checks if selected seats of the passengers in the booking before the completion are all ok
//and no one's seat has been taken from someone else
export async function checkIfSeatsAreOk(seats) {

	for(let i=0; i<seats.length; i++) {
		
		if(seats[i].outbound?.direct) {
			const result = await pool.query(`
				SELECT seatnumber 
				FROM has 
				WHERE flightid='${seats[i].outbound.direct.flightid_1}' 
				AND seatnumber='${seats[i].outbound.direct.seat_1}'`
			)
			if(result.rows.length != 0) {
				return false
			}
		}
		else if(seats[i].outbound?.oneStop) {
			let result = await pool.query(`
				SELECT seatnumber 
				FROM has 
				WHERE flightid='${seats[i].outbound.oneStop.flightid_1}' 
				AND seatnumber='${seats[i].outbound.oneStop.seat_1}'`
			)
			if(result.rows.length != 0) {
				return false
			}
			result = await pool.query(`
				SELECT seatnumber 
				FROM has 
				WHERE flightid='${seats[i].outbound.oneStop.flightid_2}' 
				AND seatnumber='${seats[i].outbound.oneStop.seat_2}'`
			)
			if(result.rows.length != 0) {
				return false
			}
		}
		if(seats[i].inbound) {
			if(seats[i].inbound?.direct) {
				const result = await pool.query(`
				SELECT seatnumber 
				FROM has 
				WHERE flightid='${seats[i].inbound.direct.flightid_1}' 
				AND seatnumber='${seats[i].inbound.direct.seat_1}'`
			)
			if(result.rows.length != 0) {
				return false
			}
			}
			else if(seats[i].inbound?.oneStop) {
				let result = await pool.query(`
					SELECT seatnumber 
					FROM has 
					WHERE flightid='${seats[i].inbound.oneStop.flightid_1}' 
					AND seatnumber='${seats[i].inbound.oneStop.seat_1}'`
				)
				if(result.rows.length != 0) {
					return false
				}
				result = await pool.query(`
					SELECT seatnumber 
					FROM has 
					WHERE flightid='${seats[i].inbound.oneStop.flightid_2}' 
					AND seatnumber='${seats[i].inbound.oneStop.seat_2}'`
				)
				if(result.rows.length != 0) {
					return false
				}
			}
		}
	}

	return true
}