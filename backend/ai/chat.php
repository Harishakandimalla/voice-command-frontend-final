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

// 2. Gemini API Key
$api_key = "AIzaSyArXzPDhbIigpK65za4S5HpEiDxc16ooME"; 

// 3. Construct Smart Prompt
// We tell Gemini to act as a Parser if an action is requested.
$system_instruction = "You are a smart Personal Assistant. 
1. If the user wants to create a task, reminder, or to-do, reply strictly in this format: ACTION:CREATE_TASK|Title|Date|Time
2. If the user wants to schedule a meeting or event, reply strictly: ACTION:CREATE_TASK|Meeting: Title|Date|Time
   - Use 'YYYY-MM-DD' for date (or 'TBD' if not specified).
   - Use 'HH:MM' for time (or 'TBD').
   - Infer the date/time relative to now if possible.
3. If it is a general question, just answer it helpfully and concisely.";

// 4. Call Google Gemini API
$url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" . $api_key;

$payload = [
    "contents" => [
        [
            "parts" => [
                ["text" => $system_instruction . "\n\nUser: " . $user_message]
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
curl_close($ch);

// 5. Parse Response from Gemini
$ai_reply = "I couldn't process that.";
$result = json_decode($response, true);

if (isset($result['candidates'][0]['content']['parts'][0]['text'])) {
    $ai_reply = trim($result['candidates'][0]['content']['parts'][0]['text']);
    
    // 6. Check for ACTION Command
    if (strpos($ai_reply, 'ACTION:CREATE_TASK') === 0) {
        // Format: ACTION:CREATE_TASK|Title|Date|Time
        $parts = explode('|', $ai_reply);
        $title = isset($parts[1]) ? trim($parts[1]) : 'New Task';
        $date = isset($parts[2]) ? trim($parts[2]) : '';
        $time = isset($parts[3]) ? trim($parts[3]) : '';
        
        // Clean up TBD
        if ($date == 'TBD') $date = '';
        if ($time == 'TBD') $time = '';

        // INSERT INTO DATABASE
        $stmt = $conn->prepare("INSERT INTO tasks (user_id, title, task_date, task_time, status) VALUES (?, ?, ?, ?, 'PENDING')");
        $stmt->bind_param("isss", $user_id, $title, $date, $time);
        
        if ($stmt->execute()) {
            $ai_reply = "✅ I've created the task \"$title\"";
            if ($date) $ai_reply .= " for $date";
            if ($time) $ai_reply .= " at $time";
            $ai_reply .= ".";
        } else {
            $ai_reply = "I tried to create the task, but a database error occurred.";
        }
        $stmt->close();
    }
} else {
    // If Gemini fails, fallback
    $ai_reply = "I'm having trouble connecting to my brain right now.";
}

// 7. Return Final Response to App
echo json_encode([
    "ok" => true,
    "message" => $ai_reply
]);
?>
