<?php

    require_once 'API.php';
    $api        = new API();
    $method     = $_POST['method'];
    $params     = ($_POST['params']);

    //echo("asas".print_r($params, true). " method: " . $method);

    switch($method){
        case 'add_user':
                        echo json_encode($api->addUser($params['username'], $params['userPassword']));
                        break;
        case 'remove_user':
                        echo (json_encode($api->removeUser($params['userID'])));
                        break;
        case 'add_shop_session':                        
                        echo(json_encode($api->createShopSession($params['userID'], $params['sessionName'], $params['where'])));
                        break;
        case 'remove_shop_session':
                        echo(json_encode($api->removeShopSession($params['userID'], $params['sessionID'])));
                        break;
        case 'add_product_to_shop_session':
                        echo(json_encode($api->addProductToSession($params['sessionID'], $params['productName'], $params['productPrice'], $params['productQuantity'])));
                        break;
        case 'remove_product_from_shop_session':
                        echo(json_encode($api->removeProductFromSession($params['sessionID'], $params['productID'])));
                        break;
        case 'get_user_shop_sessions':
                        echo(json_encode($api->getUserShopSessions($params['userID'])));
                        break;
        case 'get_shop_sessions_products':
                        echo(json_encode($api->getShopSessionProducts($params['sessionID'])));
                        break;
    }

?>
