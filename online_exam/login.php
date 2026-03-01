<?php
session_start();
include "db.php";

if(isset($_POST['login'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];

    $result = $conn->query("SELECT * FROM students 
                            WHERE email='$email' AND password='$password'");

    if($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $_SESSION['student_id'] = $row['id'];
        header("Location: exam.php");
    } else {
        echo "Invalid Login!";
    }
}
?>

<form method="post">
Email: <input type="email" name="email"><br>
Password: <input type="password" name="password"><br>
<input type="submit" name="login" value="Login">
</form>