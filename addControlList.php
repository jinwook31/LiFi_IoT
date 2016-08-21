 <?php
	$connect = mysql_connect("localhost", "root", "1541") or die("Cannot connect SQL");
	mysql_select_db("LoT_control", $connect);

	$Name = $_POST['Name'];
	$keyID = $_POST['KeyID'];
	$currentState = $_POST['onoffState'];
	$location = $_POST['Location'];

	if($Name == null){
		echo 'Regist Fail! Please Check your Name';
	}

	else{
		$findSql = "select idx from controlList where KeyID='Directory' and Name='$Name'";
		$findResult = mysql_query($findSql, $connect);
		$check = mysql_result($findResult, 0, 0);

		if($check){
			$currentSql = "update controlList set Location='$location' where Name='$Name'";
			$prevSql = "update prev_controlList set Location='$location' where Name='$Name'";
			$result = mysql_query($currentSql, $connect);
			$prevResult = mysql_query($prevSql, $connect);
		}
		else{
			$currentSql = "insert into controlList values(null, '$Name', '$keyID', '$currentState', '$location')";
			$prevSql = "insert into prev_controlList values(null, '$Name', '$keyID', '$currentState', '$locaation')";
			$result = mysql_query($currentSql, $connect);
			$prevResult = mysql_query($prevSql, $connect);
		}

		$result = mysql_result($currentSql, $connect);
		$prevResult = mysql_result($prevSql, $connect);

		if($result || $prevResult){
			echo 'Regist Successful';
		}
		else{
			echo 'Regist Fail!!!';
		}
	}

?>
