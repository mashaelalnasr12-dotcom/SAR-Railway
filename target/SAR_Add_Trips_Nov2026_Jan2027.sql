-- SAR Railway Management System
-- Add demo trips from 5 Nov 2026 through 31 Jan 2027.
-- Two trips per day; four on Friday and Saturday.
-- Safe to run more than once: matching trips will not be duplicated.
-- Assumes train IDs 1-3 and station IDs 1-5 from your setup script.

USE railway_db;

DROP PROCEDURE IF EXISTS add_sar_demo_trips;
DELIMITER $$

CREATE PROCEDURE add_sar_demo_trips()
BEGIN
    DECLARE trip_day DATE DEFAULT '2026-11-05';
    DECLARE last_day DATE DEFAULT '2027-01-31';

    WHILE trip_day <= last_day DO
        -- Don't create trips dated before the day this script is executed.
        IF trip_day >= CURDATE() THEN
            -- Daily: Riyadh (1) -> Dammam (2), train 1, 08:00-12:00.
            INSERT INTO trips
                (train_id, departure_station, arrival_station,
                 departure_time, arrival_time, price, status)
            SELECT 1, 1, 2,
                   TIMESTAMP(trip_day, '08:00:00'),
                   TIMESTAMP(trip_day, '12:00:00'),
                   120.00, 'Available'
            WHERE NOT EXISTS (
                SELECT 1 FROM trips
                WHERE train_id = 1
                  AND departure_station = 1
                  AND arrival_station = 2
                  AND departure_time = TIMESTAMP(trip_day, '08:00:00')
            );

            -- Daily: Dammam (2) -> Riyadh (1), train 2, 17:00-21:00.
            INSERT INTO trips
                (train_id, departure_station, arrival_station,
                 departure_time, arrival_time, price, status)
            SELECT 2, 2, 1,
                   TIMESTAMP(trip_day, '17:00:00'),
                   TIMESTAMP(trip_day, '21:00:00'),
                   120.00, 'Available'
            WHERE NOT EXISTS (
                SELECT 1 FROM trips
                WHERE train_id = 2
                  AND departure_station = 2
                  AND arrival_station = 1
                  AND departure_time = TIMESTAMP(trip_day, '17:00:00')
            );

            -- Fridays and Saturdays: 2 extra trips.
            -- DAYOFWEEK: Friday = 6, Saturday = 7 (MySQL).
            IF DAYOFWEEK(trip_day) IN (6, 7) THEN
                -- Jeddah (3) -> Makkah (4), train 3, 10:00-11:30.
                INSERT INTO trips
                    (train_id, departure_station, arrival_station,
                     departure_time, arrival_time, price, status)
                SELECT 3, 3, 4,
                       TIMESTAMP(trip_day, '10:00:00'),
                       TIMESTAMP(trip_day, '11:30:00'),
                       80.00, 'Available'
                WHERE NOT EXISTS (
                    SELECT 1 FROM trips
                    WHERE train_id = 3
                      AND departure_station = 3
                      AND arrival_station = 4
                      AND departure_time = TIMESTAMP(trip_day, '10:00:00')
                );

                -- Makkah (4) -> Madinah (5), train 3, 15:00-17:00.
                INSERT INTO trips
                    (train_id, departure_station, arrival_station,
                     departure_time, arrival_time, price, status)
                SELECT 3, 4, 5,
                       TIMESTAMP(trip_day, '15:00:00'),
                       TIMESTAMP(trip_day, '17:00:00'),
                       95.00, 'Available'
                WHERE NOT EXISTS (
                    SELECT 1 FROM trips
                    WHERE train_id = 3
                      AND departure_station = 4
                      AND arrival_station = 5
                      AND departure_time = TIMESTAMP(trip_day, '15:00:00')
                );
            END IF;
        END IF;

        SET trip_day = DATE_ADD(trip_day, INTERVAL 1 DAY);
    END WHILE;
END$$

DELIMITER ;
CALL add_sar_demo_trips();
DROP PROCEDURE IF EXISTS add_sar_demo_trips;

-- Confirm the number of generated/upcoming trips, grouped by month:
SELECT DATE_FORMAT(departure_time, '%Y-%m') AS trip_month,
       COUNT(*) AS total_trips
FROM trips
WHERE departure_time >= GREATEST(CURDATE(), '2026-11-05')
  AND departure_time < '2027-02-01'
GROUP BY DATE_FORMAT(departure_time, '%Y-%m')
ORDER BY trip_month;
