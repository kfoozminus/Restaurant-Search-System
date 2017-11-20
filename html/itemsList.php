<?php

  include 'login.php';

  if(isset($_POST['trade_license_no']))
  {
	$trade_license_no = (int)$_POST['trade_license_no'];
		  
        $sql = "SELECT NAME FROM Items WHERE TRADE_LICENSE_NO = '$trade_license_no'";
	$res = mysqli_query($con,$sql);
 
 	$result = array();
 
  	while($row = mysqli_fetch_array($res)) {
  		array_push($result, array('NAME'=>$row[0]));
  	}
 
  	echo json_encode(array("result"=>$result));
	
  }

  mysqli_close($con);
?>
