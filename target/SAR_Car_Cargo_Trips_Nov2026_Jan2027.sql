-- SAR Railway Management System - demo Car Cargo schedule
-- Dates: 6 November 2026 through 31 January 2027 (future dates only).
-- Routes: Riyadh Station <-> Qurayyat Station.
-- One trip in each direction every day; an extra trip in each direction on Friday and Saturday.
-- Existing trips and bookings are preserved. Re-running does not insert duplicate departures.
-- Run this SQL by itself in MySQL Workbench. Do NOT run DROP DATABASE.

USE railway_db;

-- Ensure the stations exist (exact names required by Customer.java and Cargo.java).
INSERT INTO stations (station_name, city)
SELECT 'Riyadh Station', 'Riyadh'
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_name = 'Riyadh Station');

INSERT INTO stations (station_name, city)
SELECT 'Qurayyat Station', 'Qurayyat'
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_name = 'Qurayyat Station');

-- Keep the cargo trains separate from the existing passenger trains.
-- SAR Cargo 1 also matches the train created by the earlier one-day example.
INSERT INTO trains (train_name, train_type, capacity)
SELECT 'SAR Cargo 1', 'Cargo', 8
WHERE NOT EXISTS (SELECT 1 FROM trains WHERE train_name = 'SAR Cargo 1');

INSERT INTO trains (train_name, train_type, capacity)
SELECT 'SAR Cargo 2', 'Cargo', 8
WHERE NOT EXISTS (SELECT 1 FROM trains WHERE train_name = 'SAR Cargo 2');

INSERT INTO trains (train_name, train_type, capacity)
SELECT 'SAR Cargo Weekend 1', 'Cargo', 8
WHERE NOT EXISTS (SELECT 1 FROM trains WHERE train_name = 'SAR Cargo Weekend 1');

INSERT INTO trains (train_name, train_type, capacity)
SELECT 'SAR Cargo Weekend 2', 'Cargo', 8
WHERE NOT EXISTS (SELECT 1 FROM trains WHERE train_name = 'SAR Cargo Weekend 2');

DROP PROCEDURE IF EXISTS add_sar_cargo_trips;
DELIMITER $$
CREATE PROCEDURE add_sar_cargo_trips()
BEGIN
    DECLARE d DATE DEFAULT '2026-11-06';
    DECLARE end_d DATE DEFAULT '2027-01-31';
    DECLARE riyadh_id INT;
    DECLARE qurayyat_id INT;
    DECLARE cargo_1 INT;
    DECLARE cargo_2 INT;
    DECLARE cargo_weekend_1 INT;
    DECLARE cargo_weekend_2 INT;

    SELECT MIN(station_id) INTO riyadh_id
      FROM stations WHERE station_name = 'Riyadh Station';
    SELECT MIN(station_id) INTO qurayyat_id
      FROM stations WHERE station_name = 'Qurayyat Station';
    SELECT MIN(train_id) INTO cargo_1
      FROM trains WHERE train_name = 'SAR Cargo 1';
    SELECT MIN(train_id) INTO cargo_2
      FROM trains WHERE train_name = 'SAR Cargo 2';
    SELECT MIN(train_id) INTO cargo_weekend_1
      FROM trains WHERE train_name = 'SAR Cargo Weekend 1';
    SELECT MIN(train_id) INTO cargo_weekend_2
      FROM trains WHERE train_name = 'SAR Cargo Weekend 2';

    WHILE d <= end_d DO
        -- Do not create new trips on dates that have already passed.
        IF d >= CURDATE() THEN

            -- Riyadh -> Qurayyat: daily 08:00-18:33.
            INSERT INTO trips
               (train_id, departure_station, arrival_station,
                departure_time, arrival_time, price, status)
            SELECT cargo_1, riyadh_id, qurayyat_id,
                   TIMESTAMP(d, '08:00:00'), TIMESTAMP(d, '18:33:00'),
                   200.00, 'Available'
            WHERE NOT EXISTS (
                SELECT 1 FROM trips
                WHERE departure_station = riyadh_id
                  AND arrival_station = qurayyat_id
                  AND departure_time = TIMESTAMP(d, '08:00:00')
            );

            -- Qurayyat -> Riyadh: daily 08:00-18:33.
            INSERT INTO trips
               (train_id, departure_station, arrival_station,
                departure_time, arrival_time, price, status)
            SELECT cargo_2, qurayyat_id, riyadh_id,
                   TIMESTAMP(d, '08:00:00'), TIMESTAMP(d, '18:33:00'),
                   200.00, 'Available'
            WHERE NOT EXISTS (
                SELECT 1 FROM trips
                WHERE departure_station = qurayyat_id
                  AND arrival_station = riyadh_id
                  AND departure_time = TIMESTAMP(d, '08:00:00')
            );

            -- Friday (6) and Saturday (7): add one evening trip per direction.
            IF DAYOFWEEK(d) IN (6, 7) THEN
                INSERT INTO trips
                   (train_id, departure_station, arrival_station,
                    departure_time, arrival_time, price, status)
                SELECT cargo_weekend_1, riyadh_id, qurayyat_id,
                       TIMESTAMP(d, '20:00:00'),
                       TIMESTAMP(DATE_ADD(d, INTERVAL 1 DAY), '06:33:00'),
                       230.00, 'Available'
                WHERE NOT EXISTS (
                    SELECT 1 FROM trips
                    WHERE departure_station = riyadh_id
                      AND arrival_station = qurayyat_id
                      AND departure_time = TIMESTAMP(d, '20:00:00')
                );

                INSERT INTO trips
                   (train_id, departure_station, arrival_station,
                    departure_time, arrival_time, price, status)
                SELECT cargo_weekend_2, qurayyat_id, riyadh_id,
                       TIMESTAMP(d, '20:00:00'),
                       TIMESTAMP(DATE_ADD(d, INTERVAL 1 DAY), '06:33:00'),
                       230.00, 'Available'
                WHERE NOT EXISTS (
                    SELECT 1 FROM trips
                    WHERE departure_station = qurayyat_id
                      AND arrival_station = riyadh_id
                      AND departure_time = TIMESTAMP(d, '20:00:00')
                );
            END IF;
        END IF;
        SET d = DATE_ADD(d, INTERVAL 1 DAY);
    END WHILE;
END$$
DELIMITER ;

CALL add_sar_cargo_trips();
DROP PROCEDURE IF EXISTS add_sar_cargo_trips;

-- Verify only cargo-train trips for these two routes, grouped by departure month.
SELECT DATE_FORMAT(t.departure_time, '%Y-%m') AS trip_month,
       CONCAT(s1.city, ' -> ', s2.city) AS route,
       COUNT(*) AS total_trips
FROM trips t
JOIN trains tr ON tr.train_id = t.train_id
JOIN stations s1 ON s1.station_id = t.departure_station
JOIN stations s2 ON s2.station_id = t.arrival_station
WHERE tr.train_type = 'Cargo'
  AND s1.station_name IN ('Riyadh Station', 'Qurayyat Station')
  AND s2.station_name IN ('Riyadh Station', 'Qurayyat Station')
  AND s1.station_id <> s2.station_id
  AND t.departure_time >= '2026-11-06'
  AND t.departure_time < '2027-02-01'
GROUP BY DATE_FORMAT(t.departure_time, '%Y-%m'), s1.city, s2.city
ORDER BY trip_month, route;
