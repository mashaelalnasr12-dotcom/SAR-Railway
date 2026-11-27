-- Academic demo data, not official SAR timetables or fares.
-- All 20 directed routes among the five existing passenger stations.
-- Four scheduled departures per route daily; some routes are fictional.
-- Existing stations, trains, trips and bookings are preserved.
-- Run after railway_db.sql. Safe to rerun sequentially.
USE railway_db;

DROP TEMPORARY TABLE IF EXISTS demo_passenger_schedule;
CREATE TEMPORARY TABLE demo_passenger_schedule (
    origin_name VARCHAR(100),
    destination_name VARCHAR(100),
    train_name VARCHAR(100),
    departure_clock TIME,
    duration_minutes INT,
    fare DECIMAL(10,2)
);

INSERT INTO demo_passenger_schedule VALUES
    ('Riyadh Station', 'Dammam Station', 'SAR Express 1', '05:00:00', 240, 95.00),
    ('Riyadh Station', 'Dammam Station', 'SAR Express 1', '08:00:00', 240, 120.00),
    ('Riyadh Station', 'Dammam Station', 'SAR Express 1', '14:00:00', 240, 110.00),
    ('Riyadh Station', 'Dammam Station', 'SAR Express 1', '19:00:00', 240, 135.00),
    ('Dammam Station', 'Riyadh Station', 'SAR Express 2', '05:00:00', 240, 95.00),
    ('Dammam Station', 'Riyadh Station', 'SAR Express 2', '08:00:00', 240, 120.00),
    ('Dammam Station', 'Riyadh Station', 'SAR Express 2', '17:00:00', 240, 120.00),
    ('Dammam Station', 'Riyadh Station', 'SAR Express 2', '19:00:00', 240, 135.00),
    ('Jeddah Station', 'Makkah Station', 'Haramain Express', '05:00:00', 90, 55.00),
    ('Jeddah Station', 'Makkah Station', 'Haramain Express', '10:00:00', 90, 80.00),
    ('Jeddah Station', 'Makkah Station', 'Haramain Express', '14:00:00', 90, 70.00),
    ('Jeddah Station', 'Makkah Station', 'Haramain Express', '19:00:00', 90, 95.00),
    ('Makkah Station', 'Jeddah Station', 'Haramain Express', '05:00:00', 90, 55.00),
    ('Makkah Station', 'Jeddah Station', 'Haramain Express', '08:00:00', 90, 80.00),
    ('Makkah Station', 'Jeddah Station', 'Haramain Express', '14:00:00', 90, 70.00),
    ('Makkah Station', 'Jeddah Station', 'Haramain Express', '19:00:00', 90, 95.00),
    ('Jeddah Station', 'Madinah Station', 'Haramain Express', '05:00:00', 150, 100.00),
    ('Jeddah Station', 'Madinah Station', 'Haramain Express', '08:00:00', 150, 125.00),
    ('Jeddah Station', 'Madinah Station', 'Haramain Express', '14:00:00', 150, 115.00),
    ('Jeddah Station', 'Madinah Station', 'Haramain Express', '19:00:00', 150, 140.00),
    ('Madinah Station', 'Jeddah Station', 'Haramain Express', '05:00:00', 150, 100.00),
    ('Madinah Station', 'Jeddah Station', 'Haramain Express', '08:00:00', 150, 125.00),
    ('Madinah Station', 'Jeddah Station', 'Haramain Express', '14:00:00', 150, 115.00),
    ('Madinah Station', 'Jeddah Station', 'Haramain Express', '19:00:00', 150, 140.00),
    ('Makkah Station', 'Madinah Station', 'Haramain Express', '05:00:00', 120, 70.00),
    ('Makkah Station', 'Madinah Station', 'Haramain Express', '08:00:00', 120, 95.00),
    ('Makkah Station', 'Madinah Station', 'Haramain Express', '15:00:00', 120, 95.00),
    ('Makkah Station', 'Madinah Station', 'Haramain Express', '19:00:00', 120, 110.00),
    ('Madinah Station', 'Makkah Station', 'Haramain Express', '05:00:00', 120, 70.00),
    ('Madinah Station', 'Makkah Station', 'Haramain Express', '08:00:00', 120, 95.00),
    ('Madinah Station', 'Makkah Station', 'Haramain Express', '14:00:00', 120, 85.00),
    ('Madinah Station', 'Makkah Station', 'Haramain Express', '19:00:00', 120, 110.00),
    ('Riyadh Station', 'Jeddah Station', 'SAR Express 1', '05:00:00', 480, 190.00),
    ('Riyadh Station', 'Jeddah Station', 'SAR Express 1', '08:00:00', 480, 215.00),
    ('Riyadh Station', 'Jeddah Station', 'SAR Express 1', '14:00:00', 480, 205.00),
    ('Riyadh Station', 'Jeddah Station', 'SAR Express 1', '19:00:00', 480, 230.00),
    ('Jeddah Station', 'Riyadh Station', 'SAR Express 2', '05:00:00', 480, 190.00),
    ('Jeddah Station', 'Riyadh Station', 'SAR Express 2', '08:00:00', 480, 215.00),
    ('Jeddah Station', 'Riyadh Station', 'SAR Express 2', '14:00:00', 480, 205.00),
    ('Jeddah Station', 'Riyadh Station', 'SAR Express 2', '19:00:00', 480, 230.00),
    ('Riyadh Station', 'Makkah Station', 'SAR Express 1', '05:00:00', 540, 210.00),
    ('Riyadh Station', 'Makkah Station', 'SAR Express 1', '08:00:00', 540, 235.00),
    ('Riyadh Station', 'Makkah Station', 'SAR Express 1', '14:00:00', 540, 225.00),
    ('Riyadh Station', 'Makkah Station', 'SAR Express 1', '19:00:00', 540, 250.00),
    ('Makkah Station', 'Riyadh Station', 'SAR Express 2', '05:00:00', 540, 210.00),
    ('Makkah Station', 'Riyadh Station', 'SAR Express 2', '08:00:00', 540, 235.00),
    ('Makkah Station', 'Riyadh Station', 'SAR Express 2', '14:00:00', 540, 225.00),
    ('Makkah Station', 'Riyadh Station', 'SAR Express 2', '19:00:00', 540, 250.00),
    ('Riyadh Station', 'Madinah Station', 'SAR Express 1', '05:00:00', 420, 175.00),
    ('Riyadh Station', 'Madinah Station', 'SAR Express 1', '08:00:00', 420, 200.00),
    ('Riyadh Station', 'Madinah Station', 'SAR Express 1', '14:00:00', 420, 190.00),
    ('Riyadh Station', 'Madinah Station', 'SAR Express 1', '19:00:00', 420, 215.00),
    ('Madinah Station', 'Riyadh Station', 'SAR Express 2', '05:00:00', 420, 175.00),
    ('Madinah Station', 'Riyadh Station', 'SAR Express 2', '08:00:00', 420, 200.00),
    ('Madinah Station', 'Riyadh Station', 'SAR Express 2', '14:00:00', 420, 190.00),
    ('Madinah Station', 'Riyadh Station', 'SAR Express 2', '19:00:00', 420, 215.00),
    ('Dammam Station', 'Jeddah Station', 'SAR Express 1', '05:00:00', 720, 260.00),
    ('Dammam Station', 'Jeddah Station', 'SAR Express 1', '08:00:00', 720, 285.00),
    ('Dammam Station', 'Jeddah Station', 'SAR Express 1', '14:00:00', 720, 275.00),
    ('Dammam Station', 'Jeddah Station', 'SAR Express 1', '19:00:00', 720, 300.00),
    ('Jeddah Station', 'Dammam Station', 'SAR Express 2', '05:00:00', 720, 260.00),
    ('Jeddah Station', 'Dammam Station', 'SAR Express 2', '08:00:00', 720, 285.00),
    ('Jeddah Station', 'Dammam Station', 'SAR Express 2', '14:00:00', 720, 275.00),
    ('Jeddah Station', 'Dammam Station', 'SAR Express 2', '19:00:00', 720, 300.00),
    ('Dammam Station', 'Makkah Station', 'SAR Express 1', '05:00:00', 780, 280.00),
    ('Dammam Station', 'Makkah Station', 'SAR Express 1', '08:00:00', 780, 305.00),
    ('Dammam Station', 'Makkah Station', 'SAR Express 1', '14:00:00', 780, 295.00),
    ('Dammam Station', 'Makkah Station', 'SAR Express 1', '19:00:00', 780, 320.00),
    ('Makkah Station', 'Dammam Station', 'SAR Express 2', '05:00:00', 780, 280.00),
    ('Makkah Station', 'Dammam Station', 'SAR Express 2', '08:00:00', 780, 305.00),
    ('Makkah Station', 'Dammam Station', 'SAR Express 2', '14:00:00', 780, 295.00),
    ('Makkah Station', 'Dammam Station', 'SAR Express 2', '19:00:00', 780, 320.00),
    ('Dammam Station', 'Madinah Station', 'SAR Express 1', '05:00:00', 660, 240.00),
    ('Dammam Station', 'Madinah Station', 'SAR Express 1', '08:00:00', 660, 265.00),
    ('Dammam Station', 'Madinah Station', 'SAR Express 1', '14:00:00', 660, 255.00),
    ('Dammam Station', 'Madinah Station', 'SAR Express 1', '19:00:00', 660, 280.00),
    ('Madinah Station', 'Dammam Station', 'SAR Express 2', '05:00:00', 660, 240.00),
    ('Madinah Station', 'Dammam Station', 'SAR Express 2', '08:00:00', 660, 265.00),
    ('Madinah Station', 'Dammam Station', 'SAR Express 2', '14:00:00', 660, 255.00),
    ('Madinah Station', 'Dammam Station', 'SAR Express 2', '19:00:00', 660, 280.00);

