DROP TABLE driver;
CREATE TABLE driver( driver_id VARCHAR(50) PRIMARY KEY,
driver_name VARCHAR(100) NOT NULL,
vehicle_no VARCHAR(100) NOT NULL,
verified_driver BOOLEAN NOT NULL,
driver_location VARCHAR(100),
vehicle_type VARCHAR(50),
fuel_efficiency DOUBLE,
trust_score DOUBLE,
total_co2_saved DOUBLE);

SHOW VARIABLES LIKE 'secure_file_priv';

LOAD DATA INFILE '/ProgramData/MySQL/MySQL Server 9.2/Uploads/driver_sample_data_fixed.csv'
INTO TABLE driver
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;

