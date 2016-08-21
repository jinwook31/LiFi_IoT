<?php
	$connect = mysql_connect("localhost", "root", "1541") or die("Cannot connect SQL");

	mysql_select_db("LoT_control");

	$db_list = "select * from controlList";
	$result = mysql_query($db_list, $connect);
	$temp = array();



	while($row = mysql_fetch_array($result)){
		array_push($temp, array('Name'=>$row[1], 'KeyID'=>$row[2], 'onoffState'=>$row[3], 'Location'=>$row[4]));
	}

	echo json_encode(array("result"=>$temp));
?>
