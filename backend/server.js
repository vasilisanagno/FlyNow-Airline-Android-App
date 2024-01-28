import express from 'express'
import cors from 'cors'
import pg from 'pg'

const { Pool } = pg

//conection to the database and settings about the server
const pool = new Pool({
    user: 'postgres',
    host: 'localhost',
    database: 'FlyNow',
    password: 'postgres0123456789',
    port: 5432
})
const app = express()
const PORT = process.env.PORT || 5000

app.use(cors())
app.use(express.urlencoded({extended:false}))
app.use(express.json())

//GET api that makes query to get the airports from the database
app.get('/flynow/airports', async (req, res) => {
    try {
		const result = await pool.query('SELECT * FROM airport ORDER BY city ASC')
		res.json(result.rows)
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//function that generates a random reservation id for the completion of the booking
function generateReservationId() {
	const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
	let reservationId = '';
  
	for (let i = 0; i < 6; i++) {
	  const randomIndex = Math.floor(Math.random() * characters.length);
	  reservationId += characters.charAt(randomIndex);
	}
  
	return reservationId;
}

//function that inserts the passengers of the booking in the database
async function insertPassengers(passengers) {
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
async function insertReservation(petSize,price) {
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
async function insertInHasTable(seats,baggage,passengers,passengersId,reservationId,classTypeOutbound,classTypeInbound) {

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
async function checkIfSeatsAreOk(seats) {

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

//POST api to delete the booking of the user from the database
app.post('/flynow/delete-booking', async (req, res) => {
	const jsonArray = req.body

    try {
		await pool.query(`
			DELETE FROM has
			WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
		`)
		await pool.query(`
			DELETE FROM contains
			WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
		`)
		await pool.query(`
			DELETE FROM reservation
			WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
		`)

		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api to use the previous functions to insert the new booking in the database
app.post('/flynow/new-booking', async (req, res) => {
	const jsonArray = req.body
	let passengersId, reservationId

    try {
		const result = await checkIfSeatsAreOk(jsonArray[0].seats)
		if(result) {
			passengersId = await insertPassengers(jsonArray[0].passengers)
			reservationId = await insertReservation(jsonArray[0].petSize,jsonArray[0].price)
			await insertInHasTable(jsonArray[0].seats,jsonArray[0].baggage,jsonArray[0].passengers,
				passengersId,reservationId,jsonArray[0].classTypeOutbound,jsonArray[0].classTypeInbound
			)
			res.json([{ success: true, bookingId: reservationId}])
		}
		else {
			res.json([{ success: false, bookingId: reservationId}])
		}
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that returns the value from the petsize field of the database for the pets from more screen
app.post('/flynow/pets-from-more', async (req, res) => {
    const jsonArray = req.body
	
	try {
		const result = await pool
		.query(`SELECT r.petsize
				FROM reservation r
				WHERE r.reservationid='${jsonArray[0].bookingid.toUpperCase()}'`)
		
		if(result.rows[0].petsize == null) {
			result.rows[0].petsize = ""
		}
		res.json([{ petSize: result.rows[0].petsize}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that updates the petsize field of the database that select the user from the pets from more screen
//+ the new price of the reservation that is priceOld + priceNew of the new pet size
app.post('/flynow/update-pets', async (req, res) => {
    const jsonArray = req.body
	
	try {
		const result = await pool
		.query(`UPDATE reservation
				SET petsize='${jsonArray[0].petSize}', price= price + ${jsonArray[0].price}
				WHERE reservationid='${jsonArray[0].bookingid.toUpperCase()}'`)
	
		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that returns the pieces of baggage that have selected the user during the completion of the database
app.post('/flynow/baggage-from-more', async (req, res) => {
    const jsonArray = req.body

	let passengers = []
	let result
	
	try {
		//returns the number of flights in the booking
		//one way-direct(1 flight), one way-one stop(2 flights), round trip-direct 2 ways(2 flights),
		//round trip-direct the outbound and one stop the inbound(3 flights), 
		//round trip-one stop the outbound and direct the inbound(3 flights),
		//round trip-one stop the outbound and one stop the inbound(4 flights)
		const numOfFlights = await pool.query(`SELECT COUNT(DISTINCT flightid) from has WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
		if(numOfFlights.rows[0].count === '1') {
			result = await pool
				.query(`SELECT DISTINCT p.firstname, p.lastname, p.sex, p.email, p.birthdate, p.phonenumber,
				h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg
				FROM flight f, has h1, has h2, passenger p
				WHERE h1.reservationid = '${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid = '${jsonArray[0].bookingId.toUpperCase()}'
				AND h1.baggage = 'baggage23kg'
				AND h2.baggage = 'baggage32kg'
				AND h1.flightid = f.flightid
				AND h2.flightid = f.flightid
				AND h1.passengerid = p.passengerid
				AND h2.passengerid = p.passengerid`)
			
			passengers.push({oneWay: true})
		}
		else if(numOfFlights.rows[0].count === '2') {
			result = await pool.query(`SELECT DISTINCT p.firstname, p.lastname, p.sex, p.email, p.birthdate, p.phonenumber,
			h1.numofbaggagepercategory AS numofbaggage23kgOutbound, h2.numofbaggagepercategory AS numofbaggage32kgOutbound
			,h3.numofbaggagepercategory AS numofbaggage23kgInbound, h4.numofbaggagepercategory AS numofbaggage32kgInbound
					FROM flight f1, flight f2, has h1, has h2, has h3, has h4, passenger p
					WHERE f1.flightid=h1.flightid AND f2.flightid=h3.flightid AND f1.flightid=h2.flightid AND f2.flightid=h4.flightid
					AND p.passengerid=h1.passengerid AND p.passengerid=h2.passengerid AND p.passengerid=h3.passengerid AND p.passengerid=h4.passengerid
					AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND h4.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND h1.baggage='baggage23kg'
					AND h2.baggage='baggage32kg'
					AND h3.baggage='baggage23kg'
					AND h4.baggage='baggage32kg'
					AND f1.arrivalairport=f2.departureairport AND
					f2.arrivalairport=f1.departureairport 
					AND f1.flightdate<=f2.flightdate`)

			if(result.rows.length == 0) {
				result = await pool.query(`SELECT DISTINCT p.firstname, p.lastname, p.sex, p.email, p.birthdate, p.phonenumber,
				h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg
				FROM flight f1, flight f2, has h1, has h2, passenger p
				WHERE f1.flightid = h1.flightid AND f2.flightid = h2.flightid
				AND h1.reservationid = '${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid = '${jsonArray[0].bookingId.toUpperCase()}'
				AND h1.baggage = 'baggage23kg'
				AND h2.baggage = 'baggage32kg'
				AND h1.passengerid = p.passengerid
				AND h2.passengerid = p.passengerid
				AND f1.arrivalairport=f2.departureairport AND
				f2.arrivalairport<>f1.departureairport`)

				
				passengers.push({oneWay: true})
			}
			else {

				passengers.push({oneWay: false})
			}
		}
		else if(numOfFlights.rows[0].count === '3') {
			result = await pool
			.query(`SELECT DISTINCT p.firstname, p.lastname, p.sex, p.email, p.birthdate, p.phonenumber,
			h1.numofbaggagepercategory AS numofbaggage23kgOutbound, h2.numofbaggagepercategory AS numofbaggage32kgOutbound
			,h3.numofbaggagepercategory AS numofbaggage23kgInbound, h4.numofbaggagepercategory AS numofbaggage32kgInbound
			FROM flight f1, flight f2, flight f3, has h1, has h2, has h3, has h4, has h5, has h6, passenger p
			WHERE f1.flightid=h1.flightid AND f2.flightid=h3.flightid AND f1.flightid=h2.flightid 
			AND f2.flightid=h4.flightid AND f3.flightid=h5.flightid AND f3.flightid=h6.flightid
			AND p.passengerid=h1.passengerid AND p.passengerid=h2.passengerid 
			AND p.passengerid=h3.passengerid AND p.passengerid=h4.passengerid 
			AND p.passengerid=h5.passengerid AND p.passengerid=h6.passengerid
			AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h4.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h5.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h6.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h1.baggage='baggage23kg'
			AND h2.baggage='baggage32kg'
			AND h3.baggage='baggage23kg'
			AND h4.baggage='baggage32kg'
			AND h5.baggage='baggage23kg'
			AND h6.baggage='baggage32kg'
			AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
			AND f3.arrivalairport=f1.departureairport
			AND f2.flightdate=f3.flightdate 
			AND f1.flightdate<=f2.flightdate`)


			if(result.rows.length == 0) {
				result = await pool.query(`SELECT DISTINCT p.firstname, p.lastname, p.sex, p.email, p.birthdate, p.phonenumber,
				h3.numofbaggagepercategory AS numofbaggage23kgOutbound, h4.numofbaggagepercategory AS numofbaggage32kgOutbound
				,h1.numofbaggagepercategory AS numofbaggage23kgInbound, h2.numofbaggagepercategory AS numofbaggage32kgInbound
				FROM flight f1, flight f2, flight f3, has h1, has h2, has h3, has h4, has h5, has h6, passenger p
				WHERE f1.flightid=h1.flightid AND f2.flightid=h3.flightid AND f1.flightid=h2.flightid 
				AND f2.flightid=h4.flightid AND f3.flightid=h5.flightid AND f3.flightid=h6.flightid
				AND p.passengerid=h1.passengerid AND p.passengerid=h2.passengerid 
				AND p.passengerid=h3.passengerid AND p.passengerid=h4.passengerid 
				AND p.passengerid=h5.passengerid AND p.passengerid=h6.passengerid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h4.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h5.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h6.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h1.baggage='baggage23kg'
				AND h2.baggage='baggage32kg'
				AND h3.baggage='baggage23kg'
				AND h4.baggage='baggage32kg'
				AND h5.baggage='baggage23kg'
				AND h6.baggage='baggage32kg'
				AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
				AND f3.arrivalairport=f1.departureairport
				AND f2.flightdate=f3.flightdate 
				AND f1.flightdate>=f2.flightdate`)
			}
			
			passengers.push({oneWay: false})
		}
		else if(numOfFlights.rows[0].count === '4') {
			result = await pool
			.query(`SELECT DISTINCT p.firstname, p.lastname, p.sex, p.email, p.birthdate, p.phonenumber,
			h1.numofbaggagepercategory AS numofbaggage23kgOutbound, h2.numofbaggagepercategory AS numofbaggage32kgOutbound
			,h5.numofbaggagepercategory AS numofbaggage23kgInbound, h6.numofbaggagepercategory AS numofbaggage32kgInbound
			FROM flight f1, flight f2, flight f3, flight f4, has h1, has h2, has h3, has h4, has h5, has h6, has h7, has h8, passenger p
			WHERE f1.flightid=h1.flightid AND f2.flightid=h3.flightid AND f1.flightid=h2.flightid 
			AND f2.flightid=h4.flightid AND f3.flightid=h5.flightid AND f3.flightid=h6.flightid
			AND f4.flightid=h7.flightid AND f4.flightid=h8.flightid
			AND p.passengerid=h1.passengerid AND p.passengerid=h2.passengerid 
			AND p.passengerid=h3.passengerid AND p.passengerid=h4.passengerid 
			AND p.passengerid=h5.passengerid AND p.passengerid=h6.passengerid
			AND p.passengerid=h7.passengerid AND p.passengerid=h8.passengerid
			AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h4.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h5.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h6.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h7.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h8.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h1.baggage='baggage23kg'
			AND h2.baggage='baggage32kg'
			AND h3.baggage='baggage23kg'
			AND h4.baggage='baggage32kg'
			AND h5.baggage='baggage23kg'
			AND h6.baggage='baggage32kg'
			AND h7.baggage='baggage23kg'
			AND h8.baggage='baggage32kg'
			AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
			AND f3.arrivalairport=f4.departureairport AND f4.arrivalairport=f1.departureairport
			AND f1.flightdate=f2.flightdate AND f3.flightdate=f4.flightdate
			AND f1.flightdate<=f3.flightdate`)

			passengers.push({oneWay: false})
		}

		//build the json that will be sent the data through it
		//baggageOutbound and baggageInbound is the addition of the selections of baggage 23kg and 32kg
		//to see the limit 5 baggage per passenger, is for baggage from more screen
		if(passengers[0].oneWay) {
			for (let i = 0; i < result.rows.length; i++) {
				let passenger = {
					"firstname": result.rows[i].firstname,
					"lastname": result.rows[i].lastname,
					"gender": result.rows[i].sex,
					"birthdate": result.rows[i].birthdate,
					"email": result.rows[i].email,
					"phonenumber": result.rows[i].phonenumber,
					"baggageOutbound": result.rows[i].numofbaggage23kg + result.rows[i].numofbaggage32kg
				}
	
				passengers.push(passenger)
			}
		}
		else {
			for (let i = 0; i < result.rows.length; i++) {
				let passenger = {
					"firstname": result.rows[i].firstname,
					"lastname": result.rows[i].lastname,
					"gender": result.rows[i].sex,
					"birthdate": result.rows[i].birthdate,
					"email": result.rows[i].email,
					"phonenumber": result.rows[i].phonenumber,
					"baggageOutbound": result.rows[i].numofbaggage23kgoutbound + result.rows[i].numofbaggage32kgoutbound,
					"baggageInbound": result.rows[i].numofbaggage23kginbound + result.rows[i].numofbaggage32kginbound
				}
	
				passengers.push(passenger)
			}
		}
		
		res.json(passengers)
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that updates the number of pieces of baggage per passenger that select in the baggage from more screen
//+ the new price of the reservation that is priceOld + priceNew of the new pieces of baggage
app.post('/flynow/update-baggage', async (req, res) => {
    const jsonArray = req.body
	
	try {
		if(jsonArray[0].oneWay) {
			for(let i=0; i<jsonArray[0].baggage.length; i++) {
				await pool
				.query(`UPDATE has h
						SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage23kg}
						FROM passenger p
						WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
						AND p.passengerid=h.passengerid
						AND p.email='${jsonArray[0].baggage[i].email}'
						AND h.baggage='baggage23kg'`)
				
				await pool
				.query(`UPDATE has h
						SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage32kg}
						FROM passenger p
						WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
						AND p.passengerid=h.passengerid
						AND p.email='${jsonArray[0].baggage[i].email}'
						AND h.baggage='baggage32kg'`)
			}
		}
		else {
			const numOfFlights = await pool.query(`SELECT COUNT(DISTINCT flightid) from has WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
			if(numOfFlights.rows[0].count === '2') {
				let result = await pool.query(`SELECT DISTINCT f1.flightid AS flightid1, f2.flightid AS flightid2
				FROM flight f1, flight f2, has h1, has h2 
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND
				f2.arrivalairport=f1.departureairport 
				AND f1.flightdate<=f2.flightdate`)
				
				for(let i=0; i<jsonArray[0].baggage.length; i++) {
					await pool
					.query(`UPDATE has h
							SET numofbaggagepercategory = h.numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage23kg}
							FROM passenger p
							WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
							AND p.passengerid=h.passengerid
							AND p.email='${jsonArray[0].baggage[i].email}'
							AND h.baggage='baggage23kg'
							AND h.flightid='${result.rows[0].flightid1}'`)
					
					await pool
					.query(`UPDATE has h
							SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage32kg}
							FROM passenger p
							WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
							AND p.passengerid=h.passengerid
							AND p.email='${jsonArray[0].baggage[i].email}'
							AND h.baggage='baggage32kg'
							AND h.flightid='${result.rows[0].flightid1}'`)
					
					await pool
					.query(`UPDATE has h
							SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].inbound.baggage23kg}
							FROM passenger p
							WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
							AND p.passengerid=h.passengerid
							AND p.email='${jsonArray[0].baggage[i].email}'
							AND h.baggage='baggage23kg'
							AND h.flightid='${result.rows[0].flightid2}'`)
					
					await pool
					.query(`UPDATE has h
							SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].inbound.baggage32kg}
							FROM passenger p
							WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
							AND p.passengerid=h.passengerid
							AND p.email='${jsonArray[0].baggage[i].email}'
							AND h.baggage='baggage32kg'
							AND h.flightid='${result.rows[0].flightid2}'`)
				}
			}
			else if(numOfFlights.rows[0].count === '3') {
				let result = await pool.query(`SELECT DISTINCT f1.flightid AS flightid1, f2.flightid AS flightid2, f3.flightid AS flightid3
				FROM flight f1, flight f2, flight f3, 
				has h1, has h2, has h3
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
				AND f3.flightid=h3.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
				AND f3.arrivalairport=f1.departureairport
				AND f2.flightdate=f3.flightdate 
				AND f1.flightdate<=f2.flightdate`)
	
				if(result.rows.length == 0) {
					let result = await pool.query(`SELECT DISTINCT f1.flightid AS flightid1, f2.flightid AS flightid2, f3.flightid AS flightid3
					FROM flight f1, flight f2, flight f3, 
					has h1, has h2, has h3
					WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
					AND f3.flightid=h3.flightid
					AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
					AND f3.arrivalairport=f1.departureairport
					AND f2.flightdate=f3.flightdate 
					AND f1.flightdate>=f2.flightdate`)
	
					for(let i=0; i<jsonArray[0].baggage.length; i++) {
						await pool
						.query(`UPDATE has h
								SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage23kg}
								FROM passenger p
								WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
								AND p.passengerid=h.passengerid
								AND p.email='${jsonArray[0].baggage[i].email}'
								AND h.baggage='baggage23kg'
								AND (h.flightid='${result.rows[0].flightid2}'
								OR h.flightid='${result.rows[0].flightid3}')`)
						
						await pool
						.query(`UPDATE has h
								SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage32kg}
								FROM passenger p
								WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
								AND p.passengerid=h.passengerid
								AND p.email='${jsonArray[0].baggage[i].email}'
								AND h.baggage='baggage32kg'
								AND (h.flightid='${result.rows[0].flightid2}'
								OR h.flightid='${result.rows[0].flightid3}')`)
						
						await pool
						.query(`UPDATE has h
								SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].inbound.baggage23kg}
								FROM passenger p
								WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
								AND p.passengerid=h.passengerid
								AND p.email='${jsonArray[0].baggage[i].email}'
								AND h.baggage='baggage23kg'
								AND h.flightid='${result.rows[0].flightid1}'`)
						
						await pool
						.query(`UPDATE has h
								SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].inbound.baggage32kg}
								FROM passenger p
								WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
								AND p.passengerid=h.passengerid
								AND p.email='${jsonArray[0].baggage[i].email}'
								AND h.baggage='baggage32kg'
								AND h.flightid='${result.rows[0].flightid1}'`)
					}
				}
				else {
					for(let i=0; i<jsonArray[0].baggage.length; i++) {
						await pool
						.query(`UPDATE has h
								SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage23kg}
								FROM passenger p
								WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
								AND p.passengerid=h.passengerid
								AND p.email='${jsonArray[0].baggage[i].email}'
								AND h.baggage='baggage23kg'
								AND h.flightid='${result.rows[0].flightid1}'`)
						
						await pool
						.query(`UPDATE has h
								SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage32kg}
								FROM passenger p
								WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
								AND p.passengerid=h.passengerid
								AND p.email='${jsonArray[0].baggage[i].email}'
								AND h.baggage='baggage32kg'
								AND h.flightid='${result.rows[0].flightid1}'`)
						
						await pool
						.query(`UPDATE has h
								SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].inbound.baggage23kg}
								FROM passenger p
								WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
								AND p.passengerid=h.passengerid
								AND p.email='${jsonArray[0].baggage[i].email}'
								AND h.baggage='baggage23kg'
								AND (h.flightid='${result.rows[0].flightid2}'
								OR h.flightid='${result.rows[0].flightid3}')`)
						
						await pool
						.query(`UPDATE has h
								SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].inbound.baggage32kg}
								FROM passenger p
								WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
								AND p.passengerid=h.passengerid
								AND p.email='${jsonArray[0].baggage[i].email}'
								AND h.baggage='baggage32kg'
								AND (h.flightid='${result.rows[0].flightid2}'
								OR h.flightid='${result.rows[0].flightid3}')`)
					}
				}
			}
			else if(numOfFlights.rows[0].count === '4') {
				const result = await pool.query(`SELECT DISTINCT f1.flightid AS flightid1, f2.flightid AS flightid2, f3.flightid AS flightid3, f4.flightid AS flightid4
				FROM flight f1, flight f2, flight f3,
				flight f4, has h1, has h2, has h3, has h4
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
				AND f3.flightid=h3.flightid AND f4.flightid=h4.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h4.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
				AND f3.arrivalairport=f4.departureairport AND f4.arrivalairport=f1.departureairport
				AND f1.flightdate=f2.flightdate AND f3.flightdate=f4.flightdate
				AND f1.flightdate<=f3.flightdate`)
	
				for(let i=0; i<jsonArray[0].baggage.length; i++) {
					await pool
					.query(`UPDATE has h
							SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage23kg}
							FROM passenger p
							WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
							AND p.passengerid=h.passengerid
							AND p.email='${jsonArray[0].baggage[i].email}'
							AND h.baggage='baggage23kg'
							AND (h.flightid='${result.rows[0].flightid1}'
							OR h.flightid='${result.rows[0].flightid2}')`)
					
					await pool
					.query(`UPDATE has h
							SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].outbound.baggage32kg}
							FROM passenger p
							WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
							AND p.passengerid=h.passengerid
							AND p.email='${jsonArray[0].baggage[i].email}'
							AND h.baggage='baggage32kg'
							AND (h.flightid='${result.rows[0].flightid1}'
							OR h.flightid='${result.rows[0].flightid2}')`)
					
					await pool
					.query(`UPDATE has h
							SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].inbound.baggage23kg}
							FROM passenger p
							WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
							AND p.passengerid=h.passengerid
							AND p.email='${jsonArray[0].baggage[i].email}'
							AND h.baggage='baggage23kg'
							AND (h.flightid='${result.rows[0].flightid3}'
							OR h.flightid='${result.rows[0].flightid4}')`)
					
					await pool
					.query(`UPDATE has h
							SET numofbaggagepercategory = numofbaggagepercategory + ${jsonArray[0].baggage[i].inbound.baggage32kg}
							FROM passenger p
							WHERE h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
							AND p.passengerid=h.passengerid
							AND p.email='${jsonArray[0].baggage[i].email}'
							AND h.baggage='baggage32kg'
							AND (h.flightid='${result.rows[0].flightid3}'
							OR h.flightid='${result.rows[0].flightid4}')`)
				}
			}
		}
		
		await pool
		.query(`UPDATE reservation
				SET price= price + ${jsonArray[0].price}
				WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)

		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that returns the value from the wifionboard field of the database for the wifi on board screen
app.post('/flynow/wifi-on-board', async (req, res) => {
    const jsonArray = req.body
	
	try {
		const result = await pool
		.query(`SELECT r.wifionboard 
				FROM reservation r
				WHERE r.reservationid='${jsonArray[0].bookingid.toUpperCase()}'`)
	
		res.json([{ wifiOnBoard: result.rows[0].wifionboard}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that updates the wifionboard field of the database that select the user from the wifi on board screen
//+ the new price of the reservation that is priceOld + priceNew of the new selection of the wifi on board
app.post('/flynow/update-wifi', async (req, res) => {
    const jsonArray = req.body
	
	try {
		const result = await pool
		.query(`UPDATE reservation
				SET wifionboard=${jsonArray[0].wifiOnBoard}, price= price + ${jsonArray[0].price}
				WHERE reservationid='${jsonArray[0].bookingid.toUpperCase()}'`)
	
		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that returns the class of the outbound and inbound(if there is) flights
//from the classtype field of the database for the upgrade to business screen
app.post('/flynow/upgrade-to-business', async (req, res) => {
    const jsonArray = req.body
	//the return value contains also if the flight is one way or round trip(with return)
	try {
		const numOfFlights = await pool.query(`
		SELECT COUNT(DISTINCT flightid) 
		from has 
		WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
		
		if(numOfFlights.rows[0].count === '1') {
			const result = await pool.query(`SELECT DISTINCT h.classtype FROM flight f, has h 
			WHERE f.flightid=h.flightid 
			AND h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
			
			res.json([{
				oneWay: true,
				outbound: {
					classType: result.rows[0].classtype
				}
			}])
		}
		else if(numOfFlights.rows[0].count === '2') {
			let result = await pool.query(`SELECT DISTINCT h1.classtype AS classtype1, h2.classtype AS classtype2
			FROM flight f1, flight f2, has h1, has h2 
			WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid
			AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND f1.arrivalairport=f2.departureairport AND
			f2.arrivalairport=f1.departureairport 
			AND f1.flightdate<=f2.flightdate`)

			if(result.rows.length == 0) {
				let result = await pool.query(`SELECT DISTINCT h1.classtype AS classtype
				FROM flight f1, flight f2, has h1, has h2 
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND
				f2.arrivalairport<>f1.departureairport`)

				res.json([{
					oneWay: true,
					outbound: {
						classType: result.rows[0].classtype
					}
				}])
			}
			else {
				res.json([{
					oneWay: false,
					outbound: {
						classType: result.rows[0].classtype1
					},
					inbound: {
						classType: result.rows[0].classtype2
					}
				}])
			}
		}
		else if(numOfFlights.rows[0].count === '3') {
			let result = await pool.query(`SELECT DISTINCT h1.classtype AS classtype1, h2.classtype AS classtype2 
			FROM flight f1, flight f2, flight f3, 
			has h1, has h2, has h3
			WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
			AND f3.flightid=h3.flightid
			AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
			AND f3.arrivalairport=f1.departureairport
			AND f2.flightdate=f3.flightdate 
			AND f1.flightdate<=f2.flightdate`)

			if(result.rows.length == 0) {
				let result = await pool.query(`SELECT DISTINCT h1.classtype AS classtype1, h2.classtype AS classtype2 
				FROM flight f1, flight f2, flight f3, 
				has h1, has h2, has h3
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
				AND f3.flightid=h3.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
				AND f3.arrivalairport=f1.departureairport
				AND f2.flightdate=f3.flightdate 
				AND f1.flightdate>=f2.flightdate`)

				res.json([{
					oneWay: false,
					outbound: {
						classType: result.rows[0].classtype2
					},
					inbound: {
						classType: result.rows[0].classtype1
					}
				}])
			}
			else {
				res.json([{
					oneWay: false,
					outbound: {
						classType: result.rows[0].classtype1
					},
					inbound: {
						classType: result.rows[0].classtype2
					}
				}])
			}
		}
		else if(numOfFlights.rows[0].count === '4') {
			const result = await pool.query(`SELECT DISTINCT h1.classtype AS classtype1, h3.classtype AS classtype2 
			FROM flight f1, flight f2, flight f3,
			flight f4, has h1, has h2, has h3, has h4
			WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
			AND f3.flightid=h3.flightid AND f4.flightid=h4.flightid
			AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND h4.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
			AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
			AND f3.arrivalairport=f4.departureairport AND f4.arrivalairport=f1.departureairport
			AND f1.flightdate=f2.flightdate AND f3.flightdate=f4.flightdate
			AND f1.flightdate<=f3.flightdate`)

			res.json([{
				oneWay: false,
				outbound: {
					classType: result.rows[0].classtype1
				},
				inbound: {
					classType: result.rows[0].classtype2
				}
			}])
		}
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that updates the new selected classes of the flights outbound and inbound(if there is) from the
//upgrade to business screen
//+ the new price of the reservation that is priceOld + priceNew of the new updated classes in the flights
app.post('/flynow/update-business', async (req, res) => {
    const jsonArray = req.body
	
	try {
		if(jsonArray[0].outbound&&jsonArray[0].inbound) {
			await pool.query(`UPDATE has 
			SET classtype='BUSINESS CLASS'
			WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
		}
		else {
			const numOfFlights = await pool.query(`
			SELECT COUNT(DISTINCT flightid) 
			from has 
			WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
			
			if(numOfFlights.rows[0].count === '1') {
				if(jsonArray[0].outbound) {
					await pool.query(`UPDATE has
					SET classtype='BUSINESS CLASS'
					WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
				}
			}
			else if(numOfFlights.rows[0].count === '2') {
				let result = await pool.query(`SELECT DISTINCT f1.flightid AS flightid1, f2.flightid AS flightid2
				FROM flight f1, flight f2, has h1, has h2 
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND
				f2.arrivalairport=f1.departureairport 
				AND f1.flightdate<=f2.flightdate`)

				if(result.rows.length == 0) {
					if(jsonArray[0].outbound) {
						await pool.query(`UPDATE has
						SET classtype='BUSINESS CLASS'
						WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
					}
				}
				else {
					if(jsonArray[0].outbound) {
						await pool.query(`UPDATE has
						SET classtype='BUSINESS CLASS'
						WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
						AND flightid='${result.rows[0].flightid1}'`)
					}
					else if(jsonArray[0].inbound) {
						await pool.query(`UPDATE has
						SET classtype='BUSINESS CLASS'
						WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
						AND flightid='${result.rows[0].flightid2}'`)
					}
				}
			}
			else if(numOfFlights.rows[0].count === '3') {
				let result = await pool.query(`SELECT DISTINCT f1.flightid AS flightid1, f2.flightid AS flightid2, f3.flightid AS flightid3
				FROM flight f1, flight f2, flight f3, 
				has h1, has h2, has h3
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
				AND f3.flightid=h3.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
				AND f3.arrivalairport=f1.departureairport
				AND f2.flightdate=f3.flightdate 
				AND f1.flightdate<=f2.flightdate`)

				if(result.rows.length == 0) {
					let result = await pool.query(`SELECT DISTINCT f1.flightid AS flightid1, f2.flightid AS flightid2, f3.flightid AS flightid3 
					FROM flight f1, flight f2, flight f3, 
					has h1, has h2, has h3
					WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
					AND f3.flightid=h3.flightid
					AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
					AND f3.arrivalairport=f1.departureairport
					AND f2.flightdate=f3.flightdate 
					AND f1.flightdate>=f2.flightdate`)

					if(jsonArray[0].outbound) {
						await pool.query(`UPDATE has
						SET classtype='BUSINESS CLASS'
						WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
						AND (flightid='${result.rows[0].flightid2}'
						OR flightid='${result.rows[0].flightid3}')`)
					}
					else if(jsonArray[0].inbound) {
						await pool.query(`UPDATE has
						SET classtype='BUSINESS CLASS'
						WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
						AND flightid='${result.rows[0].flightid1}'`)
					}
				}
				else {
					if(jsonArray[0].outbound) {
						await pool.query(`UPDATE has
						SET classtype='BUSINESS CLASS'
						WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
						AND flightid='${result.rows[0].flightid1}'`)
					}
					else if(jsonArray[0].inbound) {
						await pool.query(`UPDATE has
						SET classtype='BUSINESS CLASS'
						WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
						AND (flightid='${result.rows[0].flightid2}'
						OR flightid='${result.rows[0].flightid3}')`)
					}
				}
			}
			else if(numOfFlights.rows[0].count === '4') {
				const result = await pool.query(`SELECT DISTINCT f1.flightid AS flightid1, f2.flightid AS flightid2, 
				f3.flightid AS flightid3, f4.flightid AS flightid4
				FROM flight f1, flight f2, flight f3,
				flight f4, has h1, has h2, has h3, has h4
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
				AND f3.flightid=h3.flightid AND f4.flightid=h4.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h4.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
				AND f3.arrivalairport=f4.departureairport AND f4.arrivalairport=f1.departureairport
				AND f1.flightdate=f2.flightdate AND f3.flightdate=f4.flightdate
				AND f1.flightdate<=f3.flightdate`)

				if(jsonArray[0].outbound) {
					await pool.query(`UPDATE has
					SET classtype='BUSINESS CLASS'
					WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND (flightid='${result.rows[0].flightid1}'
					OR flightid='${result.rows[0].flightid2}')`)
				}
				else if(jsonArray[0].inbound) {
					await pool.query(`UPDATE has
					SET classtype='BUSINESS CLASS'
					WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
					AND (flightid='${result.rows[0].flightid3}'
					OR flightid='${result.rows[0].flightid4}')`)
				}
			}
		}

		await pool.query(`
			UPDATE reservation
			SET price = price + ${jsonArray[0].price}
			WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'
		`)
		res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that checks booking if exists and returns true or false
app.post('/flynow/check-booking', async (req, res) => {
    const jsonArray = req.body

	try {
		const result = await pool
		.query(`SELECT p.lastname, h.reservationid 
				FROM passenger p, has h 
				WHERE LOWER(p.lastname) ='${jsonArray[0].lastname.toLowerCase()}' 
						AND h.reservationid = '${jsonArray[0].bookingid.toUpperCase()}' 
						AND p.passengerid = h.passengerid`)
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
})

//POST api that returns all the booking details for flights, passengers, baggage, 
//seats, wifi, cars and the total price of the reservation
app.post('/flynow/booking-details', async (req, res) => {
    const jsonArray = req.body
	
	let i
	let flights = []
	let passengers = []
	let baggagePerPassenger = []
	let jsonResponse = []

	//returns the num of flights in a reservation		
	try{
		const numOfFlights = await pool
		.query(`SELECT COUNT(DISTINCT h.flightid)
				FROM has h
				WHERE  h.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'`)

		if(numOfFlights.rows[0].count === '1'){
			try{
				//one way direct flight
				const direct_flight = await pool
				.query(`SELECT DISTINCT TO_CHAR(f.flightdate, 'DD/MM/YYYY') AS flightdate, f.departuretime, f.arrivaltime, a1.city AS departurecity, a2.city AS arrivalcity, a1.name AS departureairport, a2.name AS arrivalairport, fd.duration, f.flightid, ap.model as airplanemodel, h.classtype
						FROM flight f, has h, airport a1, airport a2, flight_duration fd, airplane ap
						WHERE f.flightid = h.flightid
								AND h.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND f.departureairport = a1.name
								AND f.arrivalairport = a2.name
								AND f.departuretime = fd.departuretime
								AND f.arrivaltime = fd.arrivaltime
								AND f.departuretime < f.arrivaltime
								AND ap.airplaneid=f.airplane`)

				let flight = {
					"flightdate": direct_flight.rows[0].flightdate,
					"departuretime": direct_flight.rows[0].departuretime.substring(0, 5),
					"arrivaltime": direct_flight.rows[0].arrivaltime.substring(0, 5),
					"departurecity": direct_flight.rows[0].departurecity,
					"arrivalcity": direct_flight.rows[0].arrivalcity,
					"duration": direct_flight.rows[0].duration,
					"departureairport": direct_flight.rows[0].departureairport,
					"arrivalairport": direct_flight.rows[0].arrivalairport,
					"flightid":  direct_flight.rows[0].flightid,
					"airplanemodel": direct_flight.rows[0].airplanemodel,
					"classtype": direct_flight.rows[0].classtype
				}
				flights.push(flight)
				jsonResponse.push({"oneway": true})
				jsonResponse.push({"outbounddirect": true})
				jsonResponse.push({"inbounddirect": false})
				jsonResponse.push({"flights": flights})
			}catch (error) {
				console.error(error)
				res.status(500).json({ error: 'Internal Server Error' })
			}
			try {
				//returns the baggage per passenger
				const result = await pool
				.query(`SELECT h1.baggage AS baggage23kg, h2.baggage AS baggage32kg, h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg, h1.flightid , h1.reservationid, p.firstname, p.lastname, p.sex, h1.seatnumber, a1.city AS departurecity, a2.city AS arrivalcity, h1.checkin AS checkin
						FROM flight f, has h1, has h2, passenger p, airport a1, airport a2
						WHERE f.flightid = h1.flightid
								AND h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND h1.baggage = 'baggage23kg'
								AND h2.baggage = 'baggage32kg'
								AND h1.passengerid = h2.passengerid
								AND h1.flightid = h2.flightid
								AND h1.passengerid = p.passengerid
								AND h2.passengerid = p.passengerid
								AND f.departureairport = a1.name
								AND f.arrivalairport = a2.name
								ORDER BY p.lastname ASC`)
		
				for (i = 0; i < result.rows.length; i++) {
					let passengerBaggage = {
						"firstname": result.rows[i].firstname,
						"lastname": result.rows[i].lastname,
						"gender": result.rows[i].sex,
						"flightid": result.rows[i].flightid,
						"reservationid": result.rows[i].reservationid,
						"baggage23kg": result.rows[i].numofbaggage23kg,
						"baggage32kg": result.rows[i].numofbaggage32kg,
						"seatnumber": result.rows[i].seatnumber,
						"departurecity": result.rows[i].departurecity,
						"arrivalcity": result.rows[i].arrivalcity,
						"checkin": result.rows[i].checkin
					}
					baggagePerPassenger.push(passengerBaggage)
				}
				jsonResponse.push({"baggagePerPassenger": baggagePerPassenger})
			}catch (error) {
				console.error(error)
				res.status(500).json({ error: 'Internal Server Error' })
			}
		}
		else if(numOfFlights.rows[0].count === '2'){
			try{
				//two ways direct
				const two_way_direct = await pool
				.query(`SELECT DISTINCT TO_CHAR(f1.flightdate, 'DD/MM/YYYY') AS flightdate1, f1.departuretime AS departuretime1, f1.arrivaltime AS arrivaltime1, a1.city AS departurecity1, a2.city AS arrivalcity1, a1.name AS departureairport1, a2.name AS arrivalairport1, fd1.duration AS duration1, f1.flightid AS flightid1, ap1.model as airplanemodel1, h1.classtype AS classtype1,  
						TO_CHAR(f2.flightdate, 'DD/MM/YYYY') AS flightdate2, f2.departuretime AS departuretime2, f2.arrivaltime AS arrivaltime2, a3.city AS departurecity2, a4.city AS arrivalcity2, a3.name AS departureairport2, a4.name AS arrivalairport2, fd2.duration AS duration2, f2.flightid AS flightid2, ap2.model as airplanemodel2, h2.classtype AS classtype2
						FROM
						flight f1
						JOIN has h1 ON f1.flightid = h1.flightid
						JOIN airport a1 ON f1.departureairport = a1.name
						JOIN airport a2 ON f1.arrivalairport = a2.name
						JOIN flight_duration fd1 ON f1.departuretime = fd1.departuretime AND f1.arrivaltime = fd1.arrivaltime
						JOIN airplane ap1 ON ap1.airplaneid = f1.airplane,
						
						flight f2
						JOIN has h2 ON f2.flightid = h2.flightid
						JOIN airport a3 ON f2.departureairport = a3.name
						JOIN airport a4 ON f2.arrivalairport = a4.name
						JOIN flight_duration fd2 ON f2.departuretime = fd2.departuretime AND f2.arrivaltime = fd2.arrivaltime
						JOIN airplane ap2 ON ap2.airplaneid = f2.airplane
						
						WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
							AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
							AND f1.arrivalairport = f2.departureairport
							AND f1.flightdate <= f2.flightdate
							AND f2.arrivalairport = f1.departureairport`)
				
				if(two_way_direct.rows.length > 0){
					let flight1 = {
						"flightdate": two_way_direct.rows[0].flightdate1,
						"departuretime": two_way_direct.rows[0].departuretime1.substring(0, 5),
						"arrivaltime": two_way_direct.rows[0].arrivaltime1.substring(0, 5),
						"departurecity": two_way_direct.rows[0].departurecity1,
						"arrivalcity": two_way_direct.rows[0].arrivalcity1,
						"duration": two_way_direct.rows[0].duration1,
						"departureairport": two_way_direct.rows[0].departureairport1,
						"arrivalairport": two_way_direct.rows[0].arrivalairport1, 
						"flightid": two_way_direct.rows[0].flightid1,
						"airplanemodel": two_way_direct.rows[0].airplanemodel1,
						"classtype": two_way_direct.rows[0].classtype1
					}
					flights.push(flight1)
					let flight2 = {
						"flightdate": two_way_direct.rows[0].flightdate2,
						"departuretime": two_way_direct.rows[0].departuretime2.substring(0, 5),
						"arrivaltime": two_way_direct.rows[0].arrivaltime2.substring(0, 5),
						"departurecity": two_way_direct.rows[0].departurecity2,
						"arrivalcity": two_way_direct.rows[0].arrivalcity2,
						"duration": two_way_direct.rows[0].duration2,
						"departureairport": two_way_direct.rows[0].departureairport2,
						"arrivalairport": two_way_direct.rows[0].arrivalairport2,
						"flightid": two_way_direct.rows[0].flightid2,
						"airplanemodel": two_way_direct.rows[0].airplanemodel2,
						"classtype": two_way_direct.rows[0].classtype2
					}
					flights.push(flight2)
					jsonResponse.push({"oneway": false})
					jsonResponse.push({"outbounddirect": true})
					jsonResponse.push({"inbounddirect": true})
					jsonResponse.push({"flights": flights})

					try {
						//returns the baggage per passenger
						const result = await pool
						.query(`SELECT h1.baggage AS baggage23kg, h2.baggage AS baggage32kg, h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg, h1.flightid , h1.reservationid, p.firstname, p.lastname, p.sex, h1.seatnumber, a1.city AS departurecity, a2.city AS arrivalcity, h1.checkin AS checkin
								FROM has h1, has h2, passenger p, flight f1, flight f2, airport a1, airport a2
								WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
										AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
										AND h1.baggage = 'baggage23kg'
										AND h2.baggage = 'baggage32kg'
										AND h1.passengerid = h2.passengerid
										AND h1.flightid = h2.flightid
										AND h1.passengerid = p.passengerid
										AND h2.passengerid = p.passengerid
										AND f1.flightid = h1.flightid
										AND f2.flightid = h2.flightid
										AND f1.flightid = h2.flightid
										AND f2.flightid = h1.flightid
										AND f1.flightdate <= f2.flightdate
										AND f1.departureairport = a1.name
										AND f1.arrivalairport = a2.name
								ORDER BY f1.flightdate, f2.flightdate, p.lastname ASC`)
				
						for (i = 0; i < result.rows.length; i++) {
							let passengerBaggage = {
								"firstname": result.rows[i].firstname,
								"lastname": result.rows[i].lastname,
								"gender": result.rows[i].sex,
								"flightid": result.rows[i].flightid,
								"reservationid": result.rows[i].reservationid,
								"baggage23kg": result.rows[i].numofbaggage23kg,
								"baggage32kg": result.rows[i].numofbaggage32kg,
								"seatnumber": result.rows[i].seatnumber,
								"departurecity": result.rows[i].departurecity,
								"arrivalcity": result.rows[i].arrivalcity,
								"checkin": result.rows[i].checkin
							}
							baggagePerPassenger.push(passengerBaggage)
						}
						jsonResponse.push({"baggagePerPassenger": baggagePerPassenger})
					}catch (error) {
						console.error(error)
						res.status(500).json({ error: 'Internal Server Error' })
					}
				}
			}catch (error) {
				console.error(error)
				res.status(500).json({ error: 'Internal Server Error' })
			}
			if(flights.length === 0){
				try{
					//returns oneway one-stop flight
					const one_stop_flight = await pool
					.query(`SELECT DISTINCT TO_CHAR(f1.flightdate, 'DD/MM/YYYY') AS flightdate1, f1.departuretime AS departuretime1, f1.arrivaltime AS arrivaltime1, a1.city AS departurecity1, a2.city AS arrivalcity1, a1.name AS departureairport1, a2.name AS arrivalairport1, fd1.duration AS duration1, f1.flightid AS flightid1, ap1.model as airplanemodel1, h1.classtype AS classtype1,
							TO_CHAR(f2.flightdate, 'DD/MM/YYYY') AS flightdate2, f2.departuretime AS departuretime2, f2.arrivaltime AS arrivaltime2, a3.city AS departurecity2, a4.city AS arrivalcity2, a3.name AS departureairport2, a4.name AS arrivalairport2, fd2.duration AS duration2, f2.flightid AS flightid2, ap2.model as airplanemodel2, h2.classtype AS classtype2
							FROM
							flight f1
							JOIN has h1 ON f1.flightid = h1.flightid
							JOIN airport a1 ON f1.departureairport = a1.name
							JOIN airport a2 ON f1.arrivalairport = a2.name
							JOIN flight_duration fd1 ON f1.departuretime = fd1.departuretime AND f1.arrivaltime = fd1.arrivaltime
							JOIN airplane ap1 ON ap1.airplaneid = f1.airplane,
							
							flight f2
							JOIN has h2 ON f2.flightid = h2.flightid
							JOIN airport a3 ON f2.departureairport = a3.name
							JOIN airport a4 ON f2.arrivalairport = a4.name
							JOIN flight_duration fd2 ON f2.departuretime = fd2.departuretime AND f2.arrivaltime = fd2.arrivaltime
							JOIN airplane ap2 ON ap2.airplaneid = f2.airplane
							
							WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND f1.arrivalairport = f2.departureairport
								AND f2.flightdate = f1.flightdate
								AND f1.departureairport <> f2.arrivalairport`)
				
					if(one_stop_flight.rows.length > 0){
						let flight1 = {
							"flightdate": one_stop_flight.rows[0].flightdate1,
							"departuretime": one_stop_flight.rows[0].departuretime1.substring(0, 5),
							"arrivaltime": one_stop_flight.rows[0].arrivaltime1.substring(0, 5),
							"departurecity": one_stop_flight.rows[0].departurecity1,
							"arrivalcity": one_stop_flight.rows[0].arrivalcity1,
							"duration": one_stop_flight.rows[0].duration1,
							"departureairport": one_stop_flight.rows[0].departureairport1,
							"arrivalairport": one_stop_flight.rows[0].arrivalairport1,
							"flightid": one_stop_flight.rows[0].flightid1,
							"airplanemodel": one_stop_flight.rows[0].airplanemodel1,
							"classtype": one_stop_flight.rows[0].classtype1
						}
						flights.push(flight1)
						let flight2 = {
							"flightdate": one_stop_flight.rows[0].flightdate2,
							"departuretime": one_stop_flight.rows[0].departuretime2.substring(0, 5),
							"arrivaltime": one_stop_flight.rows[0].arrivaltime2.substring(0, 5),
							"departurecity": one_stop_flight.rows[0].departurecity2,
							"arrivalcity": one_stop_flight.rows[0].arrivalcity2,
							"duration": one_stop_flight.rows[0].duration2,
							"departureairport": one_stop_flight.rows[0].departureairport2,
							"arrivalairport": one_stop_flight.rows[0].arrivalairport2,
							"flightid": one_stop_flight.rows[0].flightid2,
							"airplanemodel": one_stop_flight.rows[0].airplanemodel2,
							"classtype": one_stop_flight.rows[0].classtype2
						}
						flights.push(flight2)
						jsonResponse.push({"oneway": true})
						jsonResponse.push({"outbounddirect": false})
						jsonResponse.push({"inbounddirect": false})
						jsonResponse.push({"flights": flights})

						try {
							//returns the baggage per passenger
							const result = await pool
							.query(`SELECT h1.baggage AS baggage23kg, h2.baggage AS baggage32kg, h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg, h1.flightid , h1.reservationid, p.firstname, p.lastname, p.sex, h1.seatnumber, a1.city AS departurecity, a2.city AS arrivalcity, h1.checkin AS checkin
									FROM has h1, has h2, passenger p, flight f1, flight f2, airport a1, airport a2
									WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
										AND  h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
										AND h1.baggage = 'baggage23kg'
										AND h2.baggage = 'baggage32kg'
										AND h1.passengerid = h2.passengerid
										AND h1.flightid = h2.flightid
										AND h1.passengerid = p.passengerid
										AND h2.passengerid = p.passengerid
										AND f1.flightid = h1.flightid
										AND f2.flightid = h2.flightid
										AND f1.flightid = h2.flightid
										AND f2.flightid = h1.flightid
										AND f1.flightdate = f2.flightdate
										AND f1.departureairport = a1.name
										AND f1.arrivalairport = a2.name
									ORDER BY f1.departuretime, p.lastname ASC`)
					
							for (i = 0; i < result.rows.length; i++) {
								let passengerBaggage = {
									"firstname": result.rows[i].firstname,
									"lastname": result.rows[i].lastname,
									"gender": result.rows[i].sex,
									"flightid": result.rows[i].flightid,
									"reservationid": result.rows[i].reservationid,
									"baggage23kg": result.rows[i].numofbaggage23kg,
									"baggage32kg": result.rows[i].numofbaggage32kg,
									"seatnumber": result.rows[i].seatnumber,
									"departurecity": result.rows[i].departurecity,
									"arrivalcity": result.rows[i].arrivalcity,
									"checkin": result.rows[i].checkin
								}
								baggagePerPassenger.push(passengerBaggage)
							}
							jsonResponse.push({"baggagePerPassenger": baggagePerPassenger})
						}catch (error) {
							console.error(error)
							res.status(500).json({ error: 'Internal Server Error' })
						}
					}
				}catch (error) {
					console.error(error)
					res.status(500).json({ error: 'Internal Server Error' })
				}	
			}
		}
		else if(numOfFlights.rows[0].count === '3'){
			try{
				//one-stop go , direct return
				const  one_stop_go_direct_return= await pool
				.query(`SELECT DISTINCT TO_CHAR(f1.flightdate, 'DD/MM/YYYY') AS flightdate1, f1.departuretime AS departuretime1, f1.arrivaltime AS arrivaltime1, a1.city AS departurecity1, a2.city AS arrivalcity1, a1.name AS departureairport1, a2.name AS arrivalairport1, fd1.duration AS duration1, f1.flightid AS flightid1, ap1.model as airplanemodel1, h1.classtype AS classtype1,
						TO_CHAR(f2.flightdate, 'DD/MM/YYYY') AS flightdate2, f2.departuretime AS departuretime2, f2.arrivaltime AS arrivaltime2, a3.city AS departurecity2, a4.city AS arrivalcity2, a3.name AS departureairport2, a4.name AS arrivalairport2, fd2.duration AS duration2, f2.flightid AS flightid2, ap2.model as airplanemodel2, h2.classtype AS classtype2,
						TO_CHAR(f3.flightdate, 'DD/MM/YYYY') AS flightdate3, f3.departuretime AS departuretime3, f3.arrivaltime AS arrivaltime3, a5.city AS departurecity3, a6.city AS arrivalcity3, a5.name AS departureairport3, a6.name AS arrivalairport3, fd3.duration AS duration3, f3.flightid AS flightid3, ap3.model as airplanemodel3, h3.classtype AS classtype3
						FROM
						flight f1
						JOIN has h1 ON f1.flightid = h1.flightid
						JOIN airport a1 ON f1.departureairport = a1.name
						JOIN airport a2 ON f1.arrivalairport = a2.name
						JOIN flight_duration fd1 ON f1.departuretime = fd1.departuretime AND f1.arrivaltime = fd1.arrivaltime
						JOIN airplane ap1 ON ap1.airplaneid = f1.airplane,
						
						flight f2
						JOIN has h2 ON f2.flightid = h2.flightid
						JOIN airport a3 ON f2.departureairport = a3.name
						JOIN airport a4 ON f2.arrivalairport = a4.name
						JOIN flight_duration fd2 ON f2.departuretime = fd2.departuretime AND f2.arrivaltime = fd2.arrivaltime
						JOIN airplane ap2 ON ap2.airplaneid = f2.airplane,
						
						flight f3
						JOIN has h3 ON f3.flightid = h3.flightid
						JOIN airport a5 ON f3.departureairport = a5.name
						JOIN airport a6 ON f3.arrivalairport = a6.name
						JOIN flight_duration fd3 ON f3.departuretime = fd3.departuretime AND f3.arrivaltime = fd3.arrivaltime
						JOIN airplane ap3 ON ap3.airplaneid = f3.airplane
						
						WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
							AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
							AND h3.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
							AND f1.arrivalairport = f2.departureairport
							AND f2.flightdate = f1.flightdate
							AND f2.flightdate <= f3.flightdate
							AND f1.departureairport <> f2.arrivalairport
							AND f2.arrivalairport = f3.departureairport
							AND f1.departureairport = f3.arrivalairport
							AND h1.classtype = h2.classtype`)
			
				if(one_stop_go_direct_return.rows.length > 0){
					let flight1 = {
						"flightdate": one_stop_go_direct_return.rows[0].flightdate1,
						"departuretime": one_stop_go_direct_return.rows[0].departuretime1.substring(0, 5),
						"arrivaltime": one_stop_go_direct_return.rows[0].arrivaltime1.substring(0, 5),
						"departurecity": one_stop_go_direct_return.rows[0].departurecity1,
						"arrivalcity": one_stop_go_direct_return.rows[0].arrivalcity1,
						"duration": one_stop_go_direct_return.rows[0].duration1,
						"departureairport": one_stop_go_direct_return.rows[0].departureairport1,
						"arrivalairport": one_stop_go_direct_return.rows[0].arrivalairport1,
						"flightid": one_stop_go_direct_return.rows[0].flightid1,
						"airplanemodel": one_stop_go_direct_return.rows[0].airplanemodel1,
						"classtype": one_stop_go_direct_return.rows[0].classtype1
					}
					flights.push(flight1)
					let flight2 = {
						"flightdate": one_stop_go_direct_return.rows[0].flightdate2,
						"departuretime": one_stop_go_direct_return.rows[0].departuretime2.substring(0, 5),
						"arrivaltime": one_stop_go_direct_return.rows[0].arrivaltime2.substring(0, 5),
						"departurecity": one_stop_go_direct_return.rows[0].departurecity2,
						"arrivalcity": one_stop_go_direct_return.rows[0].arrivalcity2,
						"duration": one_stop_go_direct_return.rows[0].duration2,
						"departureairport": one_stop_go_direct_return.rows[0].departureairport2,
						"arrivalairport": one_stop_go_direct_return.rows[0].arrivalairport2,
						"flightid": one_stop_go_direct_return.rows[0].flightid2,
						"airplanemodel": one_stop_go_direct_return.rows[0].airplanemodel2,
						"classtype": one_stop_go_direct_return.rows[0].classtype2
					}
					flights.push(flight2)
					let flight3 = {
						"flightdate": one_stop_go_direct_return.rows[0].flightdate3,
						"departuretime": one_stop_go_direct_return.rows[0].departuretime3.substring(0, 5),
						"arrivaltime": one_stop_go_direct_return.rows[0].arrivaltime3.substring(0, 5),
						"departurecity": one_stop_go_direct_return.rows[0].departurecity3,
						"arrivalcity": one_stop_go_direct_return.rows[0].arrivalcity3,
						"duration": one_stop_go_direct_return.rows[0].duration3,
						"departureairport": one_stop_go_direct_return.rows[0].departureairport3,
						"arrivalairport": one_stop_go_direct_return.rows[0].arrivalairport3,
						"flightid": one_stop_go_direct_return.rows[0].flightid3,
						"airplanemodel": one_stop_go_direct_return.rows[0].airplanemodel3,
						"classtype": one_stop_go_direct_return.rows[0].classtype3
					}
					flights.push(flight3)
					jsonResponse.push({"oneway": false})
					jsonResponse.push({"outbounddirect": false})
					jsonResponse.push({"inbounddirect": true})
					jsonResponse.push({"flights": flights})

					try {
						//returns the baggage per passenger
						const result = await pool
						.query(`SELECT h1.baggage AS baggage23kg, h2.baggage AS baggage32kg, h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg, h1.flightid , h1.reservationid, p.firstname, p.lastname, p.sex, h1.seatnumber, a1.city AS departurecity, a2.city AS arrivalcity, h1.checkin AS checkin
								FROM has h1, has h2, passenger p, flight f1, flight f2, flight f3, airport a1, airport a2
								WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
										AND  h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
										AND h1.baggage = 'baggage23kg'
										AND h2.baggage = 'baggage32kg'
										AND h1.passengerid = h2.passengerid
										AND h1.flightid = h2.flightid
										AND h1.passengerid = p.passengerid
										AND h2.passengerid = p.passengerid
										AND f1.flightid = h1.flightid
										AND f2.flightid = h2.flightid
										AND f1.flightid = h2.flightid
										AND f2.flightid = h1.flightid
										AND f3.flightid = h1.flightid
										AND f3.flightid = h2.flightid
										AND f2.flightdate = f1.flightdate
										AND f2.flightdate <= f3.flightdate
										AND f1.departureairport = a1.name
										AND f1.arrivalairport = a2.name
								ORDER BY f1.flightdate, f2.flightdate, f3.flightdate, f1.departuretime, f2.departuretime, p.lastname ASC`)
				
						for (i = 0; i < result.rows.length; i++) {
							let passengerBaggage = {
								"firstname": result.rows[i].firstname,
								"lastname": result.rows[i].lastname,
								"gender": result.rows[i].sex,
								"flightid": result.rows[i].flightid,
								"reservationid": result.rows[i].reservationid,
								"baggage23kg": result.rows[i].numofbaggage23kg,
								"baggage32kg": result.rows[i].numofbaggage32kg,
								"seatnumber": result.rows[i].seatnumber,
								"departurecity": result.rows[i].departurecity,
								"arrivalcity": result.rows[i].arrivalcity,
								"checkin": result.rows[i].checkin
							}
							baggagePerPassenger.push(passengerBaggage)
						}
						jsonResponse.push({"baggagePerPassenger": baggagePerPassenger})
					}catch (error) {
						console.error(error)
						res.status(500).json({ error: 'Internal Server Error' })
					}
				}
			}catch (error) {
				console.error(error)
				res.status(500).json({ error: 'Internal Server Error' })
			}
			if(flights.length === 0){
				try{
					//returns direct go and ones-stop return flights
					const direct_go_one_stop_return = await pool
					.query(`SELECT DISTINCT TO_CHAR(f1.flightdate, 'DD/MM/YYYY') AS flightdate1, f1.departuretime AS departuretime1, f1.arrivaltime AS arrivaltime1, a1.city AS departurecity1, a2.city AS arrivalcity1, a1.name AS departureairport1, a2.name AS arrivalairport1, fd1.duration AS duration1, f1.flightid AS flightid1, ap1.model as airplanemodel1, h1.classtype AS classtype1,
							TO_CHAR(f2.flightdate, 'DD/MM/YYYY') AS flightdate2, f2.departuretime AS departuretime2, f2.arrivaltime AS arrivaltime2, a3.city AS departurecity2, a4.city AS arrivalcity2, a3.name AS departureairport2, a4.name AS arrivalairport2, fd2.duration AS duration2, f2.flightid AS flightid2, ap2.model as airplanemodel2, h2.classtype AS classtype2,
							TO_CHAR(f3.flightdate, 'DD/MM/YYYY') AS flightdate3, f3.departuretime AS departuretime3, f3.arrivaltime AS arrivaltime3, a5.city AS departurecity3, a6.city AS arrivalcity3, a5.name AS departureairport3, a6.name AS arrivalairport3, fd3.duration AS duration3, f3.flightid AS flightid3, ap3.model as airplanemodel3, h3.classtype AS classtype3
							FROM
							flight f1
							JOIN has h1 ON f1.flightid = h1.flightid
							JOIN airport a1 ON f1.departureairport = a1.name
							JOIN airport a2 ON f1.arrivalairport = a2.name
							JOIN flight_duration fd1 ON f1.departuretime = fd1.departuretime AND f1.arrivaltime = fd1.arrivaltime
							JOIN airplane ap1 ON ap1.airplaneid = f1.airplane,
							
							flight f2
							JOIN has h2 ON f2.flightid = h2.flightid
							JOIN airport a3 ON f2.departureairport = a3.name
							JOIN airport a4 ON f2.arrivalairport = a4.name
							JOIN flight_duration fd2 ON f2.departuretime = fd2.departuretime AND f2.arrivaltime = fd2.arrivaltime
							JOIN airplane ap2 ON ap2.airplaneid = f2.airplane,
							
							flight f3
							JOIN has h3 ON f3.flightid = h3.flightid
							JOIN airport a5 ON f3.departureairport = a5.name
							JOIN airport a6 ON f3.arrivalairport = a6.name
							JOIN flight_duration fd3 ON f3.departuretime = fd3.departuretime AND f3.arrivaltime = fd3.arrivaltime
							JOIN airplane ap3 ON ap3.airplaneid = f3.airplane
							
							WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND h3.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND f1.arrivalairport = f2.departureairport
								AND f1.flightdate <= f2.flightdate
								AND f2.arrivalairport = f3.departureairport
								AND f3.arrivalairport = f1.departureairport
								AND f2.flightdate = f3.flightdate`)
			
					if(direct_go_one_stop_return.rows.length > 0){
						let flight1 = {
							"flightdate": direct_go_one_stop_return.rows[0].flightdate1,
							"departuretime": direct_go_one_stop_return.rows[0].departuretime1.substring(0, 5),
							"arrivaltime": direct_go_one_stop_return.rows[0].arrivaltime1.substring(0, 5),
							"departurecity": direct_go_one_stop_return.rows[0].departurecity1,
							"arrivalcity": direct_go_one_stop_return.rows[0].arrivalcity1,
							"duration": direct_go_one_stop_return.rows[0].duration1,
							"departureairport": direct_go_one_stop_return.rows[0].departureairport1,
							"arrivalairport": direct_go_one_stop_return.rows[0].arrivalairport1,
							"flightid": direct_go_one_stop_return.rows[0].flightid1,
							"airplanemodel": direct_go_one_stop_return.rows[0].airplanemodel1,
							"classtype": direct_go_one_stop_return.rows[0].classtype1
						}
						flights.push(flight1)
						let flight2 = {
							"flightdate": direct_go_one_stop_return.rows[0].flightdate2,
							"departuretime": direct_go_one_stop_return.rows[0].departuretime2.substring(0, 5),
							"arrivaltime": direct_go_one_stop_return.rows[0].arrivaltime2.substring(0, 5),
							"departurecity": direct_go_one_stop_return.rows[0].departurecity2,
							"arrivalcity": direct_go_one_stop_return.rows[0].arrivalcity2,
							"duration": direct_go_one_stop_return.rows[0].duration2,
							"departureairport": direct_go_one_stop_return.rows[0].departureairport2,
							"arrivalairport": direct_go_one_stop_return.rows[0].arrivalairport2,
							"flightid": direct_go_one_stop_return.rows[0].flightid2,
							"airplanemodel": direct_go_one_stop_return.rows[0].airplanemodel2,
							"classtype": direct_go_one_stop_return.rows[0].classtype2
						}
						flights.push(flight2)
						let flight3 = {
							"flightdate": direct_go_one_stop_return.rows[0].flightdate3,
							"departuretime": direct_go_one_stop_return.rows[0].departuretime3.substring(0, 5),
							"arrivaltime": direct_go_one_stop_return.rows[0].arrivaltime3.substring(0, 5),
							"departurecity": direct_go_one_stop_return.rows[0].departurecity3,
							"arrivalcity": direct_go_one_stop_return.rows[0].arrivalcity3,
							"duration": direct_go_one_stop_return.rows[0].duration3,
							"departureairport": direct_go_one_stop_return.rows[0].departureairport3,
							"arrivalairport": direct_go_one_stop_return.rows[0].arrivalairport3,
							"flightid": direct_go_one_stop_return.rows[0].flightid3,
							"airplanemodel": direct_go_one_stop_return.rows[0].airplanemodel3,
							"classtype": direct_go_one_stop_return.rows[0].classtype3
						}
						flights.push(flight3)
						jsonResponse.push({"oneway": false})
						jsonResponse.push({"outbounddirect": true})
						jsonResponse.push({"inbounddirect": false})
						jsonResponse.push({"flights": flights})

						try {
							//returns the baggage per passenger
							const result = await pool
							.query(`SELECT h1.baggage AS baggage23kg, h2.baggage AS baggage32kg, h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg, h1.flightid , h1.reservationid, p.firstname, p.lastname, p.sex, h1.seatnumber, a1.city AS departurecity, a2.city AS arrivalcity, h1.checkin AS checkin
									FROM has h1, has h2, passenger p, flight f1, flight f2, flight f3, airport a1, airport a2
									WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
											AND  h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
											AND h1.baggage = 'baggage23kg'
											AND h2.baggage = 'baggage32kg'
											AND h1.passengerid = h2.passengerid
											AND h1.flightid = h2.flightid
											AND h1.passengerid = p.passengerid
											AND h2.passengerid = p.passengerid
											AND f1.flightid = h1.flightid
											AND f2.flightid = h2.flightid
											AND f1.flightid = h2.flightid
											AND f2.flightid = h1.flightid
											AND f3.flightid = h1.flightid
											AND f3.flightid = h2.flightid
											AND f1.flightdate <= f2.flightdate
											AND f2.flightdate = f3.flightdate
											AND f1.departureairport = a1.name
											AND f1.arrivalairport = a2.name
									ORDER BY f1.flightdate, f2.flightdate, f3.flightdate , f1.departuretime, f2.departuretime, p.lastname ASC`)
					
							for (i = 0; i < result.rows.length; i++) {
								let passengerBaggage = {
									"firstname": result.rows[i].firstname,
									"lastname": result.rows[i].lastname,
									"gender": result.rows[i].sex,
									"flightid": result.rows[i].flightid,
									"reservationid": result.rows[i].reservationid,
									"baggage23kg": result.rows[i].numofbaggage23kg,
									"baggage32kg": result.rows[i].numofbaggage32kg,
									"seatnumber": result.rows[i].seatnumber,
									"departurecity": result.rows[i].departurecity,
									"arrivalcity": result.rows[i].arrivalcity,
									"checkin": result.rows[i].checkin
								}
								baggagePerPassenger.push(passengerBaggage)
							}
							jsonResponse.push({"baggagePerPassenger": baggagePerPassenger})
						}catch (error) {
							console.error(error)
							res.status(500).json({ error: 'Internal Server Error' })
						}
					}
				} catch (error) {
					console.error(error)
					res.status(500).json({ error: 'Internal Server Error' })
				}	
			}
		}
		else if(numOfFlights.rows[0].count === '4'){
			try{
				//one-stop go , one-stop return
				const one_stop_go_one_stop_return = await pool
				.query(`SELECT DISTINCT TO_CHAR(f1.flightdate, 'DD/MM/YYYY') AS flightdate1, f1.departuretime AS departuretime1, f1.arrivaltime AS arrivaltime1, a1.city AS departurecity1, a2.city AS arrivalcity1, a1.name AS departureairport1, a2.name AS arrivalairport1, fd1.duration AS duration1, f1.flightid AS flightid1, ap1.model as airplanemodel1, h1.classtype AS classtype1,
						TO_CHAR(f2.flightdate, 'DD/MM/YYYY') AS flightdate2, f2.departuretime AS departuretime2, f2.arrivaltime AS arrivaltime2, a3.city AS departurecity2, a4.city AS arrivalcity2, a3.name AS departureairport2, a4.name AS arrivalairport2, fd2.duration AS duration2, f2.flightid AS flightid2, ap2.model as airplanemodel2, h2.classtype AS classtype2,
						TO_CHAR(f3.flightdate, 'DD/MM/YYYY') AS flightdate3, f3.departuretime AS departuretime3, f3.arrivaltime AS arrivaltime3, a5.city AS departurecity3, a6.city AS arrivalcity3, a5.name AS departureairport3, a6.name AS arrivalairport3, fd3.duration AS duration3, f3.flightid AS flightid3, ap3.model as airplanemodel3, h3.classtype AS classtype3,
						TO_CHAR(f4.flightdate, 'DD/MM/YYYY') AS flightdate4, f4.departuretime AS departuretime4, f4.arrivaltime AS arrivaltime4, a7.city AS departurecity4, a8.city AS arrivalcity4, a7.name AS departureairport4, a8.name AS arrivalairport4, fd4.duration AS duration4, f4.flightid AS flightid4, ap4.model as airplanemodel4, h4.classtype AS classtype4
						FROM
						flight f1
						JOIN has h1 ON f1.flightid = h1.flightid
						JOIN airport a1 ON f1.departureairport = a1.name
						JOIN airport a2 ON f1.arrivalairport = a2.name
						JOIN flight_duration fd1 ON f1.departuretime = fd1.departuretime AND f1.arrivaltime = fd1.arrivaltime
						JOIN airplane ap1 ON ap1.airplaneid = f1.airplane,
						
						flight f2
						JOIN has h2 ON f2.flightid = h2.flightid
						JOIN airport a3 ON f2.departureairport = a3.name
						JOIN airport a4 ON f2.arrivalairport = a4.name
						JOIN flight_duration fd2 ON f2.departuretime = fd2.departuretime AND f2.arrivaltime = fd2.arrivaltime
						JOIN airplane ap2 ON ap2.airplaneid = f2.airplane,
						
						flight f3
						JOIN has h3 ON f3.flightid = h3.flightid
						JOIN airport a5 ON f3.departureairport = a5.name
						JOIN airport a6 ON f3.arrivalairport = a6.name
						JOIN flight_duration fd3 ON f3.departuretime = fd3.departuretime AND f3.arrivaltime = fd3.arrivaltime
						JOIN airplane ap3 ON ap3.airplaneid = f3.airplane,

						flight f4
						JOIN has h4 ON f4.flightid = h4.flightid
						JOIN airport a7 ON f4.departureairport = a7.name
						JOIN airport a8 ON f4.arrivalairport = a8.name
						JOIN flight_duration fd4 ON f4.departuretime = fd4.departuretime AND f4.arrivaltime = fd4.arrivaltime
						JOIN airplane ap4 ON ap4.airplaneid = f4.airplane

						WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND h3.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND h4.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND f1.arrivalairport = f2.departureairport
								AND f2.arrivalairport = f3.departureairport
								AND f1.flightdate = f2.flightdate
								AND f1.flightdate <= f3.flightdate
								AND f3.arrivalairport = f4.departureairport
								AND f4.arrivalairport = f1.departureairport	
								AND f3.flightdate = f4.flightdate`)
			
				if(one_stop_go_one_stop_return.rows.length > 0){
					let flight1 = {
						"flightdate": one_stop_go_one_stop_return.rows[0].flightdate1,
						"departuretime": one_stop_go_one_stop_return.rows[0].departuretime1.substring(0, 5),
						"arrivaltime": one_stop_go_one_stop_return.rows[0].arrivaltime1.substring(0, 5),
						"departurecity": one_stop_go_one_stop_return.rows[0].departurecity1,
						"arrivalcity": one_stop_go_one_stop_return.rows[0].arrivalcity1,
						"duration": one_stop_go_one_stop_return.rows[0].duration1,
						"departureairport": one_stop_go_one_stop_return.rows[0].departureairport1,
						"arrivalairport": one_stop_go_one_stop_return.rows[0].arrivalairport1,
						"flightid": one_stop_go_one_stop_return.rows[0].flightid1,
						"airplanemodel": one_stop_go_one_stop_return.rows[0].airplanemodel1,
						"classtype": one_stop_go_one_stop_return.rows[0].classtype1
					}
					flights.push(flight1)
					let flight2 = {
						"flightdate": one_stop_go_one_stop_return.rows[0].flightdate2,
						"departuretime": one_stop_go_one_stop_return.rows[0].departuretime2.substring(0, 5),
						"arrivaltime": one_stop_go_one_stop_return.rows[0].arrivaltime2.substring(0, 5),
						"departurecity": one_stop_go_one_stop_return.rows[0].departurecity2,
						"arrivalcity": one_stop_go_one_stop_return.rows[0].arrivalcity2,
						"duration": one_stop_go_one_stop_return.rows[0].duration2,
						"departureairport": one_stop_go_one_stop_return.rows[0].departureairport2,
						"arrivalairport": one_stop_go_one_stop_return.rows[0].arrivalairport2,
						"flightid": one_stop_go_one_stop_return.rows[0].flightid2,
						"airplanemodel": one_stop_go_one_stop_return.rows[0].airplanemodel2,
						"classtype": one_stop_go_one_stop_return.rows[0].classtype2
					}
					flights.push(flight2)
					let flight3 = {
						"flightdate": one_stop_go_one_stop_return.rows[0].flightdate3,
						"departuretime": one_stop_go_one_stop_return.rows[0].departuretime3.substring(0, 5),
						"arrivaltime": one_stop_go_one_stop_return.rows[0].arrivaltime3.substring(0, 5),
						"departurecity": one_stop_go_one_stop_return.rows[0].departurecity3,
						"arrivalcity": one_stop_go_one_stop_return.rows[0].arrivalcity3,
						"duration": one_stop_go_one_stop_return.rows[0].duration3,
						"departureairport": one_stop_go_one_stop_return.rows[0].departureairport3,
						"arrivalairport": one_stop_go_one_stop_return.rows[0].arrivalairport3,
						"flightid": one_stop_go_one_stop_return.rows[0].flightid3,
						"airplanemodel": one_stop_go_one_stop_return.rows[0].airplanemodel3,
						"classtype": one_stop_go_one_stop_return.rows[0].classtype3
					}
					flights.push(flight3)
					let flight4 = {
						"flightdate": one_stop_go_one_stop_return.rows[0].flightdate4,
						"departuretime": one_stop_go_one_stop_return.rows[0].departuretime4.substring(0, 5),
						"arrivaltime": one_stop_go_one_stop_return.rows[0].arrivaltime4.substring(0, 5),
						"departurecity": one_stop_go_one_stop_return.rows[0].departurecity4,
						"arrivalcity": one_stop_go_one_stop_return.rows[0].arrivalcity4,
						"duration": one_stop_go_one_stop_return.rows[0].duration4,
						"departureairport": one_stop_go_one_stop_return.rows[0].departureairport4,
						"arrivalairport": one_stop_go_one_stop_return.rows[0].arrivalairport4,
						"flightid": one_stop_go_one_stop_return.rows[0].flightid4,
						"airplanemodel": one_stop_go_one_stop_return.rows[0].airplanemodel4,
						"classtype": one_stop_go_one_stop_return.rows[0].classtype4
					}
					flights.push(flight4)
					jsonResponse.push({"oneway": false})
					jsonResponse.push({"outbounddirect": false})
					jsonResponse.push({"inbounddirect": false})
					jsonResponse.push({"flights": flights})

					try {
						//returns the baggage per passenger
						const result = await pool
						.query(`SELECT h1.baggage AS baggage23kg, h2.baggage AS baggage32kg, h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg, h1.flightid , h1.reservationid, p.firstname, p.lastname, p.sex, h1.seatnumber, a1.city AS departurecity, a2.city AS arrivalcity, h1.checkin AS checkin
								FROM has h1, has h2, passenger p, flight f1, flight f2, flight f3, flight f4, airport a1, airport a2
								WHERE h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
										AND  h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
										AND h1.baggage = 'baggage23kg'
										AND h2.baggage = 'baggage32kg'
										AND h1.passengerid = h2.passengerid
										AND h1.flightid = h2.flightid
										AND h1.passengerid = p.passengerid
										AND h2.passengerid = p.passengerid
										AND f1.flightid = h1.flightid
										AND f2.flightid = h2.flightid
										AND f1.flightid = h2.flightid
										AND f2.flightid = h1.flightid
										AND f3.flightid = h1.flightid
										AND f3.flightid = h2.flightid
										AND f4.flightid = h1.flightid
										AND f4.flightid = h2.flightid
										AND f1.flightdate = f2.flightdate
										AND f1.flightdate <= f3.flightdate
										AND f1.departureairport = a1.name
										AND f1.arrivalairport = a2.name
									ORDER BY f1.flightdate, f2.flightdate, f3.flightdate, f4.flightdate, f1.departuretime, f2.departuretime, p.lastname ASC`)
					
				
						for (i = 0; i < result.rows.length; i++) {
							let passengerBaggage = {
								"firstname": result.rows[i].firstname,
								"lastname": result.rows[i].lastname,
								"gender": result.rows[i].sex,
								"flightid": result.rows[i].flightid,
								"reservationid": result.rows[i].reservationid,
								"baggage23kg": result.rows[i].numofbaggage23kg,
								"baggage32kg": result.rows[i].numofbaggage32kg,
								"seatnumber": result.rows[i].seatnumber,
								"departurecity": result.rows[i].departurecity,
								"arrivalcity": result.rows[i].arrivalcity,
								"checkin": result.rows[i].checkin
							}
							baggagePerPassenger.push(passengerBaggage)
						}
						jsonResponse.push({"baggagePerPassenger": baggagePerPassenger})
					}catch (error) {
						console.error(error)
						res.status(500).json({ error: 'Internal Server Error' })
					}
				}
			}catch (error) {
				console.error(error)
				res.status(500).json({ error: 'Internal Server Error' })
			}
		}

	} catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
	}

	try {
		//returns the passengers info
		const result = await pool
		.query(`SELECT COUNT(DISTINCT p.passengerid), p.sex, p.lastname, p.firstname, TO_CHAR(p.birthdate, 'DD/MM/YYYY') AS birthdate, p.email, p.phonenumber, h.reservationid 
				FROM passenger p, has h
				WHERE p.passengerid = h.passengerid  AND h.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
				GROUP BY p.sex, p.lastname, p.firstname, h.reservationid, p.birthdate, p.email, p.phonenumber
				ORDER BY p.lastname ASC`)
		
		for (i = 0; i < result.rows.length; i++) {
			let passenger = {
				"gender": result.rows[i].sex,
				"firstname": result.rows[i].firstname,
				"lastname": result.rows[i].lastname,
				"birthdate": result.rows[i].birthdate,
				"email": result.rows[i].email,
				"phonenumber": result.rows[i].phonenumber
			}
			passengers.push(passenger)
		}
		jsonResponse.push({"numofpassengers": passengers.length})
		jsonResponse.push({"bookingid": result.rows[0].reservationid})
		jsonResponse.push({"passengers": passengers})

	} catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
	}

	try {
		//returns the petsize if exists
		const result = await pool
		.query(`SELECT r.petsize
				FROM reservation r
				WHERE r.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'`)

		jsonResponse.push({"petsize": result.rows[0].petsize})
	}catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
	}

	try {
		//returns the wifi type
		const result = await pool
		.query(`SELECT r.wifionboard
				FROM reservation r
				WHERE r.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'`)

		jsonResponse.push({"wifionboard": result.rows[0].wifionboard})
	}catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
	}

	try {
		//returns the car booking
		const result = await pool.query(`
			SELECT c.carimage, c.company, c.model, con.rentingprice, 
			c.location, TO_CHAR(con.pickup, 'DD/MM/YYYY HH24:MI') AS pickup, TO_CHAR(con.return, 'DD/MM/YYYY HH24:MI') AS return
			FROM contains con, car c
			WHERE con.carid=c.carid 
			AND con.reservationid='${jsonArray[0].bookingid.toUpperCase()}'`
		)

		let json = []
		for(let i=0; i<result.rows.length; i++) {
			const imageData = result.rows[i].carimage
			// Convert the bytea data to base64
			const base64ImageData = imageData.toString('base64')
			json.push({
				carimage: base64ImageData,
				company: result.rows[i].company,
				model: result.rows[i].model,
				price: result.rows[i].rentingprice,
				location: result.rows[i].location,
				pickup: result.rows[i].pickup,
				return: result.rows[i].return
			})
		}
        // Send the base64-encoded image data in the response with company and model of cars
        jsonResponse.push({"cars": json})

		const result1 = await pool.query(`
			SELECT price
			FROM reservation 
			WHERE reservationid='${jsonArray[0].bookingid.toUpperCase()}'`
		)
		jsonResponse.push({"bookingPrice": result1.rows[0].price})
	}catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
	}

	res.json(jsonResponse)
})

//POST api that returns all the checkin details for flights, passengers, baggage, 
//seats and wifi of the reservation and the check is open 24 hours to 30 minutes before flight
app.post('/flynow/checkin-details', async (req, res) => {
	const jsonArray = req.body
	let flights = []
	let passengers = []
	let baggagePerPassenger = []
	let jsonResponse = []
	let numOfFlightsToCheckin = []
	let i
	try {
		//find flights for check in
		const flight1 = await pool
		.query(`WITH CurrentTimeFormatted AS (
					SELECT 
						TO_CHAR(CURRENT_TIMESTAMP, 'HH24:MI:SS') || '+' || 
						LPAD(EXTRACT(TIMEZONE_HOUR FROM CURRENT_TIMESTAMP)::TEXT, 2, '0') || ':' || 
						LPAD(EXTRACT(TIMEZONE_MINUTE FROM CURRENT_TIMESTAMP)::TEXT, 2, '0') AS formatted_current_time
				)
				
				SELECT DISTINCT f.flightid, f.departuretime, f.flightdate, f.arrivaltime
				FROM 
					flight f
					JOIN has h ON h.flightid = f.flightid
				WHERE 
					h.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
					AND f.flightdate = CURRENT_DATE + INTERVAL '1 day'
					AND f.departuretime <= (CAST((SELECT formatted_current_time FROM CurrentTimeFormatted) AS TIME WITH TIME ZONE) + INTERVAL '24 hours' - INTERVAL '1 second')
					AND h.checkin = 'false'
					OR (f.flightdate = CURRENT_DATE 
						AND f.departuretime >= (CAST((SELECT formatted_current_time FROM CurrentTimeFormatted) AS TIME WITH TIME ZONE)+INTERVAL '30 minutes')
						AND h.checkin = 'false')
				GROUP BY f.flightid, f.flightdate, f.arrivaltime
				ORDER BY f.departuretime`)

		if(flight1.rows.length > 0){
			const flight2 = await pool
			.query(`SELECT DISTINCT f2.flightid, f2.departuretime, f2.flightdate, f2.arrivaltime
					FROM 
						flight f1
						JOIN has h1 ON h1.flightid = f1.flightid,
	
						flight f2
						JOIN has h2 ON h2.flightid = f2.flightid
					WHERE f1.flightid = '${flight1.rows[0].flightid}'
						AND h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
						AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
						AND f2.flightdate = f1.flightdate 
						AND f2.departureairport = f1.arrivalairport
						AND f2.departuretime > f1.arrivaltime
					GROUP BY f2.flightid, f2.flightdate, f2.arrivaltime
					ORDER BY f2.departuretime`)
			if(flight2.rows.length === 0){
				numOfFlightsToCheckin.push(flight1.rows[0])
			}
			else{
				numOfFlightsToCheckin.push(flight1.rows[0])
				numOfFlightsToCheckin.push(flight2.rows[0])
			}
			console.log(numOfFlightsToCheckin)
		}
		else{
			numOfFlightsToCheckin = flight1.rows
		}
		
		if (numOfFlightsToCheckin.length > 0) {
			jsonResponse.push({"numofflightstocheckin": numOfFlightsToCheckin.length})
			if(numOfFlightsToCheckin.length === 1){
				try{
					//one way direct flight
					const direct_flight = await pool
					.query(`SELECT DISTINCT TO_CHAR(f.flightdate, 'DD/MM/YYYY') AS flightdate, f.departuretime, f.arrivaltime, a1.city AS departurecity, a2.city AS arrivalcity, a1.name AS departureairport, a2.name AS arrivalairport, fd.duration, f.flightid, ap.model as airplanemodel, h.classtype
							FROM flight f, has h, airport a1, airport a2, flight_duration fd, airplane ap
							WHERE f.flightid = '${numOfFlightsToCheckin[0].flightid}'
									AND h.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
									AND f.flightid = h.flightid
									AND f.departureairport = a1.name
									AND f.arrivalairport = a2.name
									AND f.departuretime = fd.departuretime
									AND f.arrivaltime = fd.arrivaltime
									AND f.departuretime < f.arrivaltime
									AND ap.airplaneid=f.airplane`)
					let flight = {
						"flightdate": direct_flight.rows[0].flightdate,
						"departuretime": direct_flight.rows[0].departuretime.substring(0, 5),
						"arrivaltime": direct_flight.rows[0].arrivaltime.substring(0, 5),
						"departurecity": direct_flight.rows[0].departurecity,
						"arrivalcity": direct_flight.rows[0].arrivalcity,
						"duration": direct_flight.rows[0].duration,
						"departureairport": direct_flight.rows[0].departureairport,
						"arrivalairport": direct_flight.rows[0].arrivalairport,
						"flightid":  direct_flight.rows[0].flightid,
						"airplanemodel": direct_flight.rows[0].airplanemodel,
						"classtype": direct_flight.rows[0].classtype
					}
					flights.push(flight)
					jsonResponse.push({"direct": true})
					jsonResponse.push({"flights": flights})
				}catch (error) {
					console.error(error)
					res.status(500).json({ error: 'Internal Server Error' })
				}
				try {
					//returns the baggage per passenger
					const result = await pool
					.query(`SELECT h1.baggage AS baggage23kg, h2.baggage AS baggage32kg, h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg, h1.flightid , h1.reservationid, p.firstname, p.lastname, p.sex, h1.seatnumber, a1.city AS departurecity, a2.city AS arrivalcity, h1.checkin
							FROM flight f, has h1, has h2, passenger p, airport a1, airport a2
							WHERE h1.flightid = '${numOfFlightsToCheckin[0].flightid}'
									AND h2.flightid = '${numOfFlightsToCheckin[0].flightid}'
									AND f.flightid = h1.flightid
									AND h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
									AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
									AND h1.baggage = 'baggage23kg'
									AND h2.baggage = 'baggage32kg'
									AND h1.passengerid = h2.passengerid
									AND h1.flightid = h2.flightid
									AND h1.passengerid = p.passengerid
									AND h2.passengerid = p.passengerid
									AND f.departureairport = a1.name
									AND f.arrivalairport = a2.name
							ORDER BY p.lastname ASC`)
			
					for (i = 0; i < result.rows.length; i++) {
						let passengerBaggage = {
							"firstname": result.rows[i].firstname,
							"lastname": result.rows[i].lastname,
							"gender": result.rows[i].sex,
							"flightid": result.rows[i].flightid,
							"reservationid": result.rows[i].reservationid,
							"baggage23kg": result.rows[i].numofbaggage23kg,
							"baggage32kg": result.rows[i].numofbaggage32kg,
							"seatnumber": result.rows[i].seatnumber,
							"departurecity": result.rows[i].departurecity,
							"arrivalcity": result.rows[i].arrivalcity,
							"checkin": result.rows[i].checkin
						}
						baggagePerPassenger.push(passengerBaggage)
					}
					jsonResponse.push({"baggagePerPassenger": baggagePerPassenger})
				}catch (error) {
					console.error(error)
					res.status(500).json({ error: 'Internal Server Error' })
				}
			}
			else if(numOfFlightsToCheckin.length === 2){
				try{
					//returns oneway one-stop flight
					const one_stop_flight = await pool
					.query(`SELECT DISTINCT TO_CHAR(f1.flightdate, 'DD/MM/YYYY') AS flightdate1, f1.departuretime AS departuretime1, f1.arrivaltime AS arrivaltime1, a1.city AS departurecity1, a2.city AS arrivalcity1, a1.name AS departureairport1, a2.name AS arrivalairport1, fd1.duration AS duration1, f1.flightid AS flightid1, ap1.model as airplanemodel1, h1.classtype AS classtype1,
								TO_CHAR(f2.flightdate, 'DD/MM/YYYY') AS flightdate2, f2.departuretime AS departuretime2, f2.arrivaltime AS arrivaltime2, a3.city AS departurecity2, a4.city AS arrivalcity2, a3.name AS departureairport2, a4.name AS arrivalairport2, fd2.duration AS duration2, f2.flightid AS flightid2, ap2.model as airplanemodel2, h2.classtype AS classtype2
							FROM
								flight f1
								JOIN has h1 ON f1.flightid = h1.flightid
								JOIN airport a1 ON f1.departureairport = a1.name
								JOIN airport a2 ON f1.arrivalairport = a2.name
								JOIN flight_duration fd1 ON f1.departuretime = fd1.departuretime AND f1.arrivaltime = fd1.arrivaltime
								JOIN airplane ap1 ON ap1.airplaneid = f1.airplane,
								
								flight f2
								JOIN has h2 ON f2.flightid = h2.flightid
								JOIN airport a3 ON f2.departureairport = a3.name
								JOIN airport a4 ON f2.arrivalairport = a4.name
								JOIN flight_duration fd2 ON f2.departuretime = fd2.departuretime AND f2.arrivaltime = fd2.arrivaltime
								JOIN airplane ap2 ON ap2.airplaneid = f2.airplane
							
							WHERE f1.flightid = '${numOfFlightsToCheckin[0].flightid}'
								AND f2.flightid = '${numOfFlightsToCheckin[1].flightid}'
								AND h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
								AND f1.arrivalairport = f2.departureairport
								AND f2.flightdate = f1.flightdate
								AND f1.departureairport <> f2.arrivalairport`)
					
					let flight1 = {
						"flightdate": one_stop_flight.rows[0].flightdate1,
						"departuretime": one_stop_flight.rows[0].departuretime1.substring(0, 5),
						"arrivaltime": one_stop_flight.rows[0].arrivaltime1.substring(0, 5),
						"departurecity": one_stop_flight.rows[0].departurecity1,
						"arrivalcity": one_stop_flight.rows[0].arrivalcity1,
						"duration": one_stop_flight.rows[0].duration1,
						"departureairport": one_stop_flight.rows[0].departureairport1,
						"arrivalairport": one_stop_flight.rows[0].arrivalairport1,
						"flightid": one_stop_flight.rows[0].flightid1,
						"airplanemodel": one_stop_flight.rows[0].airplanemodel1,
						"classtype": one_stop_flight.rows[0].classtype1
					}
					flights.push(flight1)
					let flight2 = {
						"flightdate": one_stop_flight.rows[0].flightdate2,
						"departuretime": one_stop_flight.rows[0].departuretime2.substring(0, 5),
						"arrivaltime": one_stop_flight.rows[0].arrivaltime2.substring(0, 5),
						"departurecity": one_stop_flight.rows[0].departurecity2,
						"arrivalcity": one_stop_flight.rows[0].arrivalcity2,
						"duration": one_stop_flight.rows[0].duration2,
						"departureairport": one_stop_flight.rows[0].departureairport2,
						"arrivalairport": one_stop_flight.rows[0].arrivalairport2,
						"flightid": one_stop_flight.rows[0].flightid2,
						"airplanemodel": one_stop_flight.rows[0].airplanemodel2,
						"classtype": one_stop_flight.rows[0].classtype2
					}
					flights.push(flight2)
				
					jsonResponse.push({"direct": false})
					jsonResponse.push({"flights": flights})

					try {
						//returns the baggage per passenger
						const result = await pool
						.query(`SELECT DISTINCT h1.baggage AS baggage23kg1, h2.baggage AS baggage32kg1, h1.numofbaggagepercategory AS numofbaggage23kg1, h2.numofbaggagepercategory AS numofbaggage32kg1, h1.flightid AS flightid1, h1.reservationid AS reservationid1, p.firstname AS firstname1, p.lastname AS lastname1, p.sex AS sex1, h1.seatnumber AS seatnumber1, a1.city AS departurecity1, a2.city AS arrivalcity1, h1.checkin AS checkin1,
								h1.baggage AS baggage23kg2, h2.baggage AS baggage32kg2, h1.numofbaggagepercategory AS numofbaggage23kg2, h2.numofbaggagepercategory AS numofbaggage32kg2, h2.flightid AS flightid2, h2.reservationid AS reservationid2, p.firstname AS firstname2, p.lastname AS lastname2, p.sex AS sex2, h2.seatnumber AS seatnumber2, a3.city AS departurecity2, a4.city AS arrivalcity2, h2.checkin AS checkin2, f1.departuretime, f2.departuretime
								FROM passenger p,
								flight f1
								JOIN has h1 ON f1.flightid = h1.flightid
								JOIN airport a1 ON f1.departureairport = a1.name
								JOIN airport a2 ON f1.arrivalairport = a2.name
								JOIN flight_duration fd1 ON f1.departuretime = fd1.departuretime AND f1.arrivaltime = fd1.arrivaltime
								JOIN airplane ap1 ON ap1.airplaneid = f1.airplane,
				
								flight f2
								JOIN has h2 ON f2.flightid = h2.flightid
								JOIN airport a3 ON f2.departureairport = a3.name
								JOIN airport a4 ON f2.arrivalairport = a4.name
								JOIN flight_duration fd2 ON f2.departuretime = fd2.departuretime AND f2.arrivaltime = fd2.arrivaltime
								JOIN airplane ap2 ON ap2.airplaneid = f2.airplane
				
								WHERE f1.flightid = '${numOfFlightsToCheckin[0].flightid}'
									AND f2.flightid = '${numOfFlightsToCheckin[1].flightid}'
									AND h1.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
									AND h2.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
									AND h1.baggage = 'baggage23kg'
									AND h2.baggage = 'baggage32kg'
									AND h1.passengerid = h2.passengerid
									AND h1.passengerid = p.passengerid
									AND h2.passengerid = p.passengerid
									AND f1.arrivalairport = f2.departureairport
									AND f2.flightdate = f1.flightdate
									AND f1.departureairport <> f2.arrivalairport
								ORDER BY f1.departuretime, f2.departuretime, p.lastname ASC`)
				
						for(i=0; i<result.rows.length; i++){
							let passengerBaggage1 = {
								"firstname": result.rows[i].firstname1,
								"lastname": result.rows[i].lastname1,
								"gender": result.rows[i].sex1,
								"flightid": result.rows[i].flightid1,
								"reservationid": result.rows[i].reservationid1,
								"baggage23kg": result.rows[i].numofbaggage23kg1,
								"baggage32kg": result.rows[i].numofbaggage32kg1,
								"seatnumber": result.rows[i].seatnumber1,
								"departurecity": result.rows[i].departurecity1,
								"arrivalcity": result.rows[i].arrivalcity1,
								"checkin": result.rows[i].checkin1
							}
							baggagePerPassenger.push(passengerBaggage1)
						}

						for(i=0; i<result.rows.length; i++){
							let passengerBaggage2 = {
								"firstname": result.rows[i].firstname2,
								"lastname": result.rows[i].lastname2,
								"gender": result.rows[i].sex2,
								"flightid": result.rows[i].flightid2,
								"reservationid": result.rows[i].reservationid2,
								"baggage23kg": result.rows[i].numofbaggage23kg2,
								"baggage32kg": result.rows[i].numofbaggage32kg2,
								"seatnumber": result.rows[i].seatnumber2,
								"departurecity": result.rows[i].departurecity2,
								"arrivalcity": result.rows[i].arrivalcity2,
								"checkin": result.rows[i].checkin2
							}
							baggagePerPassenger.push(passengerBaggage2)
						}

						jsonResponse.push({"baggagePerPassenger": baggagePerPassenger})
					}catch (error) {
						console.error(error)
						res.status(500).json({ error: 'Internal Server Error' })
					}
				}catch (error) {
					console.error(error)
					res.status(500).json({ error: 'Internal Server Error' })
				}	
			}
			try {
				//returns the passengers info
				const result = await pool
				.query(`SELECT DISTINCT COUNT(DISTINCT p.passengerid), p.sex, p.lastname, p.firstname, TO_CHAR(p.birthdate, 'DD/MM/YYYY') AS birthdate, p.email, p.phonenumber, h.reservationid
						FROM passenger p, has h
						WHERE p.passengerid = h.passengerid  AND h.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'
						GROUP BY p.sex, p.lastname, p.firstname, h.reservationid, p.birthdate, p.email, p.phonenumber
						ORDER BY p.lastname ASC`)
				
				for (i = 0; i < result.rows.length; i++) {
					let passenger = {
						"gender": result.rows[i].sex,
						"firstname": result.rows[i].firstname,
						"lastname": result.rows[i].lastname,
						"birthdate": result.rows[i].birthdate,
						"email": result.rows[i].email,
						"phonenumber": result.rows[i].phonenumber
					}
					passengers.push(passenger)
				}
				jsonResponse.push({"numofpassengers": passengers.length})
				jsonResponse.push({"bookingid": result.rows[0].reservationid})
				jsonResponse.push({"passengers": passengers})
		
			} catch (error) {
				console.error(error)
				res.status(500).json({ error: 'Internal Server Error' })
			}
		
			try {
				//returns the petsize if exists
				const result = await pool
				.query(`SELECT r.petsize
						FROM reservation r
						WHERE r.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'`)
		
				jsonResponse.push({"petsize": result.rows[0].petsize})
			}catch (error) {
				console.error(error)
				res.status(500).json({ error: 'Internal Server Error' })
			}
		
			try {
				//returns the wifi type
				const result = await pool
				.query(`SELECT r.wifionboard
						FROM reservation r
						WHERE r.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'`)
		
				jsonResponse.push({"wifionboard": result.rows[0].wifionboard})
			}catch (error) {
				console.error(error)
				res.status(500).json({ error: 'Internal Server Error' })
			}
		} 
		else {
			console.log('No flights')
			jsonResponse.push({"numofflightstocheckin": 0})
		}
		
		
	} catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
	}

	res.json(jsonResponse)
})

//POST api that update the check in info and make the flight for all passengers in the reservation checked in
//otherwise is not checked in
app.post('/flynow/update-checkin', async (req, res) => {
    const jsonArray = req.body
	
	try {
		if(jsonArray[0].numofflights === 1) {
			await pool.query(`UPDATE has
				SET checkin = 'true'
				WHERE reservationid='${jsonArray[0].bookingid.toUpperCase()}'
				AND flightid = '${jsonArray[0].flightid1.toUpperCase()}'`
			)
			res.json([{ success: true}])
		}
		else if(jsonArray[0].numofflights === 2) {
			await pool.query(`UPDATE has
				SET checkin = 'true'
				WHERE reservationid='${jsonArray[0].bookingid.toUpperCase()}'
				AND (flightid = '${jsonArray[0].flightid1.toUpperCase()}' OR flightid = '${jsonArray[0].flightid2.toUpperCase()}')`
			)
			res.json([{ success: true}])
		}
		else{
			res.json([{ success: false}])
		}
		
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api to check if the booking exists in the car screen and the airport that select the user is the arrival airport
//of the booking for example if the flights are ATH->SKG with return SKG->ATH then the arrival airport is SKG
//or ATH->BER->HEL with return HEL->BUD->ATH then the arrival airport is HEL, after this, there is a check about
//pick up and return time of the car to be within the limits of the flight
app.post('/flynow/car-booking-exists', async (req, res) => {
    const jsonArray = req.body
	let check = 0

	try {
		const result = await pool.query(`
		SELECT reservationid 
		FROM reservation 
		WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
		
		if(result.rows.length==0) {
			res.json([{success: false, successAirport: true, successTime: true}])
		}
		else {
			const numOfFlights = await pool.query(`
			SELECT COUNT(DISTINCT flightid) 
			from has 
			WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
			
			if(numOfFlights.rows[0].count === '1') {
				const result = await pool.query(`SELECT DISTINCT f.flightid FROM flight f, has h 
				WHERE f.flightid=h.flightid 
				AND h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f.arrivalairport='${jsonArray[0].location}'`)
				
				if(result.rows.length == 0) {
					check = 1
					res.json([{success: true, successAirport: false, successTime: true}])
				}
				else {
					const result1 = await pool.query(`SELECT DISTINCT f.flightid FROM flight f 
					WHERE f.flightid='${result.rows[0].flightid}'
					AND (EXTRACT(HOUR FROM f.arrivaltime) * 60 + EXTRACT(MINUTE FROM f.arrivaltime) < 
					${jsonArray[0].pickUpHours * 60 + jsonArray[0].pickUpMinutes}
					AND f.flightdate = TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY'))
					OR f.flightdate < TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY'))`)
					
					if(result1.rows.length == 0) {
						check = 1
						res.json([{success: true, successAirport: true, successTime: false}])
					}
				}
			}
			else if(numOfFlights.rows[0].count === '2') {
				const result = await pool.query(`SELECT DISTINCT f1.flightid AS "flightid1", f2.flightid AS "flightid2" 
				FROM flight f1, flight f2, has h1, has h2 
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND
				(f2.arrivalairport=f1.departureairport AND f1.flightdate<=f2.flightdate 
				AND f1.arrivalairport='${jsonArray[0].location}')`)

				const result1 = await pool.query(`SELECT DISTINCT f2.flightid FROM flight f1, flight f2, has h1, has h2 
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport 
				AND (f2.arrivalairport<>f1.departureairport 
				AND f2.arrivalairport='${jsonArray[0].location}')`)

				
				if(result.rows.length == 0 && result1.rows.length == 0) {
					check = 1
					res.json([{success: true, successAirport: false, successTime: true}])
				}
				else if(result1.rows.length != 0) {
					const result2 = await pool.query(`SELECT DISTINCT f1.flightid FROM flight f1
					WHERE (f1.flightid='${result1.rows[0].flightid}'
					AND ((EXTRACT(HOUR FROM f1.arrivaltime) * 60 + EXTRACT(MINUTE FROM f1.arrivaltime) < 
					${jsonArray[0].pickUpHours * 60 + jsonArray[0].pickUpMinutes}
					AND f1.flightdate = TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY'))
					OR f1.flightdate < TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY')))`)

					if(result2.rows.length == 0) {
						check = 1
						res.json([{success: true, successAirport: true, successTime: false}])
					}
				}
				else if(result.rows.length != 0) {
					const result3 = await pool.query(`SELECT DISTINCT f1.flightid FROM flight f1, flight f2
					WHERE (f1.flightid='${result.rows[0].flightid1}'
					AND ((EXTRACT(HOUR FROM f1.arrivaltime) * 60 + EXTRACT(MINUTE FROM f1.arrivaltime) < 
					${jsonArray[0].pickUpHours * 60 + jsonArray[0].pickUpMinutes}
					AND f1.flightdate = TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY'))
					OR f1.flightdate < TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY')))
					AND (f2.flightid='${result.rows[0].flightid2}'
					AND ((EXTRACT(HOUR FROM f2.departuretime) * 60 + EXTRACT(MINUTE FROM f2.departuretime) >
					${jsonArray[0].returnHours * 60 + jsonArray[0].returnMinutes}
					AND f2.flightdate = TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY'))
					OR f2.flightdate > TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY')))`)

					if(result3.rows.length == 0) {
						check = 1
						res.json([{success: true, successAirport: true, successTime: false}])
					}
				}
			}
			else if(numOfFlights.rows[0].count === '3') {
				const result = await pool.query(`SELECT DISTINCT f1.flightid AS "flightid1", f2.flightid AS "flightid2" FROM flight f1, flight f2, flight f3, 
				has h1, has h2, has h3
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
				AND f3.flightid=h3.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
				AND f3.arrivalairport=f1.departureairport
				AND f2.flightdate=f3.flightdate 
				AND (f1.flightdate<=f2.flightdate AND f1.arrivalairport='${jsonArray[0].location}')`)
				
				const result1 = await pool.query(`SELECT DISTINCT f3.flightid AS "flightid1", f1.flightid AS "flightid2" FROM flight f1, flight f2, flight f3, 
				has h1, has h2, has h3
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
				AND f3.flightid=h3.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
				AND f3.arrivalairport=f1.departureairport
				AND f2.flightdate=f3.flightdate 
				AND (f1.flightdate>=f2.flightdate AND f1.departureairport='${jsonArray[0].location}')`)
			
				if(result.rows.length == 0 && result1.rows.length == 0) {
					check = 1
					res.json([{success: true, successAirport: false, successTime: true}])
				}
				else if(result1.rows.length != 0) {
					const result2 = await pool.query(`SELECT DISTINCT f1.flightid FROM flight f1, flight f2
					WHERE (f1.flightid='${result1.rows[0].flightid1}'
					AND ((EXTRACT(HOUR FROM f1.arrivaltime) * 60 + EXTRACT(MINUTE FROM f1.arrivaltime) < 
					${jsonArray[0].pickUpHours * 60 + jsonArray[0].pickUpMinutes}
					AND f1.flightdate = TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY'))
					OR f1.flightdate < TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY')))
					AND (f2.flightid='${result1.rows[0].flightid2}'
					AND ((EXTRACT(HOUR FROM f2.departuretime) * 60 + EXTRACT(MINUTE FROM f2.departuretime) >
					${jsonArray[0].returnHours * 60 + jsonArray[0].returnMinutes}
					AND f2.flightdate = TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY'))
					OR f2.flightdate > TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY')))`)

					if(result2.rows.length == 0) {
						check = 1
						res.json([{success: true, successAirport: true, successTime: false}])
					}
				}
				else if(result.rows.length != 0) {
					const result3 = await pool.query(`SELECT DISTINCT f1.flightid FROM flight f1, flight f2
					WHERE (f1.flightid='${result.rows[0].flightid1}'
					AND ((EXTRACT(HOUR FROM f1.arrivaltime) * 60 + EXTRACT(MINUTE FROM f1.arrivaltime) < 
					${jsonArray[0].pickUpHours * 60 + jsonArray[0].pickUpMinutes}
					AND f1.flightdate = TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY'))
					OR f1.flightdate < TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY')))
					AND (f2.flightid='${result.rows[0].flightid2}'
					AND ((EXTRACT(HOUR FROM f2.departuretime) * 60 + EXTRACT(MINUTE FROM f2.departuretime) >
					${jsonArray[0].returnHours * 60 + jsonArray[0].returnMinutes}
					AND f2.flightdate = TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY'))
					OR f2.flightdate > TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY')))`)

					if(result3.rows.length == 0) {
						check = 1
						res.json([{success: true, successAirport: true, successTime: false}])
					}
				}
			}
			else if(numOfFlights.rows[0].count === '4') {
				const result = await pool.query(`SELECT DISTINCT f2.flightid AS "flightid1", f3.flightid AS "flightid2" FROM flight f1, flight f2, flight f3,
				flight f4, has h1, has h2, has h3, has h4
				WHERE f1.flightid=h1.flightid AND f2.flightid=h2.flightid 
				AND f3.flightid=h3.flightid AND f4.flightid=h4.flightid
				AND h1.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h2.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h3.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND h4.reservationid='${jsonArray[0].bookingId.toUpperCase()}'
				AND f1.arrivalairport=f2.departureairport AND f2.arrivalairport=f3.departureairport
				AND f3.arrivalairport=f4.departureairport AND f4.arrivalairport=f1.departureairport
				AND f1.flightdate=f2.flightdate AND f3.flightdate=f4.flightdate
				AND f1.flightdate<=f3.flightdate AND f2.arrivalairport='${jsonArray[0].location}'`)
				
				if(result.rows.length == 0) {
					check = 1
					res.json([{success: true, successAirport: false, successTime: true}])
				}
				else {
					const result1 = await pool.query(`SELECT DISTINCT f1.flightid FROM flight f1, flight f2
					WHERE (f1.flightid='${result.rows[0].flightid1}'
					AND ((EXTRACT(HOUR FROM f1.arrivaltime) * 60 + EXTRACT(MINUTE FROM f1.arrivaltime) < 
					${jsonArray[0].pickUpHours * 60 + jsonArray[0].pickUpMinutes}
					AND f1.flightdate = TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY'))
					OR f1.flightdate < TO_DATE('${jsonArray[0].pickUpDate}', 'DD/MM/YYYY')))
					AND (f2.flightid='${result.rows[0].flightid2}'
					AND ((EXTRACT(HOUR FROM f2.departuretime) * 60 + EXTRACT(MINUTE FROM f2.departuretime) >
					${jsonArray[0].returnHours * 60 + jsonArray[0].returnMinutes}
					AND f2.flightdate = TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY'))
					OR f2.flightdate > TO_DATE('${jsonArray[0].returnDate}', 'DD/MM/YYYY')))`)

					if(result1.rows.length == 0) {
						check = 1
						res.json([{success: true, successAirport: true, successTime: false}])
					}
				}
			}
			if(check === 0) {
				res.json([{success: true, successAirport: true, successTime: true}])
			}
		}
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that returns the cars of the databse in some location(airport) and checks if the cars are not covered
//from some reservation in the pick up and return datetimes that select the user
app.post('/flynow/cars', async (req, res) => {
	const jsonArray = req.body
	
    try {
		const result = await pool
		.query(`
		SELECT c.carimage, c.company, c.model, c.price, c.carid
		FROM car c
		LEFT JOIN contains con ON c.carid=con.carid
		AND con.pickup <= TIMESTAMP '${jsonArray[0].return}'
		AND con.return >= TIMESTAMP '${jsonArray[0].pickUp}'
		WHERE c.location='${jsonArray[0].location}'
		AND con.carid IS NULL
		ORDER BY c.price ASC`)
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
})

//POST api that inserts the renting of car in a reservation that finally select the user
app.post('/flynow/renting-car', async (req, res) => {
	const jsonArray = req.body
	
    try {
		await pool.query(`INSERT INTO contains(carid, reservationid, pickup, return, rentingprice)
		VALUES(${jsonArray[0].carId},'${jsonArray[0].bookingId.toUpperCase()}',
		TIMESTAMP '${jsonArray[0].pickUp}',TIMESTAMP '${jsonArray[0].return}', ${jsonArray[0].price});`)

        res.json([{ success: true}])
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that returns the airplane capacity according to the airplane model
app.post('/flynow/airplane-capacity', async (req, res) => {
    const jsonArray = req.body

	try {
		const result = await pool.query(`SELECT capacity FROM model WHERE modelid='${jsonArray[0].airplaneModel}'`)
		
		res.json(result.rows)
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//POST api that returns the not available seats in a flight
app.post('/flynow/seats', async (req, res) => {
    const jsonArray = req.body

	try {
		const result = await pool
		.query(`SELECT DISTINCT seatnumber FROM has WHERE flightid='${jsonArray[0].flightId}'`)
		
		res.json(result.rows)
    } catch (error) {
		console.error(error)
		res.status(500).json({ error: 'Internal Server Error' })
    }
})

//function that makes query to find the flights for one way in a destination
//and see if the restriction am flights or pm flights is true or the restriction direct
//otherwise returns both direct and one stop flights
//also checks if the number of passengers that select the user for the reservation is less or equal than
//the capacity of the airplane - the seats that is not available
async function queryOneWayFlights(jsonArray) {
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
async function queryReturnFlights(jsonArray) {
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

//POST api that returns the flights with the previous functions queryOneWayFlights() and queryReturnFlights()
//and takes from the departure and arrival time the hours and minutes like '19:20'
app.post('/flynow/flights', async (req, res) => {
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
})

app.listen(PORT,() => {
    console.log(`Server is open at ${PORT}`)
})