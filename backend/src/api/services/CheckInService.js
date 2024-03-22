import { pool } from '../../config/DatabaseConfig.js'

//function that returns all the checkin details for flights, passengers, baggage, 
//seats and wifi of the reservation and the check is open 24 hours to 30 minutes before flight
export async function queriesForSearchingCheckInDetails(jsonArray) {
    let flights = []
	let passengers = []
	let baggagePerPassenger = []
	let jsonResponse = []
	let numOfFlightsToCheckin = []
	let i

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
                AND ((f.flightdate = CURRENT_DATE + INTERVAL '1 day'
                AND f.departuretime <= (CAST((SELECT formatted_current_time FROM CurrentTimeFormatted) AS TIME WITH TIME ZONE) + INTERVAL '24 hours' - INTERVAL '1 second')
                AND h.checkin = 'false')
                OR (f.flightdate = CURRENT_DATE 
                    AND f.departuretime >= (CAST((SELECT formatted_current_time FROM CurrentTimeFormatted) AS TIME WITH TIME ZONE)+INTERVAL '30 minutes')
                    AND h.checkin = 'false'))
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
    }
    else{
        numOfFlightsToCheckin = flight1.rows
    }
    
    if (numOfFlightsToCheckin.length > 0) {
        jsonResponse.push({"numofflightstocheckin": numOfFlightsToCheckin.length})
        if(numOfFlightsToCheckin.length === 1){
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
        }
        else if(numOfFlightsToCheckin.length === 2){
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
        }

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
    } 
    else {
        console.log('No flights')
        jsonResponse.push({"numofflightstocheckin": 0})
    }
		
    return jsonResponse
}

//function that updates the check in info and makes the flight for all passengers in the reservation checked in
//otherwise is not checked in
export async function queryForUpdatingCheckIn(jsonArray) {
    if(jsonArray[0].numofflights === 1) {
        await pool.query(`UPDATE has
            SET checkin = 'true'
            WHERE reservationid='${jsonArray[0].bookingid.toUpperCase()}'
            AND flightid = '${jsonArray[0].flightid1.toUpperCase()}'`
        )
        return true
    }
    else if(jsonArray[0].numofflights === 2) {
        await pool.query(`UPDATE has
            SET checkin = 'true'
            WHERE reservationid='${jsonArray[0].bookingid.toUpperCase()}'
            AND (flightid = '${jsonArray[0].flightid1.toUpperCase()}' OR flightid = '${jsonArray[0].flightid2.toUpperCase()}')`
        )
        return true
    }
    else{
        return false
    }
}