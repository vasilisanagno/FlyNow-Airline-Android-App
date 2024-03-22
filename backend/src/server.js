import express from 'express'
import cors from 'cors'
import 'dotenv/config'
import { BookingRouter } from './api/routes/Booking.js'
import { BaggageRouter } from './api/routes/Baggage.js'
import { CarsRouter } from './api/routes/Cars.js'
import { CheckInRouter } from './api/routes/CheckIn.js'
import { FlightsAirportsRouter } from './api/routes/FlightsAirports.js'
import { MakingReservationRouter } from './api/routes/MakingReservation.js'
import { PetsRouter } from './api/routes/Pets.js'
import { UpgradeToBusinessRouter } from './api/routes/UpgradeToBusiness.js'
import { WifiRouter } from './api/routes/Wifi.js'

const app = express()
const PORT = process.env.PORT || 5000

app.use(cors())
app.use(express.urlencoded({extended:false}))
app.use(express.json())

app.use('/flynow', BookingRouter)
app.use('/flynow', BaggageRouter)
app.use('/flynow', CarsRouter)
app.use('/flynow', CheckInRouter)
app.use('/flynow', FlightsAirportsRouter)
app.use('/flynow', MakingReservationRouter)
app.use('/flynow', PetsRouter)
app.use('/flynow', UpgradeToBusinessRouter)
app.use('/flynow', WifiRouter)

app.listen(PORT,() => {
    console.log(`Server is open at ${PORT}`)
})