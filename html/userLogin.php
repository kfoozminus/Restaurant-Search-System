<?php

  include 'login.php';

  if(isset($_POST['username']) && isset($_POST['password']))
  {
	$username = $_POST['username'];
	$password = $_POST['password'];
		  
	//$sql = "SELECT AMOUNT FROM Cards WHERE CARD_NO = '01234567891' AND PIN = '1234'";
	$sql = "SELECT * FROM Users WHERE LOGIN_ID = '$username' AND PASSWORD = '$password' AND USER_TYPE = 1";
	$res = mysqli_query($con,$sql);

	if(mysqli_num_rows($res) > 0) {
		echo "Success";
	}
	else echo "Failure";
  }

mysqli_close($con);
?>
