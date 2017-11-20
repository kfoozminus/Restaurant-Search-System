<?php

  include 'login.php';

  if(isset($_POST['card']) && isset($_POST['pin']))
  {
	$card = $_POST['card'];
	$pin = $_POST['pin'];
		  
	//$sql = "SELECT AMOUNT FROM Cards WHERE CARD_NO = '01234567891' AND PIN = '1234'";
	$sql = "SELECT AMOUNT FROM Cards WHERE CARD_NO = '$card' AND PIN = '$pin'";
	$res = mysqli_query($con,$sql);

	if(mysqli_num_rows($res) > 0) {
		$row = mysqli_fetch_array($res);
		echo $row[0];
	}
	else echo "failure";
 	
  }

mysqli_close($con);
?>
