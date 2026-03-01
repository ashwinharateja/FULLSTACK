<?php
session_start();
include "db.php";

$score = 0;
$student_id = $_SESSION['student_id'];

$result = $conn->query("SELECT * FROM questions");

while($row = $result->fetch_assoc()) {
    $qid = $row['id'];
    $correct = $row['correct_option'];

    if(isset($_POST['q'.$qid])) {
        if($_POST['q'.$qid] == $correct) {
            $score++;
        }
    }
}

$conn->query("INSERT INTO results(student_id, score) 
              VALUES('$student_id', '$score')");

echo "Your Score: " . $score;
?>