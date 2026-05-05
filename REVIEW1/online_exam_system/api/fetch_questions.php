<?php
include("../config/db.php");

$data = json_decode(file_get_contents("php://input"), true);

$email = $data['email'];
$password = $data['password'];

$result = $conn->query("SELECT * FROM students WHERE email='$email'");
$user = $result->fetch_assoc();

if($user && password_verify($password, $user['password'])){
    echo json_encode(["status"=>"success","id"=>$user['id']]);
}else{
    echo json_encode(["status"=>"error"]);
}
?>