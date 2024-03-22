import { pool } from '../../config/DatabaseConfig.js'

//function that returns the value from the petsize field of the database
export async function queryForSearchingPet(bookingid) {
    const result = await pool
		.query(`
        SELECT r.petsize
        FROM reservation r
        WHERE r.reservationid='${bookingid}'`
    )
    return result
}

//function that updates the petsize field of the database in a specific reservation
//+ the new price of the reservation that is priceOld + priceNew of the new pet size
export async function queryForUpdatingPet(petSize, price, bookingid) {
    const result = await pool
		.query(`
        UPDATE reservation
        SET petsize='${petSize}', price= price + ${price}
        WHERE reservationid='${bookingid}'`
    )
    return result
}