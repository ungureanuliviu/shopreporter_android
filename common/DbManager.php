<?php

    $dbhost = 'localhost';
    $dbuser = 'liviu';
    $dbpass = 'tyger1988';
    $dbname = 'shopdb';

    $conn = mysql_connect($dbhost, $dbuser, $dbpass) or die(mysql_error());
    
    mysql_select_db($dbname) or die(mysql_error());


    class DbManager{

        public function __construct() {
            
        }

        public function addUser($userName, $userPassword){
            $addUserQuery   = "INSERT INTO users(user_name, user_password) VALUES('".mysql_real_escape_string($userName)."', '".md5($userPassword)."')";
            $addUserResults = mysql_query($addUserQuery);
            $returnResponse = array("is_success" => 1, "method" => "addUser", params=>array("userName" => $userName, "password" => $userPassword));

            if(mysql_affected_rows() == -1){
                    $returnResponse['is_success'] = 0;
                    $returnResponse['error']      = mysql_error();
            }

            return $returnResponse;

        }

        public function getUser($userID){
            $userInfoQuery  = "SELECT * FROM users WHERE user_id =" . $userID;
            $userInfoResult = mysql_query($userInfoQuery) or die(mysql_error());
            $rowInfo        = mysql_fetch_assoc($userInfoResult);

            $returnResponse = array("is_success" => 1, "method" => "getUser", "data" => $rowInfo, params=>array("userID" => $userID));

            if($rowInfo == FALSE){
                    $returnResponse['is_success'] = 0;
                    $returnResponse['error']      = mysql_error();
            }

            return $returnResponse;
        }

        public function removeUser($userID){
            $removeUserQuery  = "DELETE FROM users where user_id =" . $userID;
            $removeUserResult = mysql_query($removeUserQuery);
            $returnResponse   = array("is_success" => 1, "method" => "removeUser", params=>array("userID" => $userID));

            if(mysql_affected_rows() == -1){
                    $returnResponse['is_success'] = 0;
                    $returnResponse['error']      = mysql_error();

                    return $returnResponse;
            }

            $removeUserSessionsQuery = "DELETE FROM shop_sessions where sh_user_id = " . $userID;
            $removeSessionsResult    = mysql_query($removeUserSessionsQuery);
            
            if(mysql_affected_rows() == -1){
                $returnResponse['is_success']    = 0;
                $returnResponse['error']         = mysql_error();
                $returnResponse['totalSessions'] = -1;
            }
            else
                $returnResponse['totalSessions'] = mysql_affected_rows();

            return $returnResponse;
        }

        public function createShopSession($userID, $session_name, $where){
            $now                = time();
            $createSSQuery      = "INSERT INTO shop_sessions(sh_user_id, sh_name, sh_where, sh_when) VALUES(".$userID.",'".mysql_real_escape_string($session_name)."','".mysql_real_escape_string($where)."','".$now."')";
            $createSSResult     = mysql_query($createSSQuery);

            $returnResponse   = array("is_success" => 1, "method" => "createShopSession", params=>array("userID" => $userID, "sessionName" => $session_name, "where" => $where, "when" => $now));

            if(mysql_affected_rows() == -1){
                    $returnResponse['is_success'] = 0;
                    $returnResponse['error']      = mysql_error();

                    return $returnResponse;
            }

            $getSSIDQuery   = "SELECT * FROM `shop_sessions` WHERE sh_user_id = ".$userID." order by sh_id DESC limit 1";
            $getSSIDResult  = mysql_query($getSSIDQuery);
            $num_rows       = mysql_num_rows($getSSIDResult);

            if($num_rows != FALSE){
                if($num_rows > 0){
                    $row = mysql_fetch_assoc($getSSIDResult);
                    $returnResponse['data'] = $row;
                }
                else{
                    $returnResponse['is_success'] = 0;
                    $returnResponse['error']      = mysql_error();
                }
            }
            else{
                $returnResponse['is_success'] = 0;
                $returnResponse['error']      = mysql_error();
            }

            return $returnResponse;
        }

        public function removeShopSession($userID, $session_id){
            $removeSSQuery      = "DELETE FROM shop_sessions WHERE sh_user_id=".$userID." AND sh_id=".$session_id;            
            $removeSSResult     = mysql_query($removeSSQuery) or die(mysql_error());

            $returnResponse   = array("is_success" => 1, "method" => "removeShopSession", params=>array("userID" => $userID, "sessionID" => $session_id));

            if(mysql_affected_rows() == -1){
                    $returnResponse['is_success'] = 0;
                    $returnResponse['error']      = mysql_error();

                    return $returnResponse;
            }

            return $returnResponse;
        }

        public function addProductToSession($sessionID, $productName, $productPrice, $productQunatity){
            $now                = time();
            $addProductQuery    = "INSERT INTO products(p_sh_id, p_name, p_price, p_quantity, p_when) VALUES(".$sessionID.",'".mysql_real_escape_string($productName)."',".$productPrice.",".$productQunatity.",'".$now."')";
            $addProductResult   = mysql_query($addProductQuery);            
            $returnResponse     = array("is_success" => 1, "method" => "addProductToSession", params=>array("sessionID" => $sessionID, "productName" => $productName, "productPrice" => $productPrice, "when" => $now));

            if(mysql_affected_rows() == -1){
                    $returnResponse['is_success'] = 0;
                    $returnResponse['error']      = mysql_error();

                    return $returnResponse;
            }

            $getProductIDQuery   = "SELECT * FROM products WHERE p_sh_id = ".$sessionID." order by p_id DESC limit 1";
            $productIDResult     = mysql_query($getProductIDQuery);
            $num_rows            = mysql_num_rows($productIDResult);

            if($num_rows != FALSE){
                if($num_rows > 0){
                    $row = mysql_fetch_assoc($productIDResult);
                    $returnResponse['data'] = array( 'new_product_id'        => $row['p_id'],
                                                     'new_product_quantity'  => $row['p_quantity'],
                                                     'new_product_name'      => $row['p_name'],
                                                     'new_product_price'     => $row['p_price']
                                                   );
                }
                else{
                    $returnResponse['is_success'] = 0;
                    $returnResponse['error']      = mysql_error();
                }
            }
            else{
                $returnResponse['is_success'] = 0;
                $returnResponse['error']      = mysql_error();
            }

            return $returnResponse;
     }    

    public function removeProductFromSession($sessionID, $productID){
        $removeProductQuery     = "DELETE FROM products WHERE p_sh_id=". $sessionID . " AND p_id=".$productID;
        $removeProductResult    = mysql_query($removeProductQuery);
        $returnResponse         = array("is_success" => 1, "method" => "removeProductFromSession", params=>array("sessionID" => $sessionID, "productID" => $productID));

        if(mysql_affected_rows() == -1){
                $returnResponse['is_success'] = 0;
                $returnResponse['error']      = mysql_error();
        }

        return $returnResponse;
    }

    public function getUserShopSessions($userID){
        $getSessionsQuery     = "SELECT * FROM  shop_sessions WHERE sh_user_id=".$userID;
        $getSessionsResult    = mysql_query($getSessionsQuery);
        $returnResponse       = array("is_success" => 1, "method" => "getUserShopSessions", params=>array("userID" => $userID));
        $data                 = array();

        if(mysql_affected_rows() == -1){
                $returnResponse['is_success'] = 0;
                $returnResponse['error']      = mysql_error();
        }
        else{
            while($session = mysql_fetch_assoc($getSessionsResult)){
                $data[count($data)] = $session;
            }
        }

        $returnResponse['data'] = $data;
        return $returnResponse;
    }

    public function getShopSessionProducts($sessionID){
        $getSessionProductsQuery  = "SELECT * FROM  products WHERE p_sh_id=".$sessionID;
        $getSessionProductsResult = mysql_query($getSessionProductsQuery);
        $returnResponse           = array("is_success" => 1, "method" => "getShopSessionProducts", params=>array("userID" => $userID));
        $data                     = array();

        if(mysql_affected_rows() == -1){
                $returnResponse['is_success'] = 0;
                $returnResponse['error']      = mysql_error();
        }
        else{
            while($product = mysql_fetch_assoc($getSessionProductsResult)){
                $data[count($data)] = $product;
            }
        }

        $returnResponse['data'] = $data;
        return $returnResponse;
    }

 }
?>
