import { pool } from '../../config/DatabaseConfig.js'

//function that returns the pieces of baggage that have selected the user during the completion of the database
export async function queriesForSearchingBaggage(bookingId) {
    let passengers = []
	let result

    //returns the number of flights in the booking
    //one way-direct(1 flight), one way-one stop(2 flights), round trip-direct 2 ways(2 flights),
    //round trip-direct the outbound and one stop the inbound(3 flights), 
    //round trip-one stop the outbound and direct the inbound(3 flights),
    //round trip-one stop the outbound and one stop the inbound(4 flights)
    const numOfFlights = await pool.query(`SELECT COUNT(DISTINCT flightid) from has WHERE reservationid='${bookingId}'`)
    if(numOfFlights.rows[0].count === '1') {
        result = await pool
            .query(`SELECT DISTINCT p.firstname, p.lastname, p.sex, p.email, p.birthdate, p.phonenumber,
            h1.numofbaggagepercategory AS numofbaggage23kg, h2.numofbaggagepercategory AS numofbaggage32kg
            FROM flight f, has h1, has h2, passenger p
            WHERE h1.reservationid = '${bookingId}'
            AND h2.reservationid = '${bookingId}'
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
                AND h1.reservationid='${bookingId}'
                AND h2.reservationid='${bookingId}'
                AND h3.reservationid='${bookingId}'
                AND h4.reservationid='${bookingId}'
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
            AND h1.reservationid = '${bookingId}'
            AND h2.reservationid = '${bookingId}'
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
        AND h1.reservationid='${bookingId}'
        AND h2.reservationid='${bookingId}'
        AND h3.reservationid='${bookingId}'
        AND h4.reservationid='${bookingId}'
        AND h5.reservationid='${bookingId}'
        AND h6.reservationid='${bookingId}'
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
            AND h1.reservationid='${bookingId}'
            AND h2.reservationid='${bookingId}'
            AND h3.reservationid='${bookingId}'
            AND h4.reservationid='${bookingId}'
            AND h5.reservationid='${bookingId}'
            AND h6.reservationid='${bookingId}'
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
        AND h1.reservationid='${bookingId}'
        AND h2.reservationid='${bookingId}'
        AND h3.reservationid='${bookingId}'
        AND h4.reservationid='${bookingId}'
        AND h5.reservationid='${bookingId}'
        AND h6.reservationid='${bookingId}'
        AND h7.reservationid='${bookingId}'
        AND h8.reservationid='${bookingId}'
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

    return passengers
}

//function that updates the number of pieces of baggage per passenger that select in the baggage from more screen
//+ the new price of the reservation that is priceOld + priceNew of the new pieces of baggage
export async function queriesForUpdatingBaggage(jsonArray) {
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
    .query(`
        UPDATE reservation
        SET price= price + ${jsonArray[0].price}
        WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`
    )
}