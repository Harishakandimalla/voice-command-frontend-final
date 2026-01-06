<?php
// Disable error reporting for production, or use specific level for dev
error_reporting(0);
ini_set('display_errors', 0);

// Start output buffering to catch any unwanted whitespace/warnings
ob_start();

header("Content-Type: application/json; charset=UTF-8");
require_once 'db_connect.php';



$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents("php://input"));

    if (isset($data->name) && isset($data->email) && isset($data->password)) {
        // No need for real_escape_string with prepared statements
        $name = trim($data->name);
        $email = trim($data->email);
        $password = password_hash(trim($data->password), PASSWORD_DEFAULT);

        // Check if email already exists
        $checkStmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
        $checkStmt->bind_param("s", $email);
        $checkStmt->execute();
        $checkResult = $checkStmt->get_result();

        if ($checkResult->num_rows > 0) {
            $response['ok'] = false;
            $response['message'] = "Email " . $email . " already exists";
        } else {
            // Insert new user DIRECTLY (No OTP)
            $stmt = $conn->prepare("INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
            $stmt->bind_param("sss", $name, $email, $password);

            if ($stmt->execute()) {
                $response['ok'] = true;
                $response['message'] = "Registration successful";
            } else {
                $response['ok'] = false;
                $response['message'] = "Registration failed: " . $stmt->error;
            }
            $stmt->close();
        }
        $checkStmt->close();
    } else {
        $response['ok'] = false;
        $response['message'] = "Required parameters missing";
    }
} else {
    $response['ok'] = false;
    $response['message'] = "Invalid request method";
}

// Clean the output buffer (discard everything before this point)
ob_clean();
echo json_encode($response);
