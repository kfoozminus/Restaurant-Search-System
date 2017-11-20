<?php

  include 'login.php';

  $sql = "SELECT Restaurants.NAME, Restaurant_Locations.ADDRESS, Restaurants.TRADE_LICENSE_NO FROM Restaurants INNER JOIN Restaurant_Locations ON Restaurants.TRADE_LICENSE_NO = Restaurant_Locations.TRADE_LICENSE_NO ORDER BY Restaurants.NAME";
 
  $res = mysqli_query($con,$sql);
 
  $result = array();
 
  while($row = mysqli_fetch_array($res)){
  array_push($result,
  array('NAME'=>$row[0]
  , 'ADDRESS'=>$row[1]
  , 'TRADE_LICENSE_NO'=>$row[2]
  ));
  }
 
  echo json_encode(array("result"=>$result));

  mysqli_close($con);
?>
