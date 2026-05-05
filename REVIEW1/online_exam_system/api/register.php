<?php
include("../config/db.php");

// Try JSON first
$data = json_decode(file_get_contents("php://input"), true);

// If JSON empty, try normal POST
if(!$data){
    $data = $_POST;
}

if(empty($data['name']) || empty($data['email']) || empty($data['password'])){
    echo json_encode(["status"=>"error","message"=>"No Data"]);
    exit();
}

$name = $data['name'];
$email = $data['email'];
$password = password_hash($data['password'], PASSWORD_DEFAULT);

$sql = "INSERT INTO students (name,email,password)
        VALUES ('$name','$email','$password')";

if($conn->query($sql)){
    echo json_encode(["status"=>"success"]);
}else{
    echo json_encode(["status"=>"error","message"=>$conn->error]);
}
?>