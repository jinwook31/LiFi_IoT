<?php
  $link = mysql_connect("localhost", "root", "1541") or die ("Fail to connect MySQL");
  $selected_db = mysql_select_db("db", $link) or die ("Fail to connect DB");
  $num = $_GET["num"];
  echo "Sending $num to data table....";
  $sqlt = "insert into data values ('$num')";
  $db_result = mysql_query($sqlt, $link) or die("Fail to connect Table");

  echo "Success!!";
?>
