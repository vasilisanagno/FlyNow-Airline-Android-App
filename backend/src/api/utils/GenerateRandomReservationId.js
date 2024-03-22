//function that generates a random reservation id for the completion of the booking
export function generateReservationId() {
	const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
	let reservationId = '';
  
	for (let i = 0; i < 6; i++) {
	  const randomIndex = Math.floor(Math.random() * characters.length);
	  reservationId += characters.charAt(randomIndex);
	}
  
	return reservationId;
}