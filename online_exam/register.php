<?php include "db.php"; ?>

<form method="post">
Name: <input type="text" name="name"><br>
Email: <input type="email" name="email"><br>
Password: <input type="password" name="password"><br>
<input type="submit" name="register" value="Register">
</form>

<?php
if(isset($_POST['register'])) {
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];

    $conn->query("INSERT INTO students(name,email,password) 
                  VALUES('$name','$email','$password')");
    echo "Registered Successfully!";
}
?>