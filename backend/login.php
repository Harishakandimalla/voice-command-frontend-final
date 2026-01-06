<?php
// Disable error reporting
error_reporting(0);
ini_set('display_errors', 0);

// Start output buffering
ob_start();

header("Content-Type: application/json; charset=UTF-8");
require_once 'db_connect.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents("php://input"));

    if (isset($data->email) && isset($data->password)) {
        $email = $conn->real_escape_string(trim($data->email));
        $password = trim($data->password);

        $stmt = $conn->prepare("SELECT id, name, password FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $hashed_password = $row['password'];

            if (password_verify($password, $hashed_password)) {
                $response['ok'] = true;
                $response['user_id'] = $row['id'];
                $response['user_name'] = $row['name'];
                $response['message'] = "Login successful";
            } else {
                $response['ok'] = false;
                $response['message'] = "Invalid email or password";
            }
        } else {
            $response['ok'] = false;
            $response['message'] = "Invalid email or password";
        }
        $stmt->close();
    } else {
        $response['ok'] = false;
        $response['message'] = "Required parameters missing";
    }
} else {
    $response['ok'] = false;
    $response['message'] = "Invalid request method";
}

// Clean output buffer
ob_clean();
echo json_encode($response);
