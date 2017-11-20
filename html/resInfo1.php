<?php

  include 'login.php';

  if(isset($_POST['tradeString']))
  {
	$tradeString = (int)$_POST['tradeString'];
		  
        $sql = "SELECT RATING, START_AT, END_AT, DELIVERY_FEE FROM Restaurants WHERE TRADE_LICENSE_NO = '$tradeString'";
	$res = mysqli_query($con,$sql);
 
 	$result = array();
 
  	while($row = mysqli_fetch_array($res)) {
  		array_push($result,
		array('RATING'=>$row[0]
		, 'START'=>$row[1]
		, 'END'=>$row[2]
		, 'DELIVERY_FEE'=>$row[3]
	));
  	}
 
  	echo json_encode(array("result"=>$result));
	
  }

  mysqli_close($con);
?>
