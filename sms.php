<?php
echo "received\n";
$data = file_get_contents('php://input');
if (empty($data)) die();

function format_msg($data) {
    return "<b>" . htmlentities(trim($data['sender'])) . "</b>\n" . htmlentities(trim($data['msg']));
}

$data = json_decode(file_get_contents('php://input'), true);
$template = '';
if (count($data) > 0) {
    foreach ($data as $v) $template .= format_msg($v);
} else {
    $template = json_encode($data, JSON_PRETTY_PRINT);
}

$data = [
    'parse_mode' => 'HTML',
    'chat_id' => 1212121212,
    'text' => $template
];

$url = 'https://api.telegram.org/bot123123123:ABCDEF_ABCDEF_ABCDEF/sendMessage';
$ch = curl_init();
curl_setopt_array($ch, [
    CURLOPT_URL => $url,
    CURLOPT_POST => count($data),
    CURLOPT_POSTFIELDS => $data,
    CURLOPT_RETURNTRANSFER => true
]);
curl_exec($ch);
curl_close($ch);
