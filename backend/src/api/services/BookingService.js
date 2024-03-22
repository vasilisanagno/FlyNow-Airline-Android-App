import { pool } from '../../config/DatabaseConfig.js'

//function that checks booking if exists and returns the result of the query
export async function queryForCheckingBooking(jsonArray) {
    const result = 
        await pool.query(`
        SELECT p.lastname, h.reservationid 
        FROM passenger p, has h 
        WHERE LOWER(p.lastname) ='${jsonArray[0].lastname.toLowerCase()}' 
        AND h.reservationid = '${jsonArray[0].bookingid.toUpperCase()}' 
        AND p.passengerid = h.passengerid`
    )
    return result
}

//function that returns all the booking details for flights, passengers, baggage, 
//seats, wifi, cars and the total price of the reservation
export async function queriesForRetrievingBookingDetails(jsonArray) {
	let i
	let flights = []
	let passengers = []
	let baggagePerPassenger = []
	let jsonResponse = []

    //returns the num of flights in a reservation
    const numOfFlights = await pool
    .query(`SELECT COUNT(DISTINCT h.flightid)
            FROM has h
            WHERE  h.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'`)

    if(numOfFlights.rows[0].count === '1'){
        jsonResponse = await queriesForOneFlight(jsonResponse, jsonArray, flights, baggagePerPassenger)
    }
    else if(numOfFlights.rows[0].count === '2'){
        jsonResponse = await queriesForTwoFlights(jsonResponse, jsonArray, flights, baggagePerPassenger)
    }
    else if(numOfFlights.rows[0].count === '3'){
        jsonResponse = await queriesForThreeFlights(jsonResponse, jsonArray, flights, baggagePerPassenger)
    }
    else if(numOfFlights.rows[0].count === '4'){
        jsonResponse = await queriesForFourFlights(jsonResponse,jsonArray, flights, baggagePerPassenger)
    }

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

    //returns the petsize if exists
    const result1 = await pool
    .query(`SELECT r.petsize
            FROM reservation r
            WHERE r.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'`)

    jsonResponse.push({"petsize": result1.rows[0].petsize})

    //returns the wifi type
    const result2 = await pool
    .query(`SELECT r.wifionboard
            FROM reservation r
            WHERE r.reservationid = '${jsonArray[0].bookingid.toUpperCase()}'`)

    jsonResponse.push({"wifionboard": result2.rows[0].wifionboard})

    //returns the car booking
    const result3 = await pool.query(`
        SELECT c.carimage, c.company, c.model, con.rentingprice, 
        c.location, TO_CHAR(con.pickup, 'DD/MM/YYYY HH24:MI') AS pickup, TO_CHAR(con.return, 'DD/MM/YYYY HH24:MI') AS return
        FROM contains con, car c
        WHERE con.carid=c.carid 
        AND con.reservationid='${jsonArray[0].bookingid.toUpperCase()}'`
    )

    let json = []
    for(let i=0; i<result3.rows.length; i++) {
        const imageData = result3.rows[i].carimage
        // Convert the bytea data to base64
        const base64ImageData = imageData.toString('base64')
        json.push({
            carimage: base64ImageData,
            company: result3.rows[i].company,
            model: result3.rows[i].model,
            price: result3.rows[i].rentingprice,
            location: result3.rows[i].location,
            pickup: result3.rows[i].pickup,
            return: result3.rows[i].return
        })
    }
    // Send the base64-encoded image data in the response with company and model of cars
    jsonResponse.push({"cars": json})

    const result4 = await pool.query(`
        SELECT price
        FROM reservation 
        WHERE reservationid='${jsonArray[0].bookingid.toUpperCase()}'`
    )
    jsonResponse.push({"bookingPrice": result4.rows[0].price})
    
    return jsonResponse
}

//function to delete the booking of the user from the database
export async function queriesForDeletingBooking(bookingId) {
    await pool.query(`
        DELETE FROM has
        WHERE reservationid='${bookingId}'
    `)
    await pool.query(`
        DELETE FROM contains
        WHERE reservationid='${bookingId}'
    `)
    await pool.query(`
        DELETE FROM reservation
        WHERE reservationid='${bookingId}'
    `)
}

//function to find the booking if there is one flight
async function queriesForOneFlight(jsonResponse, jsonArray, flights, baggagePerPassenger) {
    let i

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
    return jsonResponse
}

//function to find the booking if there are two flights
async function queriesForTwoFlights(jsonResponse, jsonArray, flights, baggagePerPassenger) {
    let i

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
    }
    if(flights.length === 0){
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
        }	
    }
    return jsonResponse
}

//function to find the booking if there are three flights
async function queriesForThreeFlights(jsonResponse, jsonArray, flights, baggagePerPassenger) {
    let i

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
    }
    if(flights.length === 0){
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
        }
    }
    return jsonResponse
}

//function to find the booking if there are four flights
async function queriesForFourFlights(jsonResponse, jsonArray,flights, baggagePerPassenger) {
    let i

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
    }
    return jsonResponse
}