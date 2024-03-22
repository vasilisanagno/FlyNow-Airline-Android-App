import pg from 'pg'

const { Pool } = pg

//conection to the database and settings about the server
const pool = new Pool({
    user: 'postgres',
    host: 'localhost',
    database: 'FlyNow',
    password: process.env.DATABASE_PASSWORD,
    port: process.env.DATABASE_PORT
})

export { pool }