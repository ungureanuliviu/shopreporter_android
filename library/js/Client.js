var Client      = function(){};
Client.methods  = {};

// constants
Client.API_PATH                                     = "/ShopReporter/API/";
Client.methods.ADD_USER                             = "add_user";
Client.methods.REMOVE_USER                          = "remove_user";
Client.methods.CREATE_SHOP_SESSION                  = "add_shop_session";
Client.methods.REMOVE_SHOP_SESSION                  = "remove_shop_session";
Client.methods.ADD_PRODUCT_TO_SHOP_SESSION          = "add_product_to_shop_session";
Client.methods.REMOVE_PRODUCT_FROM_SHOP_SESSION     = "remove_product_from_shop_session";
Client.methods.GET_USER_SHOP_SESSIONS               = "get_user_shop_sessions";
Client.methods.GET_SHOP_SESSION_PRODUCTS            = "get_shop_sessions_products";

Client.prototype = {
    constructor: Client,
    handlerError: function(errorObj){

    },
    addUser: function(userName_, userPassword_, callback){
        $.ajax({
                type: "post",
                url: Client.API_PATH,
                data: {
                        method: Client.methods.ADD_USER,
                        params: {
                            username : userName_,
                            userPassword : userPassword_
                        }
                },
                success: function(data){                    
                    var mainData = {};

                    try{
                        mainData = JSON.parse(data);
                    }catch(e){
                        alert(e.toSource());
                        mainData = undefined;
                    }finally{
                        if(typeof(callback) != 'undefined')
                            callback(mainData);
                    }
                }
              });
    },
    removeUser: function(userID_, callback){
        $.ajax({
                type: "post",
                url: Client.API_PATH,
                data: {
                        method: Client.methods.REMOVE_USER,
                        params: {
                            userID : userID_
                        }
                },
                success: function(data){
                    var mainData = {};

                    try{
                        mainData = JSON.parse(data);
                    }catch(e){
                        alert(e.toSource());
                        mainData = undefined;
                    }finally{
                        if(typeof(callback) != 'undefined')
                            callback(mainData);
                    }
                }
              });
    },

    createShopSession: function(userID_, sessionName_, where_, callback){        
        $.ajax({
                type: "post",
                url: Client.API_PATH,
                data: {
                        method: Client.methods.CREATE_SHOP_SESSION,
                        params: {
                            userID : userID_,
                            sessionName: sessionName_,
                            where: where_
                        }
                },
                success: function(data){
                    var mainData = {};

                    try{
                        mainData = JSON.parse(data);
                    }catch(e){
                        alert(e.toSource());
                        mainData = undefined;
                    }finally{
                        if(typeof(callback) != 'undefined')
                            callback(mainData);
                    }
                }
              });
    },
    removeShopSession: function(userID_, sessionID_, callback){
        $.ajax({
                type: "post",
                url: Client.API_PATH,
                data: {
                        method: Client.methods.REMOVE_SHOP_SESSION,
                        params: {
                            userID : userID_,
                            sessionID: sessionID_
                        }
                },
                success: function(data){
                    var mainData = {};

                    try{
                        mainData = JSON.parse(data);
                    }catch(e){
                        alert(e.toSource());
                        mainData = undefined;
                    }finally{
                        if(typeof(callback) != 'undefined')
                            callback(mainData);
                    }
                }
              });
    },
    addProductToShopSession: function(sessionID_,
                                      productName_,
                                      productPrice_,
                                      productQuantity_,
                                      callback
                                     ){
        $.ajax({
                type: "post",
                url: Client.API_PATH,
                data: {
                        method: Client.methods.ADD_PRODUCT_TO_SHOP_SESSION,
                        params: {
                            sessionID: sessionID_,
                            productName: productName_,
                            productPrice: productPrice_,
                            productQuantity: productQuantity_
                        }
                },
                success: function(data){
                    var mainData = {};

                    try{
                        mainData = JSON.parse(data);
                    }catch(e){
                        alert(e.toSource());
                        mainData = undefined;
                    }finally{
                        if(typeof(callback) != 'undefined')
                            callback(mainData);
                    }
                }
              });
    },
    removeProductFromShopSession: function(sessionID_,
                                           productID_,
                                           callback
                                           ){
        $.ajax({
                type: "post",
                url: Client.API_PATH,
                data: {
                        method: Client.methods.REMOVE_PRODUCT_FROM_SHOP_SESSION,
                        params: {
                            sessionID: sessionID_,
                            productID: productID_
                        }
                },
                success: function(data){
                    var mainData = {};

                    try{
                        mainData = JSON.parse(data);
                    }catch(e){
                        alert(e.toSource());
                        mainData = undefined;
                    }finally{
                        if(typeof(callback) != 'undefined')
                            callback(mainData);
                    }
                }
              });
    },
    getUserShopSessions: function(userID_, callback){
        $.ajax({
                type: "post",
                url: Client.API_PATH,
                data: {
                        method: Client.methods.GET_USER_SHOP_SESSIONS,
                        params: {
                            userID: userID_
                        }
                },
                success: function(data){
                    var mainData = {};

                    try{
                        mainData = JSON.parse(data);
                    }catch(e){
                        alert(e.toSource());
                        mainData = undefined;
                    }finally{
                        if(typeof(callback) != 'undefined')
                            callback(mainData);
                    }
                }
              });
    },
    getShopSessionProducts: function(sessionID_){
        $.ajax({
                type: "post",
                url: Client.API_PATH,
                data: {
                        method: Client.methods.GET_SHOP_SESSION_PRODUCTS,
                        params: {
                            sessionID: sessionID_
                        }
                },
                success: function(data){
                    var mainData = {};

                    try{
                        mainData = JSON.parse(data);
                    }catch(e){
                        alert(e.toSource());
                        mainData = undefined;
                    }finally{
                        if(typeof(callback) != 'undefined')
                            callback(mainData);
                    }
                }
              });
    }


}

//Client.methods.GET_SHOP_SESSION_PRODUCTS