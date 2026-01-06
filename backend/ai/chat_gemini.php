<?php
header("Content-Type: application/json; charset=UTF-8");

require_once '../db_connect.php'; 

// 1. Get User Input
$data = json_decode(file_get_contents("php://input"));

if (!isset($data->user_id) || !isset($data->message)) {
    echo json_encode(["ok" => false, "error" => "Missing user_id or message"]);
    exit();
}

$user_id = $data->user_id;
$user_message = trim($data->message);

// 2. Gemini API Key (Get from aistudio.google.com)
$api_key = "AIzaSyArXzPDhbIigpK65za4S5HpEiDxc16ooME"; 

if ($api_key === "YOUR_GEMINI_API_KEY") {
    echo json_encode([
        "ok" => true,
        "message" => "Please configure your Gemini API Key in backend/ai/chat_gemini.php."
    ]);
    exit();
}

// 3. Call Google Gemini API
$url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" . $api_key;

$payload = [
    "contents" => [
        [
            "parts" => [
                ["text" => $user_message]
            ]
        ]
    ]
];

$ch = curl_init($url);
curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type: application/json"]);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

// 4. Parse Response
if ($http_code === 200) {
    $result = json_decode($response, true);
    $ai_reply = $result['candidates'][0]['content']['parts'][0]['text'] ?? "I couldn't generate a response.";
    
    echo json_encode([
        "ok" => true,
        "message" => $ai_reply
    ]);
} else {
    echo json_encode([
        "ok" => true, 
        "message" => "Gemini is napping. (Error: $http_code)"
    ]);
}
?>
