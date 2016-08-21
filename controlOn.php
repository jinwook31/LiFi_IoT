<?php

	$connect = mysql_connect("localhost", "root", "1541") or die ("Cannot connect SQL");
	mysql_select_db("LoT_control", $connect);

	echo 'hi';

        $Name = $_POST['Name'];
        $keyID = $_POST['KeyID'];
        $state = $_POST['onoffState'];

        $change = "update controlList set onoffState = '1' where KeyID='$keyID'";
        $temp = mysql_query($change, $connect);

        if($temp){
                echo 'successfull change!!';
        }
        else{
                echo 'fail';
        }

?>
