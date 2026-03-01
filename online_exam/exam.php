<?php
session_start();
include "db.php";

$result = $conn->query("SELECT * FROM questions");
?>

<form action="submit_exam.php" method="post">

<?php while($row = $result->fetch_assoc()) { ?>

<p><?php echo $row['question']; ?></p>

<input type="radio" name="q<?php echo $row['id']; ?>" value="option1">
<?php echo $row['option1']; ?><br>

<input type="radio" name="q<?php echo $row['id']; ?>" value="option2">
<?php echo $row['option2']; ?><br>

<input type="radio" name="q<?php echo $row['id']; ?>" value="option3">
<?php echo $row['option3']; ?><br>

<input type="radio" name="q<?php echo $row['id']; ?>" value="option4">
<?php echo $row['option4']; ?><br>

<?php } ?>

<input type="submit" value="Submit Exam">
</form>