<?php
header("Content-type:application/json");
if(isset($_POST['rollno'])){
    require 'database.php';
    $conn = new mysqli($servername, $username, $password, $dbname);
    if ($conn->connect_error) {
        echo '[]';
        exit();
    }

    $sql = "select books.name,b.issue_date from issuebook b INNER JOIN books ON b.bid = books.id WHERE b.sid = ".(int)($_POST['rollno']).";";
    $result = $conn->query($sql);
    $tmp = array();
    if($result->num_rows > 0){
        while($row = $result->fetch_assoc()) {
            array_push($tmp,$row);
        }
    }
    echo json_encode($tmp);
    $conn->close();
}else
    echo '[]';
?>