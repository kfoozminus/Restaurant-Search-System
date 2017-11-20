<?php

  include 'login.php';

  if(isset($_POST['card']) && isset($_POST['amount']))
  {
	$card = $_POST['card'];
	$amount = (int)$_POST['amount'];
		  
	$sql = "UPDATE Cards SET AMOUNT = AMOUNT - '$amount' WHERE CARD_NO = '$card'";
	$res = mysqli_query($con,$sql);
 	
  }

mysqli_close($con);
?>
