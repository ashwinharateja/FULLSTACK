<?php
include("../config/db.php");

$data = json_decode(file_get_contents("php://input"), true);

$answers = $data['answers'];
$student_id = $data['student_id'];

$score = 0;
$total = 0;

$result = $conn->query("SELECT * FROM questions");

while($row = $result->fetch_assoc()){
    $qid = $row['id'];
    if(isset($answers[$qid])){
        $total++;
        if($answers[$qid] == $row['correct_option']){
            $score++;
        }
    }
}

$conn->query("INSERT INTO results (student_id,score,total)
VALUES ('$student_id','$score','$total')");

echo json_encode(["score"=>$score,"total"=>$total]);
?>