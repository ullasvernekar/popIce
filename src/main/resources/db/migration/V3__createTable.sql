    CREATE TABLE cash_register (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_mode VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    created_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);