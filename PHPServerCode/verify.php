<?php
header("Content-type:application/json");
    if(isset($_POST['rollno']) && isset($_POST['pass'])){
        require 'database.php';
        $conn = new mysqli($servername, $username, $password, $dbname);
        if ($conn->connect_error) {
            echo '{"login":false}';
            exit();
        }
    
        $sql = "select name,pass FROM students WHERE rollno = ".(int)($_POST['rollno']).";";
        $result = $conn->query($sql);
        if($result->num_rows > 0){
            $row = $result->fetch_assoc();
            if (password_verify($_POST['pass'], $row['pass'])) {
                echo '{"login":true,"name":"'.$row['name'].'"}';
            }else
                echo '{"login":false}';
        }else
            echo '{"login":false}';
        $conn->close();

    }else 
        echo '{"login":false}';
?>