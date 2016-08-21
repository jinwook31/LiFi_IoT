<?php
	$connect = mysql_connect("localhost", "root", "1541") or die("Cannot connect SQL");
	mysql_select_db("LoT_control", $connect);

	$keyID = $_POST['KeyID'];
	$Name = $_POST['Name'];

	if($keyID == "Directory"){
		$currentSql = "delete from controlList where KeyID='$keyID' and Name='$Name'";
		$prevSql = "delete from prev_controlList where KeyID='$keyID' and Name='$Name'";
		$prevResult = mysql_query($prevSql, $connect);
		$result = mysql_query($currentSql, $connect);
	}


	else{
		$currentSql = "delete from controlList where KeyID = '$keyID'";
		$prevSql = "delete from prev_controlList where KeyID = '$keyID'";
		$prevResult = mysql_query($prevSql, $connect);
		$result = mysql_query($currentSql, $connect);
	}

	if($result && $prevResult){
		echo 'Delete Successful';
	}
	else{
		echo 'Delete Fail';
	}

?>
