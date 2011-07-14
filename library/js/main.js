function callback(data){
    alert(data.toSource());
}

$(document).ready(function(){
    var client = new Client();

// ADD USER
//    client.addUser("tester", "test", function(data){
//       alert("in callback: " + data.toSource());
//    });

    //client.createShopSession(5, "First Shop Session [testing2]", "Kaufland", callback);
    
    //client.removeUser(3, callback);
    //client.removeShopSession(4, 42, callback);
    //client.addProductToShopSession(4, "product1", "10", "1", callback);
    //client.addProductToShopSession(4, "product2", "140", "12", callback);
    client.getUserShopSessions(5, callback);
    //client.removeProductFromShopSession(4, 4, callback);
    //client.getShopSessionProducts(19, callback);
});