DROP PROCEDURE IF EXISTS add_sar_demo_trips;
DELIMITER $$
CREATE PROCEDURE add_sar_demo_trips()
BEGIN
    DECLARE trip_day DATE;
    DECLARE last_day DATE DEFAULT '2027-01-31';
    SET trip_day = GREATEST(CURDATE(), '2026-11-05');

    WHILE trip_day <= last_day DO
        INSERT INTO trips
            (train_id, departure_station, arrival_station,
             departure_time, arrival_time, price, status)
        SELECT tr.train_id, s1.station_id, s2.station_id,
               TIMESTAMP(trip_day, d.departure_clock),
               TIMESTAMPADD(MINUTE, d.duration_minutes,
                            TIMESTAMP(trip_day, d.departure_clock)),
               d.fare, 'Available'
        FROM demo_passenger_schedule d
        JOIN stations s1 ON s1.station_id = (
            SELECT MIN(station_id) FROM stations
            WHERE station_name = d.origin_name)
        JOIN stations s2 ON s2.station_id = (
            SELECT MIN(station_id) FROM stations
            WHERE station_name = d.destination_name)
        JOIN trains tr ON tr.train_id = (
            SELECT MIN(train_id) FROM trains
            WHERE train_name = d.train_name AND train_type = 'Passenger')
        WHERE NOT EXISTS (
            SELECT 1 FROM trips t
            JOIN trains existing_train ON existing_train.train_id = t.train_id
            WHERE t.departure_station = s1.station_id
              AND t.arrival_station = s2.station_id
              AND t.departure_time = TIMESTAMP(trip_day, d.departure_clock)
              AND existing_train.train_type = 'Passenger'
        );
        SET trip_day = DATE_ADD(trip_day, INTERVAL 1 DAY);
    END WHILE;
END$$
DELIMITER ;

CALL add_sar_demo_trips();
DROP PROCEDURE IF EXISTS add_sar_demo_trips;
DROP TEMPORARY TABLE IF EXISTS demo_passenger_schedule;

-- Verify the available passenger options for tomorrow.
SELECT s1.station_name AS departure_station,
       s2.station_name AS arrival_station,
       COUNT(*) AS trip_options
FROM trips t
JOIN stations s1 ON s1.station_id = t.departure_station
JOIN stations s2 ON s2.station_id = t.arrival_station
JOIN trains tr ON tr.train_id = t.train_id
WHERE tr.train_type = 'Passenger'
  AND DATE(t.departure_time) = DATE_ADD(CURDATE(), INTERVAL 1 DAY)
GROUP BY s1.station_name, s2.station_name
ORDER BY s1.station_name, s2.station_name;
