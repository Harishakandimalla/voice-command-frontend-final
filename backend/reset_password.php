<?php
// Disable error reporting for production, or use specific level for dev
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

    if (isset($data->email) && isset($data->new_password)) {
        $email = $conn->real_escape_string(trim($data->email));
        $new_password = password_hash(trim($data->new_password), PASSWORD_DEFAULT);

        $stmt = $conn->prepare("UPDATE users SET password = ?, otp = NULL, otp_expiry = NULL WHERE email = ?");
        $stmt->bind_param("ss", $new_password, $email);

        if ($stmt->execute()) {
             // Send confirmation email
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
                $mail->Subject = "Password Changed Successfully";
                $mail->Body    = "Your password for VoiceCommand App has been changed successfully. If you did not make this change, please contact support immediately.";

                $mail->send();
             } catch (Exception $e) {
                 // Log error or ignore, but don't fail the password reset response
             }

            $response['ok'] = true;
            $response['message'] = "Password reset successfully.";
        } else {
            $response['ok'] = false;
            $response['message'] = "Failed to reset password: " . $stmt->error;
        }
        $stmt->close();
    } else {
        $response['ok'] = false;
        $response['message'] = "Email and new password required.";
    }
} else {
    $response['ok'] = false;
    $response['message'] = "Invalid request method";
}

// Clean output buffer
ob_clean();
echo json_encode($response);
