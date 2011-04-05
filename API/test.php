<?php

require_once '../common/DbManager.php';

$dbManager = new DbManager();
//print_r($dbManager->addUser("livius", "test"));
//$dbManager->removeUser(1);
//print_r($dbManager->removeUser(4));
//print_r($dbManager->createShopSession(4, "sat". rand()));
//print_r($dbManager->removeShopSession(4, 21));
//print_r($dbManager->addProductToSession(19, "pname1", 100, 2));
print_r($dbManager->removeProductFromSession(19, 1));
?>
