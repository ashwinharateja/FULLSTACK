<?php

$conn = new mysqli("localhost","root","","studenr_db");

if($conn->connect_error)
{
die("Connection failed");
}


$department = "";
$sort = "";

$sql = "SELECT * FROM students";


if(isset($_GET['department']) && $_GET['department'] != "")
{
$department = $_GET['department'];
$sql .= " WHERE department='$department'";
}


if(isset($_GET['sort']))
{

if($_GET['sort']=="name")
{
$sql .= " ORDER BY name ASC";
}

elseif($_GET['sort']=="dob")
{
$sql .= " ORDER BY dob ASC";
}

}


$result = $conn->query($sql);

?>


<html>

<head>

<title>Student Dashboard</title>

<link rel="stylesheet" href="style.css">

</head>

<body>


<h2>Student Dashboard</h2>


<form method="GET">

Filter Department:

<select name="department">

<option value="">All</option>
<option value="CSE">CSE</option>
<option value="IT">IT</option>
<option value="ECE">ECE</option>

</select>


Sort By:

<select name="sort">

<option value="">None</option>
<option value="name">Name</option>
<option value="dob">DOB</option>

</select>


<input type="submit" value="Apply">

</form>



<table border="1">

<tr>

<th>Name</th>
<th>Email</th>
<th>DOB</th>
<th>Department</th>
<th>Phone</th>

</tr>


<?php

while($row=$result->fetch_assoc())
{

echo "<tr>";

echo "<td>".$row['name']."</td>";
echo "<td>".$row['email']."</td>";
echo "<td>".$row['dob']."</td>";
echo "<td>".$row['department']."</td>";
echo "<td>".$row['phone']."</td>";

echo "</tr>";

}

?>


</table>



<h3>Student Count per Department</h3>


<?php

$count = $conn->query("SELECT department, COUNT(*) as total FROM students GROUP BY department");

while($row=$count->fetch_assoc())
{

echo $row['department']." : ".$row['total']."<br>";

}

?>


</body>

</html>