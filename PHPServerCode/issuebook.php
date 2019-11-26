<?php
header("Content-type:application/json");
if (isset($_POST['rollno']) && isset($_POST['bookid'])) {
    require 'database.php';
    $conn = new mysqli($servername, $username, $password, $dbname);
    if ($conn->connect_error) {
        echo '{"done":false}';
        exit();
    }

    $sql = "INSERT INTO issuebook(sid,bid) VALUES (" . (int) ($_POST['rollno']) . "," . (int) ($_POST['bookid']) . ");";
    $result = $conn->query($sql);
    if ($result == true) {
        echo '{"done":true}';
    } else {
        echo '{"done":false}';
    }

    $conn->close();
} else {
    echo '{"done":false}';
}
?>