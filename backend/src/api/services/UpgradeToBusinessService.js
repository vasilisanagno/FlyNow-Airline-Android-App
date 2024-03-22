import { pool } from '../../config/DatabaseConfig.js'

//function that returns the class of the outbound and inbound(if there is) flights
//from the classtype field of the database for the upgrade to business screen
export async function queriesForSearchingClass(jsonArray) {
    //the return value contains also if the flight is one way or round trip(with return)
    const numOfFlights = await pool.query(`
    SELECT COUNT(DISTINCT flightid) 
    from has 
    WHERE reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
    
    if(numOfFlights.rows[0].count === '1') {
        const result = await pool.query(`SELECT DISTINCT h.classtype FROM flight f, has h 
        WHERE f.flightid=h.flightid 
        AND h.reservationid='${jsonArray[0].bookingId.toUpperCase()}'`)
        
        return {
            oneWay: true,
            outbound: {
                classType: result.rows[0].classtype
            }
        }
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

            return {
                oneWay: true,
                outbound: {
                    classType: result.rows[0].classtype
                }
            }
        }
        else {
            return {
                oneWay: false,
                outbound: {
                    classType: result.rows[0].classtype1
                },
                inbound: {
                    classType: result.rows[0].classtype2
                }
            }
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

            return {
                oneWay: false,
                outbound: {
                    classType: result.rows[0].classtype2
                },
                inbound: {
                    classType: result.rows[0].classtype1
                }
            }
        }
        else {
            return {
                oneWay: false,
                outbound: {
                    classType: result.rows[0].classtype1
                },
                inbound: {
                    classType: result.rows[0].classtype2
                }
            }
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

        return {
            oneWay: false,
            outbound: {
                classType: result.rows[0].classtype1
            },
            inbound: {
                classType: result.rows[0].classtype2
            }
        }
    }
}

//function that updates the new selected classes of the flights outbound and inbound(if there is) from the
//upgrade to business screen
//+ the new price of the reservation that is priceOld + priceNew of the new updated classes in the flights
export async function queriesForUpdatingClass(jsonArray) {
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
}