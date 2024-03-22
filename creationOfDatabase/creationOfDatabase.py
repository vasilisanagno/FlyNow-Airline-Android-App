import psycopg2
from faker import Faker
import random as r
from datetime import datetime

fake = Faker()

#connection with database FlyNow, 
#you must create a database with name FlyNow
#and password is different, you must type your own
#password of the user postgres
conn = psycopg2.connect(
database="FlyNow",
user="postgres",
password="postgres0123456789",
host="localhost",
port="5432"
)

cur = conn.cursor()

#creation of table "passenger"
cur.execute("""DROP TABLE IF EXISTS passenger CASCADE""")
cur.execute("""
    CREATE TABLE passenger(
        passengerid SERIAL4 NOT NULL PRIMARY KEY,
        firstname VARCHAR (50) NOT NULL,
        lastname VARCHAR (50) NOT NULL,
        email VARCHAR (100) UNIQUE NOT NULL,
        birthdate DATE NOT NULL,
        sex VARCHAR(10) NOT NULL,
        phonenumber VARCHAR(20) UNIQUE NOT NULL
    );
""")

#creation of table "flight"
cur.execute("""DROP TABLE IF EXISTS flight CASCADE""")
cur.execute("""
    CREATE TABLE flight(
        flightid VARCHAR(20) NOT NULL PRIMARY KEY,
        flightdate DATE NOT NULL,
        departuretime TIME WITH TIME ZONE NOT NULL,
        arrivaltime TIME WITH TIME ZONE NOT NULL,
        economyprice NUMERIC(6,2) NOT NULL,
        flexprice NUMERIC(6,2) NOT NULL,
        businessprice NUMERIC(6,2) NOT NULL,
        departureairport VARCHAR(50) NOT NULL,
        arrivalairport VARCHAR(50) NOT NULL,
        airplane VARCHAR(20) NOT NULL
    );
""")

#creation of table "reservation"
cur.execute("""DROP TABLE IF EXISTS reservation CASCADE""")
cur.execute("""
    CREATE TABLE reservation(
        reservationid VARCHAR(20) NOT NULL PRIMARY KEY,
        petsize VARCHAR(20),
        wifionboard INTEGER NOT NULL,
        price NUMERIC(6,2) NOT NULL
    );
""")

#creation of table "contains"
cur.execute("""DROP TABLE IF EXISTS contains CASCADE""")
cur.execute("""
    CREATE TABLE contains(
        carid INTEGER NOT NULL,
        reservationid VARCHAR(20) NOT NULL,
        pickup TIMESTAMP NOT NULL,
        return TIMESTAMP NOT NULL,
        rentingprice NUMERIC(6,2) NOT NULL,
        PRIMARY KEY(carid,reservationid)
    );
""")

#creation of table "has"
cur.execute("""DROP TABLE IF EXISTS has CASCADE""")
cur.execute("""
    CREATE TABLE has(
        passengerid INTEGER NOT NULL,
        flightid VARCHAR(20) NOT NULL,
        reservationid VARCHAR(20) NOT NULL,
        seatnumber VARCHAR(10) NOT NULL,
        classtype VARCHAR(20) NOT NULL,
        baggage VARCHAR(20) NOT NULL,
        numofbaggagepercategory INTEGER NOT NULL,
        checkin BOOL NOT NULL, 
        PRIMARY KEY(passengerid,flightid,reservationid,baggage)
    );
""")

#creation of table "car"
cur.execute("""DROP TABLE IF EXISTS car CASCADE""")
cur.execute("""
    CREATE TABLE car(
        carid SERIAL4 NOT NULL PRIMARY KEY,
        model VARCHAR(20) NOT NULL,
        location VARCHAR(50) NOT NULL,
        company VARCHAR(20) NOT NULL,
        price NUMERIC(6,2) NOT NULL,
        carimage BYTEA NOT NULL
    );
""")

#creation of table "airport"
cur.execute("""DROP TABLE IF EXISTS airport CASCADE""")
cur.execute("""
    CREATE TABLE airport(
        name VARCHAR(50) NOT NULL,
        city VARCHAR(50) NOT NULL,
        PRIMARY KEY(name)
    );
""")

#creation of table "airplane"
cur.execute("""DROP TABLE IF EXISTS airplane CASCADE""")
cur.execute("""
    CREATE TABLE airplane(
        airplaneid VARCHAR(20) NOT NULL,
        model VARCHAR(20) NOT NULL,
        PRIMARY KEY(airplaneid)
    );
""")

#creation of table "model"
cur.execute("""DROP TABLE IF EXISTS model CASCADE""")
cur.execute("""
    CREATE TABLE model(
        modelid VARCHAR(20) NOT NULL,
        capacity INTEGER NOT NULL,
        PRIMARY KEY(modelid)
    );
""")

#creation of table "flight_duration"
cur.execute("""DROP TABLE IF EXISTS flight_duration CASCADE""")
cur.execute("""
    CREATE TABLE flight_duration(
        departuretime TIME WITH TIME ZONE NOT NULL,
        arrivaltime TIME WITH TIME ZONE NOT NULL,
        duration VARCHAR(20) NOT NULL,
        PRIMARY KEY(departuretime,arrivaltime)
    );
""")

