<?php

  include 'login.php';

  if(isset($_POST['username']) && isset($_POST['password']))
  {
	$username = $_POST['username'];
	$password = $_POST['password'];
		  
	//$sql = "SELECT AMOUNT FROM Cards WHERE CARD_NO = '01234567891' AND PIN = '1234'";
	$sql = "INSERT INTO Users(LOGIN_ID, PASSWORD, USER_TYPE) VALUES('$username', '$password', 1)";
	$res = mysqli_query($con,$sql);

	if($res) echo "Success";
	else echo "Failure";
  }

mysqli_close($con);
?>
