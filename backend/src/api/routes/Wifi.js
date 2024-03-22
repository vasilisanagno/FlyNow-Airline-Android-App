import express from 'express'
import { searchWifi, updateWifi } from '../controllers/WifiController.js'

const router = express.Router()

//route for searching wifi-package in a specific reservation
router.post('/wifi-on-board', searchWifi)

//route for updating wifi-package in a specific reservation
router.post('/update-wifi', updateWifi)

export { router as WifiRouter }