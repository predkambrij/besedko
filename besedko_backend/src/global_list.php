<?


$con = mysql_connect("localhost","root","root") or die("cannot connect");


if (!mysql_select_db('global_list', $con)) {
	(mysql_query('CREATE DATABASE global_list', $con)) or die('Error creating database: ' . mysql_error() . "\n");
	mysql_select_db('global_list', $con) or die("cannot select db". mysql_error() . "\n");
}

mysql_query("CREATE TABLE if not exists global_list(id varchar(150),score int)",$con) or die("cannot create table");

// query for update
$result = mysql_query("SELECT * FROM global_list where id='".$_GET["id"]."'", $con);
$num_rows = mysql_num_rows($result);

if ($num_rows == 1) {
	$row = mysql_fetch_array($result);
	if ($_GET["score"] > $row["score"]) {
		mysql_query("UPDATE global_list SET score=".$_GET["score"]." where id='".$_GET["id"]."'", $con) or die("update failed". mysql_error()."\n");
	}
} else {
	mysql_query("insert into global_list (id,score) values('".$_GET["id"]."', ".$_GET["score"].")") or die("insert failed". mysql_error()."\n");
}

// position after
$score_b =-1;
$pos1=-1;
$score1 =-1;
$count = 0;
$result = mysql_query("SELECT * FROM global_list order by score desc");
while($row = mysql_fetch_array($result)) {
	$count++;
	
	if($row['id'] == $_GET["id"]) {
		$score1 =$row['score'];
		$pos1 = $count; 
		break;
	} else {
		$score_b = $row['score'];
	}
}


mysql_close($con);

$posts = array ("best_position" => $pos1
		, "best_score" => $score1
		, "b_pos_score" => $score_b);
echo json_encode($posts);


?>
