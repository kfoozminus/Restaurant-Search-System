<?php

  include 'login.php';

  if(isset($_POST['orderId']) && isset($_POST['tradeString']) && isset($_POST['itemName']) && isset($_POST['itemQuan']))
  {
	$orderId = (int)$_POST['orderId'];
	$itemQuan = (int)$_POST['itemQuan'];
	$itemName = $_POST['itemName'];
	$tradeString = $_POST['tradeString'];
		  
	$sql = "INSERT INTO Orders_Contains_Items(ORDER_ID, NAME, TRADE_LICENSE_NO, QUANTITY) VALUES('$orderId', '$itemName', '$tradeString', '$itemQuan')";
	$res = mysqli_query($con,$sql);
	
	if($res) echo "Success";
	else "Failure";
 	
  }

mysqli_close($con);
?>
