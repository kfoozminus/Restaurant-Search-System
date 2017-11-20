<?php

  include 'login.php';

  if(isset($_POST['trade_license_no']) && isset($_POST['totalPrice']))
  {
	$trade_license_no = (int)$_POST['trade_license_no'];
	$totalPrice = (int)$_POST['totalPrice'];
		  
	$sql = "INSERT INTO Orders(TRADE_LICENSE_NO, TOTAL_PRICE) VALUES('$trade_license_no', '$totalPrice')";
	$res = mysqli_query($con,$sql);
	
	echo mysqli_insert_id($con);
 	
  }

mysqli_close($con);
?>
