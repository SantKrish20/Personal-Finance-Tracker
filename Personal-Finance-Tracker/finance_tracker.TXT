CREATE DATABASE finance_tracker;
USE finance_tracker;
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_type ENUM('Income', 'Expense') NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT,
    transaction_date DATE NOT NULL
);