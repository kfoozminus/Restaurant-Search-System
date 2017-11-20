<?php

  include 'login.php';

  if(isset($_POST['username']))
  {
	$username = $_POST['username'];
		  
	//$sql = "SELECT AMOUNT FROM Cards WHERE CARD_NO = '01234567891' AND PIN = '1234'";
	$sql = "SELECT * FROM Users WHERE LOGIN_ID = '$username'";
	$res = mysqli_query($con,$sql);

	if(mysqli_num_rows($res) > 0) {
		echo "1";
	}
	else echo "0";
  }

mysqli_close($con);
?>
