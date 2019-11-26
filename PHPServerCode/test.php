<?php
$options = [
    'cost' => 11,
];
// Get the password from post
$passwordFromPost = 'Enter the password and copy the hash and insert user manually';
$hash = password_hash($passwordFromPost, PASSWORD_BCRYPT, $options);
echo $hash;

?>