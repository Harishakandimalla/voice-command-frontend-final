<?php
require_once 'db_connect.php';

// Create users table if not exists (including otp columns)
$sql = "CREATE TABLE IF NOT EXISTS users (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    otp VARCHAR(10) DEFAULT NULL,
    otp_expiry DATETIME DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)";

if ($conn->query($sql) === TRUE) {
    echo "Table users created successfully or already exists.<br>";
} else {
    echo "Error creating table: " . $conn->error . "<br>";
}

// Check if otp column exists, if not add it
$check_col = $conn->query("SHOW COLUMNS FROM users LIKE 'otp'");
if ($check_col->num_rows == 0) {
    $conn->query("ALTER TABLE users ADD COLUMN otp VARCHAR(10) DEFAULT NULL");
    echo "Added otp column.<br>";
}

// Check if otp_expiry column exists, if not add it
$check_col = $conn->query("SHOW COLUMNS FROM users LIKE 'otp_expiry'");
if ($check_col->num_rows == 0) {
    $conn->query("ALTER TABLE users ADD COLUMN otp_expiry DATETIME DEFAULT NULL");
    echo "Added otp_expiry column.<br>";
}

echo "Database setup complete.";
?>
