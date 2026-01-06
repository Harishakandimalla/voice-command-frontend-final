<?php
// Enable error reporting for debug
error_reporting(E_ALL);
ini_set('display_errors', 1);

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

    if (isset($data->email)) {
        $email = $conn->real_escape_string(trim($data->email));

        // Check if email exists
        $checkStmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
        $checkStmt->bind_param("s", $email);
        $checkStmt->execute();
        $checkResult = $checkStmt->get_result();

        if ($checkResult->num_rows > 0) {
            $otp = rand(100000, 999999);
            // Expiry 10 minutes from now
            $expiry = date("Y-m-d H:i:s", strtotime("+10 minutes"));

            $updateStmt = $conn->prepare("UPDATE users SET otp = ?, otp_expiry = ? WHERE email = ?");
            $updateStmt->bind_param("sss", $otp, $expiry, $email);

            if ($updateStmt->execute()) {
                // Send email
                $mail = new PHPMailer(true);

                try {
                     //Server settings
                    $mail->SMTPDebug = 0;                      
                    $mail->isSMTP();                                            
                    $mail->Host       = 'smtp.gmail.com';                     
                    $mail->SMTPAuth   = true;                                   
                    $mail->Username   = 'your_email@gmail.com';                     
                    $mail->Password   = 'your_app_password';                               
                    $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS;            
                    $mail->Port       = 465;                                    

                    //Recipients
                    $mail->setFrom('your_email@gmail.com', 'VoiceCommand App');
                    $mail->addAddress($email);     

                    //Content
                    $mail->isHTML(true);                                  
                    $mail->Subject = "Reset Password OTP";
                    $mail->Body    = "Your OTP for password reset is: " . $otp;

                    // $mail->send();
                    $response['ok'] = true;
                    $response['message'] = "OTP sent to email. (Debug: $otp) [Email simulation]"; 
                } catch (Exception $e) {
                     $response['ok'] = true; // Allow success for debugging
                     $response['message'] = "OTP generated! Email failed: {$mail->ErrorInfo}. Debug OTP: " . $otp;
                } 
            } else {
                $response['ok'] = false;
                $response['message'] = "Failed to generate OTP.";
            }
            $updateStmt->close();
        } else {
            $response['ok'] = false;
            $response['message'] = "Email not registered.";
        }
        $checkStmt->close();
    } else {
        $response['ok'] = false;
        $response['message'] = "Email required.";
    }
} else {
    $response['ok'] = false;
    $response['message'] = "Invalid request method";
}

// Clean output buffer
// ob_clean();
echo json_encode($response);