#creation of foreign keys
#arrivalairport(flight) -> name(airport)
cur.execute("""
    ALTER TABLE flight 
    ADD FOREIGN KEY(arrivalairport)
    REFERENCES airport(name) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

#departureairport(flight) -> name(airport)
cur.execute("""
    ALTER TABLE flight 
    ADD FOREIGN KEY(departureairport)
    REFERENCES airport(name) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

#arrivaltime,departuretime(flight) -> arrivaltime,departuretime(flight_duration)
cur.execute("""
    ALTER TABLE flight 
    ADD FOREIGN KEY(arrivaltime,departuretime)
    REFERENCES flight_duration(arrivaltime,departuretime) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

#airplane(flight) -> airplaneid(airplane)
cur.execute("""
    ALTER TABLE flight 
    ADD FOREIGN KEY(airplane)
    REFERENCES airplane(airplaneid) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

#passengerid(has) -> passengerid(passenger)
cur.execute("""
    ALTER TABLE has 
    ADD FOREIGN KEY(passengerid)
    REFERENCES passenger(passengerid) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

#reservationid(has) -> reservationid(reservation)
cur.execute("""
    ALTER TABLE has 
    ADD FOREIGN KEY(reservationid)
    REFERENCES reservation(reservationid) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

#flightid(has) -> flightid(flight)
cur.execute("""
    ALTER TABLE has 
    ADD FOREIGN KEY(flightid)
    REFERENCES flight(flightid) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

#model(airplane) -> modelid(model)
cur.execute("""
    ALTER TABLE airplane 
    ADD FOREIGN KEY(model)
    REFERENCES model(modelid) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

#cardid(contains) -> carid(car)
cur.execute("""
    ALTER TABLE contains 
    ADD FOREIGN KEY(carid)
    REFERENCES car(carid) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

#reservationid(contains) -> reservationid(reservation)
cur.execute("""
    ALTER TABLE contains 
    ADD FOREIGN KEY(reservationid)
    REFERENCES reservation(reservationid) 
    ON DELETE CASCADE ON UPDATE CASCADE;
""")

female_names = [
    'Maria', 'Dimitra', 'Sofia', 'Georgia', 'Ioanna', 'Ellisabet', 'Helen', 'Rachel', 
    'Amanda', 'Anna', 'Alexia', 'Angelina', 'Kate', 'Penelope', 'Sandra', 'Scarlett'
]
male_names = [
    'Vasilis', 'George', 'John', 'Michael', 'Victor', 'Panagiotis', 'Aggelos', 'Stavros',
    'Russel', 'Brad', 'Tom', 'Dimitris', 'Konstantinos', 'Nick', 'Iasonas'  
]
female_lastnames = [
    'Amalidou', 'Nomikou', 'Papadopoulou', 'Papadimitriou', 'Karavasili', 'Kavoura',
    'Maniati', 'Weber', 'Beckett', 'Jones', 'Perry', 'Aniston' 
]
male_lastnames = [
    'House', 'Pitt', 'Johanson', 'Wilson', 'Taylor', 'Antoniou', 'Papadopoulos', 'Ioannou', 
    'Stergiou', 'Georgiou', 'Venetis', 'Grigoriou', 'Nikolopoulos', 'Papazoglou', 'Diamantidis', 
    'Spanoulis'
]

greek_phone = "+3069"
num = 1
#insert records into passenger table
for i in range(1,21):
    #random combinations from above lists and makes 10 female persons and 10 male persons
    if i%2 == 1: 
        firstname = female_names[r.randint(0,15)]
        lastname = female_lastnames[r.randint(0,11)]
        sex = 'Female'
    else :
        firstname = male_names[r.randint(0,14)]
        lastname = male_lastnames[r.randint(0,15)]
        sex = 'Male'

    #random birthdate from 18 to 65 ages in format for example 01/02/1987
    birthdate = fake.date_of_birth(minimum_age=18, maximum_age=65).strftime('%d/%m/%Y')
    
    # Create a simple email based on the name
    if int(birthdate.split('/')[2])>=2000 :
        #puts the whole birthdate year
        email = f"{firstname.lower()}.{lastname.lower()}{birthdate.split('/')[2]}{num}@gmail.com"
    else :
        #puts only the two last digits from birthdate year, like from 1985, the digits 85
        email = f"{firstname.lower()}.{lastname.lower()}{birthdate.split('/')[2][2:]}{num}@gmail.com"
    
    num = num + 1
    
    #phones that has the type of greek to be more simple
    end_phone = r.randint(12345678,98765432)
    phone = greek_phone + str(end_phone)
    
    #insert each record into the passenger table with random values in the fields
    cur.execute("""INSERT INTO passenger(firstname,lastname,email,birthdate,sex,phonenumber) 
        VALUES(%s,%s,%s,%s,%s,%s)""",
        [firstname,lastname,email,datetime.strptime(birthdate,'%d/%m/%Y'),sex,phone]
    )

#insert records into model table
cur.execute("""INSERT INTO model 
    VALUES
    ('Airbus A320neo',180),
    ('Airbus A321-200',180),
    ('Airbus A320-200',180),
    ('Boeing 737-800',180),
    ('Airbus A220-100',120);
""")

#insert records into airplane table
cur.execute("""INSERT INTO airplane 
    VALUES
    ('FN1','Airbus A320neo'),
    ('FN2','Airbus A321-200'),
    ('FN3','Airbus A320-200'),
    ('FN4','Boeing 737-800'),
    ('FN5','Airbus A320-200'),
    ('FN6','Airbus A320neo'),
    ('FN7','Boeing 737-800'),
    ('FN8','Airbus A320neo'),
    ('FN9','Airbus A321-200'),
    ('FN10','Airbus A320neo'),
    ('FN11','Airbus A220-100'),
    ('FN12','Airbus A220-100');
""")

#insert records into airport table
cur.execute("""INSERT INTO airport 
    VALUES
    ('SKG','Thessaloniki'),
    ('MAD','Madrid'),
    ('BER','Berlin'),
    ('AMS','Amsterdam'),
    ('CDG','Paris'),
    ('FCO','Rome'),
    ('ATH','Athens'),
    ('MJT','Mytilene'),
    ('VIE','Vienna'),
    ('BUD','Budapest'),
    ('HEL','Helsinki'),
    ('ZRH','Zurich'),
    ('LON','London'),
    ('PRG','Prague'),
    ('LIS','Lisbon');
""")

images = []
with open('./images/ToyotaAygo.png', 'rb') as image_file:
        # Read the content of the image as bytes
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/Fiat500Bev.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/OpelCorsa.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/ToyotaCorolla.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/SeatIbiza.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/HyundaiI10.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/MercedesE220.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/BMWSeries1.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/NissanQashqai.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/ToyotaYaris.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
with open('./images/TeslaModelY.png', 'rb') as image_file:
        image_data = image_file.read()
        images.append(psycopg2.Binary(image_data))
        
#insert records into car table
cur.execute("""INSERT INTO car(model, location, company, price, carimage) 
    VALUES
    ('Toyota Aygo','ATH','Avis',55.00,%s),
    ('Fiat 500 Bev','SKG','Avis',75.00,%s),
    ('Opel Corsa','CDG','Thrifty',80.00,%s),
    ('Toyota Corolla','FCO','Firefly',100.00,%s),
    ('Seat Ibiza','LIS','Firefly',75.00,%s),
    ('Hyundai i10','PRG','Hertz',65.00,%s),
    ('Mercedes E220','AMS','Hertz',82.00,%s),
    ('BMW Series 1','BER','Hertz',98.00,%s),
    ('Nissan Qashqai','MAD','Hertz',110.00,%s),
    ('Toyota Yaris','MJT','Avis',69.00,%s),
    ('Tesla Model Y','VIE','Thrifty',134.00,%s),
    ('Fiat 500 Bev','BUD','Hertz',83.00,%s),
    ('Opel Corsa','HEL','Firefly',85.00,%s),
    ('Hyundai i10','ZRH','Hertz',65.00,%s),
    ('Toyota Corolla','LON','Thrifty',90.00,%s),
    ('Seat Ibiza','PRG','Hertz',70.00,%s),
    ('Toyota Corolla','HEL','Hertz',106.00,%s),
    ('Mercedes E220','SKG','Avis',91.00,%s),
    ('Seat Ibiza','CDG','Thrifty',77.00,%s),
    ('Opel Corsa','FCO','Hertz',80.00,%s),
    ('Hyundai i10','LIS','Thrifty',73.00,%s),
    ('Fiat 500 Bev','LIS','Avis',78.00,%s),
    ('Opel Corsa','LIS','Hertz',93.00,%s),
    ('Toyota Corolla','LIS','Hertz',112.00,%s),
    ('Hyundai i10','PRG','Firefly',80.00,%s),
    ('BMW Series 1','AMS','Thrifty',84.00,%s),
    ('BMW Series 1','BER','Firefly',100.00,%s),
    ('Toyota Yaris','MAD','Hertz',70.00,%s),
    ('Nissan Qashqai','MJT','Avis',105.00,%s),
    ('Fiat 500 Bev','VIE','Firefly',70.00,%s),
    ('Fiat 500 Bev','BUD','Thrifty',78.00,%s),
    ('Tesla Model Y','HEL','Hertz',127.00,%s),
    ('Hyundai i10','ZRH','Avis',75.00,%s),
    ('Seat Ibiza','LON','Avis',72.00,%s),
    ('Toyota Corolla','PRG','Avis',88.00,%s),
    ('Hyundai i10','HEL','Thrifty',73.00,%s),
    ('Tesla Model Y','ATH','Firefly',120.00,%s),
    ('Mercedes E220','SKG','Thrifty',94.00,%s),
    ('Toyota Aygo','SKG','Firefly',60.00,%s),
    ('Toyota Aygo','HEL','Thrifty',63.00,%s),
    ('Nissan Qashqai','HEL','Firefly',110.00,%s),
    ('Toyota Yaris','HEL','Avis',67.00,%s);
    """,(images[0],images[1],images[2],images[3],images[4],images[5],
         images[6],images[7],images[8],images[9],images[10],images[1],
         images[2],images[5],images[3],images[4],images[3],images[6],
         images[4],images[2],images[5],images[1],images[2],images[3],
         images[5],images[7],images[7],images[9],images[8],images[1],
         images[1],images[10],images[5],images[4],images[3],images[5],
         images[10],images[6],images[0],images[0],images[8],images[9]))

#insert records into flight_duration table
cur.execute("""
    INSERT INTO flight_duration(departuretime, arrivaltime, duration)
    VALUES
    ('10:00:00+02:00', '10:55:00+02:00','55min'),
    ('12:05:00+02:00', '14:30:00+00:00', '4h 25min'),
    ('18:50:00+02:00', '19:45:00+02:00', '55min'),
    ('12:00:00+00:00', '18:00:00+02:00', '4h'),
    ('13:15:00+01:00', '17:45:00+02:00', '3h 30min'),
    ('09:15:00+02:00', '11:45:00+01:00', '3h 30min'),
    ('12:15:00+02:00', '14:45:00+01:00', '3h 30min'),
    ('12:55:00+01:00', '14:00:00+00:00', '2h 5min'),
    ('18:00:00+00:00', '21:05:00+01:00', '2h 5min'),
    ('14:50:00+01:00', '16:20:00+01:00', '1h 30min'),
    ('09:40:00+01:00', '11:10:00+01:00', '1h 30min'),
    ('09:00:00+01:00', '10:45:00+01:00', '1h 45min'),
    ('21:10:00+01:00', '22:55:00+01:00', '1h 45min'),
    ('08:30:00+01:00', '10:05:00+01:00', '1h 35min'),
    ('16:05:00+01:00', '17:40:00+01:00', '1h 35min'),
    ('18:35:00+02:00', '21:15:00+01:00', '3h 40min'),
    ('13:50:00+01:00', '18:20:00+02:00', '3h 30min'),
    ('08:35:00+02:00', '12:15:00+01:00', '3h 40min'),
    ('07:20:00+01:00', '11:40:00+02:00', '3h 20min'),
    ('07:30:00+02:00', '08:20:00+02:00', '50min'),
    ('10:30:00+02:00', '11:20:00+02:00', '50min'),
    ('14:50:00+02:00', '16:15:00+01:00', '2h 25min'),
    ('17:00:00+01:00', '20:25:00+02:00', '2h 25min'),
    ('10:15:00+01:00', '11:45:00+01:00', '1h 30min'),
    ('17:15:00+01:00', '18:45:00+01:00', '1h 30min'),
    ('22:00:00+01:00', '00:10:00+00:00', '3h 10min'),
    ('08:30:00+02:00', '10:20:00+01:00', '2h 50min'),
    ('16:30:00+01:00', '20:20:00+02:00', '2h 50min'),
    ('07:10:00+01:00', '08:55:00+00:00', '2h 45min'),
    ('21:50:00+00:00', '01:25:00+01:00', '2h 35min'),
    ('11:40:00+02:00', '13:25:00+01:00', '2h 45min'),
    ('17:30:00+01:00', '19:25:00+02:00', '55min'),
    ('09:30:00+02:00', '10:55:00+01:00', '2h 25min'),
    ('12:00:00+01:00', '15:05:00+02:00', '2h 5min'),
    ('16:50:00+02:00', '17:55:00+01:00', '2h 5min'),
    ('19:00:00+01:00', '22:45:00+02:00', '2h 45min'),
    ('10:00:00+02:00', '11:05:00+01:00', '2h 5min'),
    ('23:10:00+02:00', '00:15:00+01:00', '2h 5min'),
    ('07:00:00+01:00', '09:55:00+02:00', '1h 55min'),
    ('18:30:00+02:00', '19:20:00+02:00', '50min'),
    ('16:20:00+02:00', '17:10:00+02:00', '50min'),
    ('12:00:00+02:00', '12:50:00+02:00', '50min'),
    ('12:10:00+02:00', '13:15:00+01:00', '2h 5min'),
    ('18:00:00+01:00', '21:05:00+02:00', '2h 5min'),
    ('14:05:00+01:00', '18:10:00+02:00', '3h 5min'),
    ('10:40:00+02:00', '13:10:00+01:00', '3h 30min'),
    ('18:25:00+02:00', '20:05:00+01:00', '2h 40min'),
    ('20:50:00+01:00', '00:15:00+02:00', '2h 25min'),
    ('09:25:00+02:00', '11:05:00+01:00', '2h 40min'),
    ('11:50:00+01:00', '15:15:00+02:00', '2h 25min'),
    ('17:50:00+02:00', '18:45:00+02:00', '55min'),
    ('16:00:00+02:00', '16:50:00+01:00', '1h 50min'),
    ('20:25:00+01:00', '22:30:00+01:00', '2h 5min'),
    ('07:10:00+01:00', '09:05:00+01:00', '1h 55min'),
    ('11:40:00+01:00', '14:25:00+02:00', '1h 45min'),
    ('16:00:00+01:00', '16:25:00+00:00', '1h 25min'),
    ('16:45:00+00:00', '19:15:00+01:00', '1h 30min'),
    ('20:35:00+01:00', '01:10:00+02:00', '3h 35min'),
    ('21:25:00+02:00', '22:55:00+00:00', '3h 30min'),
    ('08:00:00+00:00', '13:10:00+02:00', '3h 10min'),
    ('08:00:00+02:00', '08:45:00+01:00', '1h 45min'),
    ('11:00:00+01:00', '14:25:00+02:00', '2h 25min'),
    ('17:05:00+02:00', '18:30:00+01:00', '2h 25min'),
    ('21:50:00+01:00', '00:25:00+02:00', '1h 35min'),
    ('10:50:00+02:00', '11:45:00+02:00', '55min'),
    ('09:00:00+02:00', '09:50:00+02:00', '50min'),
    ('18:50:00+02:00', '19:40:00+02:00', '50min'),
    ('17:00:00+02:00', '17:55:00+02:00', '55min');
""")


#insert records into flight table
cur.execute("""
    INSERT INTO flight(flightid, flightdate, departuretime, arrivaltime, economyprice, flexprice, businessprice, departureairport, arrivalairport, airplane)
    VALUES
    ('MJ129', '22/03/2024', '10:00:00+02:00', '10:55:00+02:00', 100.00, 115.00, 200.00, 'MJT', 'ATH', 'FN1'),
    ('AT121', '22/03/2024', '17:50:00+02:00', '18:45:00+02:00', 100.00, 115.00, 200.00, 'ATH', 'SKG', 'FN2'),
    ('SK129', '23/03/2024', '10:00:00+02:00', '10:55:00+02:00', 100.00, 115.00, 200.00, 'SKG', 'ATH', 'FN1'),
    ('AT131', '23/03/2024', '17:50:00+02:00', '18:45:00+02:00', 100.00, 115.00, 200.00, 'ATH', 'MJT', 'FN2'),
    ('SK123', '20/05/2024', '10:00:00+02:00', '10:55:00+02:00', 100.00, 115.00, 200.00, 'SKG', 'ATH', 'FN1'),
    ('AT567', '18/06/2024', '12:05:00+02:00', '14:30:00+00:00', 95.00, 110.00, 190.00, 'ATH', 'LIS', 'FN4'),
    ('AT124', '22/05/2024', '18:50:00+02:00', '19:45:00+02:00', 60.00, 75.00, 120.00, 'ATH', 'SKG', 'FN1'),
    ('MA456', '22/05/2024', '13:15:00+01:00', '17:45:00+02:00', 75.00, 90.00, 150.00, 'MAD', 'ATH', 'FN2'),
    ('AT457', '26/05/2024', '09:15:00+02:00', '11:45:00+01:00', 75.00, 90.00, 150.00, 'ATH', 'MAD', 'FN2'),
    ('BE789', '25/05/2024', '12:55:00+01:00', '14:00:00+00:00', 120.00, 135.00, 240.00, 'BER', 'LON', 'FN3'),
    ('LO790', '29/05/2024', '18:00:00+00:00', '21:05:00+01:00', 125.00, 140.00, 250.00, 'LON', 'BER', 'FN3'),
    ('AM234', '28/05/2024', '14:50:00+01:00', '16:20:00+01:00', 50.00, 65.00, 100.00, 'AMS', 'PRG', 'FN4'),
    ('PR235', '01/06/2024', '09:40:00+01:00', '11:10:00+01:00', 70.00, 85.00, 140.00, 'PRG', 'AMS', 'FN4'),
    ('CD567', '30/05/2024', '09:00:00+01:00', '10:45:00+01:00', 100.00, 115.00, 200.00, 'CDG', 'BER', 'FN5'),
    ('BE568', '06/06/2024', '21:10:00+01:00', '22:55:00+01:00', 85.00, 100.00, 170.00, 'BER', 'CDG', 'FN5'),
    ('FC890', '30/05/2024', '08:30:00+01:00', '10:05:00+01:00', 90.00, 105.00, 180.00, 'FCO', 'ZRH', 'FN6'),
    ('ZR891', '02/06/2024', '16:05:00+01:00', '17:40:00+01:00', 110.00, 125.00, 220.00, 'ZRH', 'FCO', 'FN6'),
    ('AT123', '02/06/2024', '18:35:00+02:00', '21:15:00+01:00', 65.00, 80.00, 130.00, 'ATH', 'AMS', 'FN1'),
    ('AM124', '07/06/2024', '13:50:00+01:00', '18:20:00+02:00', 85.00, 100.00, 170.00, 'AMS', 'ATH', 'FN7'),
    ('AT159', '02/06/2024', '08:35:00+02:00', '12:15:00+01:00', 105.00, 120.00, 210.00, 'ATH', 'AMS', 'FN5'),
    ('AM192', '05/06/2024', '07:20:00+01:00', '11:40:00+02:00', 90.00, 105.00, 180.00, 'AMS', 'ATH', 'FN4'),
    ('SK459', '03/06/2024', '07:30:00+02:00', '08:20:00+02:00', 95.00, 110.00, 190.00, 'SKG', 'MJT', 'FN3'),
    ('MJ458', '08/06/2024', '10:30:00+02:00', '11:20:00+02:00', 60.00, 75.00, 120.00, 'MJT', 'SKG', 'FN3'),
    ('AT789', '02/06/2024', '14:50:00+02:00', '16:15:00+01:00', 100.00, 115.00, 200.00, 'ATH', 'VIE', 'FN9'),
    ('VI800', '03/06/2024', '17:00:00+01:00', '20:25:00+02:00', 130.00, 145.00, 260.00, 'VIE', 'ATH', 'FN9'),
    ('BU234', '04/06/2024', '10:15:00+01:00', '11:45:00+01:00', 70.00, 85.00, 140.00, 'BUD', 'ZRH', 'FN10'),
    ('ZR235', '06/06/2024', '17:15:00+01:00', '18:45:00+01:00', 55.00, 70.00, 110.00, 'ZRH', 'BUD', 'FN10'),    
    ('AM329', '02/06/2024', '22:00:00+01:00', '00:10:00+00:00', 75.00, 90.00, 150.00, 'AMS', 'LIS', 'FN2'),
    ('AT598', '03/06/2024', '08:30:00+02:00', '10:20:00+01:00', 95.00, 110.00, 190.00, 'ATH', 'ZRH', 'FN1'),
    ('ZR567', '05/06/2024', '16:30:00+01:00', '20:20:00+02:00', 120.00, 135.00, 240.00, 'ZRH', 'ATH', 'FN1'),
    ('FC899', '08/06/2024', '07:10:00+01:00', '08:55:00+00:00', 120.00, 135.00, 240.00, 'FCO', 'LON', 'FN6'),
    ('LO899', '10/06/2024', '21:50:00+00:00', '01:25:00+01:00', 150.00, 165.00, 300.00, 'LON', 'FCO', 'FN9'),
    ('AT122', '09/06/2024', '11:40:00+02:00', '13:25:00+01:00', 65.00, 80.00, 130.00, 'ATH', 'BER', 'FN1'),
    ('BE113', '09/06/2024', '17:30:00+01:00', '19:25:00+02:00', 95.00, 110.00, 190.00, 'BER', 'HEL', 'FN4'),
    ('HE113', '15/06/2024', '09:30:00+02:00', '10:55:00+01:00', 95.00, 110.00, 190.00, 'HEL', 'BUD', 'FN2'),
    ('BU213', '15/06/2024', '12:00:00+01:00', '15:05:00+02:00', 55.00, 70.00, 110.00, 'BUD', 'ATH', 'FN8'),
    ('HE176', '15/06/2024', '16:50:00+02:00', '17:55:00+01:00', 165.00, 180.00, 330.00, 'HEL', 'BER', 'FN1'),
    ('BE290', '15/06/2024', '19:00:00+01:00', '22:45:00+02:00', 100.00, 115.00, 200.00, 'BER', 'ATH', 'FN2'),
    ('AT223', '05/06/2024', '10:00:00+02:00', '11:05:00+01:00', 95.00, 110.00, 190.00, 'ATH', 'BUD', 'FN8'),
    ('AT801', '07/06/2024', '23:10:00+02:00', '00:15:00+01:00', 120.00, 135.00, 240.00, 'ATH', 'FCO', 'FN3'),
    ('FC893', '11/06/2024', '07:00:00+01:00', '09:55:00+02:00', 50.00, 65.00, 100.00, 'FCO', 'ATH', 'FN7'),
    ('AT102', '10/06/2024', '10:30:00+02:00', '11:20:00+02:00', 95.00, 110.00, 190.00, 'ATH', 'MJT', 'FN5'),
    ('MJ500', '15/06/2024', '18:30:00+02:00', '19:20:00+02:00', 75.00, 90.00, 150.00, 'MJT', 'ATH', 'FN5'),
    ('AT103', '10/06/2024', '16:20:00+02:00', '17:10:00+02:00', 60.00, 75.00, 120.00, 'ATH', 'MJT', 'FN3'),
    ('MJ501', '15/06/2024', '12:00:00+02:00', '12:50:00+02:00', 80.00, 95.00, 160.00, 'MJT', 'ATH', 'FN3'),
    ('AT802', '07/06/2024', '12:10:00+02:00', '13:15:00+01:00', 100.00, 115.00, 200.00, 'ATH', 'FCO', 'FN4'),
    ('FC894', '11/06/2024', '18:00:00+01:00', '21:05:00+02:00', 70.00, 85.00, 140.00, 'FCO', 'ATH', 'FN4'),
    ('CD543', '10/06/2024', '14:05:00+01:00', '18:10:00+02:00', 90.00, 105.00, 180.00, 'CDG', 'ATH', 'FN6'),
    ('AT521', '12/06/2024', '10:40:00+02:00', '13:10:00+01:00', 75.00, 90.00, 150.00, 'ATH', 'CDG', 'FN6'),
    ('AT220', '28/05/2024', '18:25:00+02:00', '20:05:00+01:00', 60.00, 75.00, 120.00, 'ATH', 'PRG', 'FN1'),
    ('PR773', '01/06/2024', '20:50:00+01:00', '00:15:00+02:00', 65.00, 80.00, 130.00, 'PRG', 'ATH', 'FN2'),    
    ('AT222', '28/05/2024', '09:25:00+02:00', '11:05:00+01:00', 80.00, 95.00, 160.00, 'ATH', 'PRG', 'FN8'),
    ('PR775', '03/06/2024', '11:50:00+01:00', '15:15:00+02:00', 85.00, 100.00, 160.00, 'PRG', 'ATH', 'FN9'),
    ('SK150', '13/06/2024', '10:00:00+02:00', '10:55:00+02:00', 80.00, 95.00, 160.00, 'SKG', 'ATH', 'FN1'),
    ('AT151', '09/06/2024', '17:50:00+02:00', '18:45:00+02:00', 50.00, 65.00, 100.00, 'ATH', 'SKG', 'FN9'),
    ('SK527', '14/06/2024', '16:00:00+02:00', '16:50:00+01:00', 150.00, 165.00, 300.00, 'SKG', 'VIE', 'FN6'),
    ('VI549', '14/06/2024', '20:25:00+01:00', '22:30:00+01:00', 135.00, 150.00, 270.00, 'VIE', 'CDG', 'FN7'),
    ('CD501', '19/06/2024', '07:10:00+01:00', '09:05:00+01:00', 70.00, 85.00, 140.00, 'CDG', 'VIE', 'FN2'),
    ('VI502', '19/06/2024', '11:40:00+01:00', '14:25:00+02:00', 105.00, 120.00, 210.00, 'VIE', 'SKG', 'FN9'),
    ('AT466', '18/06/2024', '12:15:00+02:00', '14:45:00+01:00', 70.00, 85.00, 140.00, 'ATH', 'MAD', 'FN1'),
    ('MA429', '18/06/2024', '16:00:00+01:00', '16:25:00+00:00', 100.00, 115.00, 200.00, 'MAD', 'LIS', 'FN3'),
    ('LI122', '24/06/2024', '16:45:00+00:00', '19:15:00+01:00', 100.00, 115.00, 200.00, 'LIS', 'MAD', 'FN8'),
    ('MA448', '24/06/2024', '20:35:00+01:00', '01:10:00+02:00', 100.00, 115.00, 200.00, 'MAD', 'ATH', 'FN3'),
    ('SK167', '23/06/2024', '21:25:00+02:00', '22:55:00+00:00', 130.00, 145.00, 260.00, 'SKG', 'LON', 'FN5'),
    ('LO117', '28/06/2024', '08:00:00+00:00', '13:10:00+02:00', 110.00, 125.00, 220.00, 'LON', 'SKG', 'FN5'),
    ('SK522', '17/06/2024', '08:00:00+02:00', '08:45:00+01:00', 70.00, 85.00, 140.00, 'SKG', 'BUD', 'FN10'),
    ('BU502', '17/06/2024', '11:00:00+01:00', '14:25:00+02:00', 80.00, 95.00, 160.00, 'BUD', 'HEL', 'FN6'),
    ('HE112', '24/06/2024', '17:05:00+02:00', '18:30:00+01:00', 180.00, 195.00, 360.00, 'HEL', 'BUD', 'FN6'),
    ('BU812', '24/06/2024', '21:50:00+01:00', '00:25:00+02:00', 60.00, 75.00, 120.00, 'BUD', 'SKG', 'FN9'),
    ('AT154', '09/06/2024', '10:50:00+02:00', '11:45:00+02:00', 60.00, 75.00, 120.00, 'ATH', 'SKG', 'FN11'),
    ('AT158', '09/06/2024', '09:00:00+02:00', '09:50:00+02:00', 70.00, 85.00, 140.00, 'ATH', 'MJT', 'FN12'),
    ('MJ155', '15/06/2024', '18:50:00+02:00', '19:40:00+02:00', 55.00, 70.00, 110.00, 'MJT', 'ATH', 'FN12'),
    ('SK153', '15/06/2024', '17:00:00+02:00', '17:55:00+02:00', 75.00, 90.00, 150.00, 'SKG', 'ATH', 'FN11'),
    ('LI603', '24/06/2024', '12:00:00+00:00', '18:00:00+02:00', 120.00, 135.00, 240.00, 'LIS', 'ATH', 'FN1');
""")

#insert records into reservation table
cur.execute("""
    INSERT INTO reservation(reservationid, petsize, wifionboard, price)
    VALUES
    ('AHDSSD', NULL, 0, 300.00),
    ('DFAKEM', NULL, 0, 195.00),
    ('HAOSMC', NULL, 0, 85.00),
    ('POWTWU', NULL, 0, 1020.00),
    ('UIWERT', NULL, 0, 135.00),
    ('BCVZSD', NULL, 0, 135.00),
    ('LAKSJF', 'Large', 0, 525.00),
    ('JQUERF', 'Small', 0, 255.00),
    ('AKJDFN', NULL, 0, 95.00),
    ('SAKSDM', NULL, 0, 100.00),
    ('QEIEUN', NULL, 0, 100.00),
    ('SAHIBU', NULL, 0, 100.00),
    ('ADBBSA', NULL, 0, 100.00),
    ('IAKDKA', NULL, 0, 70.00),
    ('AKSDJS', NULL, 0, 70.00);
""")

#insert records into has table
cur.execute("""
    INSERT INTO has(passengerid, flightid, reservationid, seatnumber, classtype, baggage, numofbaggagepercategory, checkin)
    VALUES
    (1, 'SK527', 'AHDSSD', '1A', 'BUSINESS CLASS', 'baggage23kg', 1, false),
    (1, 'SK527', 'AHDSSD', '1A', 'BUSINESS CLASS', 'baggage32kg', 0, false),
    (2, 'AM192', 'DFAKEM', '1B', 'BUSINESS CLASS', 'baggage23kg', 1, false),
    (2, 'AM192', 'DFAKEM', '1B', 'BUSINESS CLASS', 'baggage32kg', 1, false),
    (3, 'FC894', 'HAOSMC', '1C', 'FLEX CLASS', 'baggage23kg', 1, false),
    (3, 'FC894', 'HAOSMC', '1C', 'FLEX CLASS', 'baggage32kg', 0, false),
    (4, 'HE176', 'POWTWU', '1D', 'FLEX CLASS', 'baggage32kg', 1, false),
    (5, 'HE176', 'POWTWU', '1E', 'FLEX CLASS', 'baggage32kg', 1, false),
    (6, 'HE176', 'POWTWU', '1F', 'FLEX CLASS', 'baggage32kg', 1, false),
    (4, 'BE290', 'POWTWU', '2D', 'FLEX CLASS', 'baggage32kg', 1, false),
    (5, 'BE290', 'POWTWU', '2E', 'FLEX CLASS', 'baggage32kg', 1, false),
    (6, 'BE290', 'POWTWU', '2F', 'FLEX CLASS', 'baggage32kg', 1, false),
    (4, 'HE176', 'POWTWU', '1D', 'FLEX CLASS', 'baggage23kg', 0, false),
    (5, 'HE176', 'POWTWU', '1E', 'FLEX CLASS', 'baggage23kg', 0, false),
    (6, 'HE176', 'POWTWU', '1F', 'FLEX CLASS', 'baggage23kg', 0, false),
    (4, 'BE290', 'POWTWU', '2D', 'FLEX CLASS', 'baggage23kg', 0, false),
    (5, 'BE290', 'POWTWU', '2E', 'FLEX CLASS', 'baggage23kg', 0, false),
    (6, 'BE290', 'POWTWU', '2F', 'FLEX CLASS', 'baggage23kg', 0, false),
    (7, 'LI603', 'UIWERT', '1E', 'FLEX CLASS', 'baggage23kg', 0, false),
    (7, 'LI603', 'UIWERT', '1E', 'FLEX CLASS', 'baggage32kg', 0, false),
    (8, 'LI603', 'BCVZSD', '2F', 'FLEX CLASS', 'baggage23kg', 0, false),
    (8, 'LI603', 'BCVZSD', '2F', 'FLEX CLASS', 'baggage32kg', 0, false),
    (9, 'ZR567', 'LAKSJF', '1E', 'ECONOMY CLASS', 'baggage32kg', 1, false),
    (10, 'ZR567', 'LAKSJF', '5D', 'ECONOMY CLASS', 'baggage32kg', 1, false),
    (11, 'ZR567', 'LAKSJF', '5E', 'ECONOMY CLASS', 'baggage32kg', 1, false),
    (9, 'ZR567', 'LAKSJF', '1E', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (10, 'ZR567', 'LAKSJF', '5D', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (11, 'ZR567', 'LAKSJF', '5E', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (12, 'AT567', 'JQUERF', '7E', 'ECONOMY CLASS', 'baggage23kg', 1, false),
    (13, 'AT567', 'JQUERF', '1A', 'ECONOMY CLASS', 'baggage23kg', 1, false),
    (12, 'AT567', 'JQUERF', '7E', 'ECONOMY CLASS', 'baggage32kg', 0, false),
    (13, 'AT567', 'JQUERF', '1A', 'ECONOMY CLASS', 'baggage32kg', 0, false),
    (14, 'AT567', 'AKJDFN', '2A', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (15, 'AT789', 'SAKSDM', '2D', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (16, 'AT789', 'QEIEUN', '3E', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (17, 'AT802', 'SAHIBU', '1F', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (18, 'AT802', 'ADBBSA', '2C', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (19, 'AT466', 'IAKDKA', '1B', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (20, 'AT466', 'AKSDJS', '4D', 'ECONOMY CLASS', 'baggage23kg', 0, false),
    (14, 'AT567', 'AKJDFN', '2A', 'ECONOMY CLASS', 'baggage32kg', 0, false),
    (15, 'AT789', 'SAKSDM', '2D', 'ECONOMY CLASS', 'baggage32kg', 0, false),
    (16, 'AT789', 'QEIEUN', '3E', 'ECONOMY CLASS', 'baggage32kg', 0, false),
    (17, 'AT802', 'SAHIBU', '1F', 'ECONOMY CLASS', 'baggage32kg', 0, false),
    (18, 'AT802', 'ADBBSA', '2C', 'ECONOMY CLASS', 'baggage32kg', 0, false),
    (19, 'AT466', 'IAKDKA', '1B', 'ECONOMY CLASS', 'baggage32kg', 0, false),
    (20, 'AT466', 'AKSDJS', '4D', 'ECONOMY CLASS', 'baggage32kg', 0, false);
""")

#create the trigger function that deletes the expired car rentings
cur.execute("""DROP TRIGGER IF EXISTS before_insert_trigger_car ON contains""")
cur.execute("""DROP FUNCTION IF EXISTS delete_expired_car_rentings()""")
cur.execute("""
    CREATE OR REPLACE FUNCTION delete_expired_car_rentings()
    RETURNS TRIGGER AS $$
    BEGIN
        DELETE FROM contains
        WHERE return < CURRENT_TIMESTAMP;

        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;
""")

#create the trigger that automatically is executed before every insert in the table contains
cur.execute("""
    CREATE TRIGGER before_insert_trigger_car
    BEFORE INSERT ON contains
    FOR EACH ROW EXECUTE FUNCTION delete_expired_car_rentings();
""")

# Make the changes to the FlyNow database persistent
conn.commit()

# Close cursor and communication with the database
cur.close()
conn.close()
