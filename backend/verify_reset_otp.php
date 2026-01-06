<?php
// Disable error reporting
error_reporting(0);
ini_set('display_errors', 0);

// Start output buffering
ob_start();

header("Content-Type: application/json; charset=UTF-8");
require_once 'db_connect.php';

/* ========================= PHPMailer setup ========================= */
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;
require 'phpmailer/PHPMailer.php';
require 'phpmailer/SMTP.php';
require 'phpmailer/Exception.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents("php://input"));

    if (isset($data->email) && isset($data->otp)) {
        $email = $conn->real_escape_string(trim($data->email));
        $otp = trim($data->otp);

        $stmt = $conn->prepare("SELECT otp, otp_expiry FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $db_otp = $row['otp'];
            $db_expiry = $row['otp_expiry'];

            if ($db_otp === $otp) {
                if (strtotime($db_expiry) > time()) {
                    $response['ok'] = true;
                    $response['message'] = "OTP verified.";
                } else {
                    $response['ok'] = false;
                    $response['message'] = "OTP expired.";
                }
            } else {
                $response['ok'] = false;
                $response['message'] = "Invalid OTP.";
            }
        } else {
            $response['ok'] = false;
            $response['message'] = "User not found.";
        }
        $stmt->close();
    } else {
        $response['ok'] = false;
        $response['message'] = "Email and OTP required.";
    }
} else {
    $response['ok'] = false;
    $response['message'] = "Invalid request method";
}

// Clean output buffer
ob_clean();
echo json_encode($response);
