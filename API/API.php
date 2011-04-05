<?php
// API

require_once '../common/DbManager.php';
    class API{
        private $dbManager;

        public function __construct() {
            $this->dbManager = new DbManager();
        }

        public function addUser($userName, $userPassword){
            return $this->dbManager->addUser($userName, $userPassword);
        }

        public function getUser($userID){
            return $this->dbManager->getUser($userID);
        }

        public function removeUser($userID){
            return $this->dbManager->removeUser($userID);
        }

        public function createShopSession($userID, $session_name, $where){           
            return $this->dbManager->createShopSession($userID, $session_name, $where);
        }

        public function removeShopSession($userID, $session_id){
            return $this->dbManager->removeShopSession($userID, $session_id);
        }

        public function addProductToSession($sessionID, $productName, $productPrice, $productQunatity){
            return $this->dbManager->addProductToSession($sessionID, $productName, $productPrice, $productQunatity);
        }

        public function removeProductFromSession($sessionID, $productID){
            return $this->dbManager->removeProductFromSession($sessionID, $productID);
        }

        public function getUserShopSessions($userID){
            return $this->dbManager->getUserShopSessions($userID);
        }

        public function getShopSessionProducts($sessionID){
            return $this->dbManager->getShopSessionProducts($sessionID);
        }
    }
?>
