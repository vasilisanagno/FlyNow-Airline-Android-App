import { pool } from '../../config/DatabaseConfig.js'

//function that returns the value from the wifionboard field of the database
export async function queryForSearchingWifi(bookingid) {
    const result = 
    await pool.query(`
        SELECT r.wifionboard 
		FROM reservation r
		WHERE r.reservationid='${bookingid}'`
    )

    return result
}

//function that updates the wifionboard field of the database that select the user from the wifi on board screen
//+ the new price of the reservation that is priceOld + priceNew of the new selection of the wifi on board
export async function queryForUpdatingWifi(wifiOnBoard,price,bookingid) {
    const result = 
    await pool.query(`
        UPDATE reservation
		SET wifionboard=${wifiOnBoard}, price= price + ${price}
		WHERE reservationid='${bookingid}'`
    )

    return result
}