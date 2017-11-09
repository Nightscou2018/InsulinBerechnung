<?php
print_r($DBH);
$DBH=new PDO("mysql:host=rdbms.strato.de;dbname=DB2465298", "U2465298", "Abraham1");
if(isset($_POST["absenden"])){
	$username=$_POST["username"];
	$password=hash("sha256",$_POST["passwort"]);
	$name=$_POST["vornachname"];
	$gebdat=$_POST["gebdat"];
	$sql="INSERT INTO dia_user VALUES(:username, :password, :name, :gebdat)";
	$stmt=$DBH->prepare($sql);
	$stmt->bindParam(":username", $username, PDO::PARAM_STR);
	$stmt->bindParam(":password", $password, PDO::PARAM_STR);
	$stmt->bindParam(":name", $name, PDO::PARAM_STR);
	$stmt->bindParam(":gebdat", $gebdat, PDO::PARAM_STR);
} else {
	header('Location: http://mkservices.de/diabetes/newuser.html');
}
?>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Neuer Benutzer angelegt</title>
</head>
<body>
<?php
	if($stmt->execute()){
		echo "<h2>Neuer Benutzer angelegt</h2>";
	} else {
		echo "<h2>Ein Fehler ist aufgetreten.</h2>";
		echo $stmt->errorCode()."<br>";
	}
?>
<a href="newuser.html">Zurück</a>
</body>
</html>