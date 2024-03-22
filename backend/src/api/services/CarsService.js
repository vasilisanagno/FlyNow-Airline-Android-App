import { pool } from '../../config/DatabaseConfig.js'

//function to check if the booking exists in the car screen and the airport that select the user is the arrival airport
//of the booking for example if the flights are ATH->SKG with return SKG->ATH then the arrival airport is SKG
//or ATH->BER->HEL with return HEL->BUD->ATH then the arrival airport is HEL, after this, there is a check about
//pick up and return time of the car to be within the limits of the flight
export async function queriesForValidationOfCarData(jsonArray) {
    let check = 0
    const result = await pool.query(`
    SELECT reservationid 
    FROM reservation 
    WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
    
    if(result.rows.length==0) {
        return {success: false, successAirport: true, successTime: true}
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
                return {success: true, successAirport: false, successTime: true}
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
                    return {success: true, successAirport: true, successTime: false}
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
                return {success: true, successAirport: false, successTime: true}
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
                    return {success: true, successAirport: true, successTime: false}
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
                    return {success: true, successAirport: true, successTime: false}
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
                return {success: true, successAirport: false, successTime: true}
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
                    return {success: true, successAirport: true, successTime: false}
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
                    return {success: true, successAirport: true, successTime: false}
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
                return {success: true, successAirport: false, successTime: true}
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
                    return {success: true, successAirport: true, successTime: false}
                }
            }
        }
        if(check === 0) {
            return {success: true, successAirport: true, successTime: true}
        }
    }
}

//function that returns the cars of the databse in some location(airport) and checks if the cars are not covered
//from some reservation in the pick up and return datetimes that select the user
export async function queryForSearchingCars(jsonArray) {
    const result = await pool
		.query(`
		SELECT c.carimage, c.company, c.model, c.price, c.carid
		FROM car c
		LEFT JOIN contains con ON c.carid=con.carid
		AND con.pickup <= TIMESTAMP '${jsonArray[0].return}'
		AND con.return >= TIMESTAMP '${jsonArray[0].pickUp}'
		WHERE c.location='${jsonArray[0].location}'
		AND con.carid IS NULL
		ORDER BY c.price ASC`
    )
    return result
}

//function that inserts the renting of car in a reservation that finally select the user
export async function insertNewRentalOfCar(jsonArray) {
    await pool.query(`INSERT INTO contains(carid, reservationid, pickup, return, rentingprice)
    VALUES(${jsonArray[0].carId},'${jsonArray[0].bookingId.toUpperCase()}',
    TIMESTAMP '${jsonArray[0].pickUp}',TIMESTAMP '${jsonArray[0].return}', ${jsonArray[0].price});`)
}