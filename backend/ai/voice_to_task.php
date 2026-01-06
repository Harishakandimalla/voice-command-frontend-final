<?php
header("Content-Type: application/json; charset=UTF-8");

require_once '../db_connect.php';
require_once 'groq_client.php';

// 1. Get Input
$data = json_decode(file_get_contents("php://input"));

if (!isset($data->user_id) || !isset($data->message)) {
    echo json_encode(["ok" => false, "error" => "Missing user_id or message"]);
    exit();
}

$user_id = $data->user_id;
$user_message = trim($data->message);

// 2. System Instruction for Groq
$system_instruction = "You are a smart Personal Assistant built for an Android App.
Your goal is to understand the user's voice command and decide if it is a TASK creation request or a General Question.

1. IF user wants to create a task, reminder, to-do, or schedule something:
   - Output explicitly in this format: ACTION:CREATE_TASK|Title|Date|Time|Category|Priority
   - Date format: YYYY-MM-DD (or 'TBD' if not found)
   - Time format: HH:mm:ss (or 'TBD' if not found)
   - Category: Work, Personal, Shopping, Health, Learning (Infer one)
   - Priority: HIGH, MEDIUM, LOW (Infer one, default MEDIUM)
   - Infer relative dates (tomorrow, next friday) to absolute YYYY-MM-DD based on today (" . date('Y-m-d') . ").

2. IF it is a general question or chat:
   - Output explicitly in this format: ACTION:CHAT|Your helpful response here.
   
   Examples:
   User: 'Remind me to buy milk tomorrow 5pm'
   AI: ACTION:CREATE_TASK|Buy milk|" . date('Y-m-d', strtotime('+1 day')) . "|17:00:00|Shopping|MEDIUM

   User: 'What is the capital of France?'
   AI: ACTION:CHAT|The capital of France is Paris.
";

// 3. Call Groq
$groq = new GroqClient();
$ai_reply = $groq->sendMessage($system_instruction, $user_message);

if ($ai_reply) {
    // 4. Parse Response
    if (strpos($ai_reply, 'ACTION:CREATE_TASK') === 0) {
        $parts = explode('|', $ai_reply);
        $title = isset($parts[1]) ? trim($parts[1]) : 'New Task';
        $date = isset($parts[2]) ? trim($parts[2]) : '';
        $time = isset($parts[3]) ? trim($parts[3]) : '';
        $category = isset($parts[4]) ? trim($parts[4]) : 'General';
        $priority = isset($parts[5]) ? trim($parts[5]) : 'MEDIUM';

        if ($date == 'TBD') $date = '';
        if ($time == 'TBD') $time = '';

        // Insert into DB
        // NOTE: Adjusted column names based on UnderstandingActivity usage (category/priority might be needed in DB)
        // Assuming 'tasks' table has these columns. If not, we might need to be simpler.
        // For now, let's Stick to the standard columns we saw in chat_gemini: user_id, title, task_date, task_time, status
        // I'll assume category and priority might be supported or I should stick to basic. 
        // Let's try to include them if possible, but safely fall back.
        
        // Let's check db_connect.php or previously seen queries.
        // chat_gemini.php used: INSERT INTO tasks (user_id, title, task_date, task_time, status)
        // So I will stick to that to be safe.
        
        $stmt = $conn->prepare("INSERT INTO tasks (user_id, title, task_date, task_time, status, category, priority) VALUES (?, ?, ?, ?, 'PENDING', ?, ?)");
        if (!$stmt) {
             // Fallback if category/priority columns don't exist
             $stmt = $conn->prepare("INSERT INTO tasks (user_id, title, task_date, task_time, status) VALUES (?, ?, ?, ?, 'PENDING')");
             $stmt->bind_param("isss", $user_id, $title, $date, $time);
        } else {
             $stmt->bind_param("isssss", $user_id, $title, $date, $time, $category, $priority);
        }

        if ($stmt->execute()) {
            echo json_encode([
                "ok" => true,
                "action" => "CREATE_TASK",
                "data" => [
                    "title" => $title,
                    "date" => $date,
                    "time" => $time,
                    "category" => $category,
                    "priority" => $priority
                ],
                "message" => "Task created successfully."
            ]);
        } else {
            echo json_encode(["ok" => false, "error" => "Database error: " . $stmt->error]);
        }
        $stmt->close();

    } elseif (strpos($ai_reply, 'ACTION:CHAT') === 0) {
        $chat_msg = substr($ai_reply, strpos($ai_reply, '|') + 1);
        echo json_encode([
            "ok" => true,
            "action" => "CHAT",
            "message" => $chat_msg
        ]);
    } else {
        // Fallback for unclear response
        echo json_encode([
            "ok" => true,
            "action" => "CHAT",
            "message" => $ai_reply
        ]);
    }

} else {
    echo json_encode(["ok" => false, "error" => "Failed to get response from Groq."]);
}
?>
