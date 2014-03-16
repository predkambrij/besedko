<?php
$resp = exec("python ".getcwd()."/get_random_word.py ".$_GET["number_of_words"]);
$posts = array ("string" => $resp);
echo json_encode($posts);
?>
