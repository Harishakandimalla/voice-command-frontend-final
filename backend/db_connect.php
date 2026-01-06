<?php
$servername = "localhost";
$username = "root"; // Default XAMPP username
$password = "";     // Default XAMPP password (empty)
$dbname = "voice_command_db"; // YOUR DATABASE NAME

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
// Check connection
if ($conn->connect_error) {
    // Return JSON error response so the app doesn't crash with MalformedJsonException
    $response = array();
    $response['ok'] = false;
    $response['message'] = "Database connection failed: " . $conn->connect_error;
    echo json_encode($response);
    exit();
}
