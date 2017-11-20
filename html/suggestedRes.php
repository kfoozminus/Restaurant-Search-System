<?php

  include 'login.php';

  if(isset($_POST['latitude']) && isset($_POST['longitude'])) {

	  $latitude = (float)$_POST['latitude'];
	  $longitude = (float)$_POST['longitude'];
	  $latitude = deg2rad($latitude);
	  $longitude = deg2rad($longitude);
	  $earthRadius = (float)6371000;

	  $sql = "SELECT Restaurants.NAME, Restaurant_Locations.ADDRESS, Restaurants.TRADE_LICENSE_NO
		  FROM Restaurants
		  INNER JOIN Restaurant_Locations
		  ON Restaurants.TRADE_LICENSE_NO = Restaurant_Locations.TRADE_LICENSE_NO
		  ORDER BY ((ATAN2(SQRT(POWER(COS(RADIANS(Restaurant_Locations.LATITUDE)) * SIN(RADIANS(Restaurant_Locations.LONGITUDE) - '$longitude'), 2) + POWER(COS('$latitude') * SIN(RADIANS(Restaurant_Locations.LATITUDE)) - SIN('$latitude') * COS(RADIANS(Restaurant_Locations.LATITUDE)) * COS(RADIANS(Restaurant_Locations.LONGITUDE) - '$longitude'), 2)), (SIN('$latitude') * SIN(RADIANS(Restaurant_Locations.LATITUDE)) + COS('$latitude') + COS(RADIANS(Restaurant_Locations.LATITUDE)) * COS(RADIANS(Restaurant_Locations.LONGITUDE) - '$longitude'))) * '$earthRadius') / (Restaurants.RATING * Restaurants.NO_OF_USERS_RATED))";
	 
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
  }

  mysqli_close($con);
?>
