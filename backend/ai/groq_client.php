<?php

class GroqClient {
    private $apiKey;
    private $model;
    private $baseUrl = 'https://api.groq.com/openai/v1/chat/completions';

    public function __construct() {
        $config = require 'groq_config.php';
        $this->apiKey = $config['api_key'];
        $this->model = $config['model'];
    }

    public function sendMessage($systemInstruction, $userMessage) {
        $headers = [
            'Authorization: Bearer ' . $this->apiKey,
            'Content-Type: application/json'
        ];

        $data = [
            'model' => $this->model,
            'messages' => [
                ['role' => 'system', 'content' => $systemInstruction],
                ['role' => 'user', 'content' => $userMessage]
            ],
            'temperature' => 0.7
        ];

        $ch = curl_init($this->baseUrl);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        if ($httpCode === 200) {
            $result = json_decode($response, true);
            return $result['choices'][0]['message']['content'] ?? null;
        }

        return null;
    }
}
?>
