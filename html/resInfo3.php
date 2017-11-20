<?php

  include 'login.php';

  if(isset($_POST['trade_license_no']) && isset($_POST['itemName']))
  {
	$trade_license_no = (int)$_POST['trade_license_no'];
	$itemName = $_POST['itemName'];
		  
        $sql = "SELECT IMAGE, PRICE, RATING FROM Items WHERE TRADE_LICENSE_NO = '$trade_license_no' AND NAME = '$itemName'";
	$res = mysqli_query($con,$sql);
 
 	$result = array();
 
  	while($row = mysqli_fetch_array($res)) {
  		array_push($result,
		array('IMAGE'=>$row[0]
		, 'PRICE'=>$row[1]
		, 'RATING'=>$row[2]
	));
  	}
 
  	echo json_encode(array("result"=>$result));
	
  }

  mysqli_close($con);
?>
