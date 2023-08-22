function addToCart(movieID) {
    console.log("In addToCart:");
    console.log(movieID);
    let quantity = $("#Quantity").val();
    console.log(quantity);

    console.log("Cookies email is: " + Cookies.get("email"));
    $.ajax(
        {
            url: openlab+":7014/api/g/billing/cart/insert",
            method: "POST",
            data: JSON.stringify({
                "email": Cookies.get("email"),
                "movieId": movieID,
                "quantity": quantity
            }),
            headers:{
                "email": Cookies.get("email"),
                "sessionID": Cookies.get("sessionID")
            },
            dataType: "json",
            contentType: "application/json",
            statusCode: {
                200: function (data, textStatus, request){
                    console.log("Received 200 in Cart.addToCart");
                    handleInsertCart200(request);
                },
                204: function (data, textStatus, request) {
                    console.log("Received 204 in Cart.addToCart");
                    console.log(request);
                    let all = request.getAllResponseHeaders();
                    console.log(all);
                    let transactionID = request.getResponseHeader("transactionID");
                    let sessionID = request.getResponseHeader("sessionID");
                    let requestDelay = request.getResponseHeader("requestDelay");
                    console.log("TransactionID: " + transactionID);
                    console.log("SessionID: " + sessionID);
                    setTimeout(getResponse(transactionID, requestDelay, handleInsertCart200, handleInsertCart400), requestDelay);
                },

                400: function (data, textStatus, request) {
                    console.log("Received 400 in Cart.addToCart");
                    handleInsertCart400(request)
                },
                500: function (response) {
                    console.log("Received 500 in Cart.addToCart");
                    setErrorMessage("Receive 500 in Cart.addToCart.ajax");
                }
            }
        }
    )
}

function handleInsertCart200(request) {
    console.log("Handle insert Cart 200 function");
    let jsonResponse = request.responseJSON;
    if (jsonResponse.resultCode == 3100) {
        setErrorMessage("Successfully insert item into cart")
    }
    else{
        getShoppingCartContent(request);
        setErrorMessage(jsonResponse.message);
    }
}

function handleInsertCart400(request){
    console.log("Handle insert Cart 400 function");
    let jsonResponse = request.responseJSON;
    getShoppingCartContent();
    setErrorMessage(jsonResponse.message);
}


function retrieveCart(){
    clearMainAndErrorMessage();
    console.log("Cookies email is: " + Cookies.get("email"));
    $.ajax(
        {
            url: openlab+":7014/api/g/billing/cart/retrieve",
            method: "POST",
            data: JSON.stringify({
                "email": Cookies.get("email")
            }),
            headers:{
                "email": Cookies.get("email"),
                "sessionID": Cookies.get("sessionID")
            },
            dataType: "json",
            contentType: "application/json",
            statusCode: {
                200: function (data, textStatus, request){
                    console.log("Received 200 in Cart.RetrieveCart");
                    handleRetrieveCart200(request);
                },
                204: function (data, textStatus, request) {
                    console.log("Received 204 in Cart.RetrieveCart");
                    console.log(request);
                    let all = request.getAllResponseHeaders();
                    console.log(all);
                    let transactionID = request.getResponseHeader("transactionID");
                    let sessionID = request.getResponseHeader("sessionID");
                    let requestDelay = request.getResponseHeader("requestDelay");
                    console.log("TransactionID: " + transactionID);
                    console.log("SessionID: " + sessionID);
                    setTimeout(getResponse(transactionID, requestDelay, handleRetrieveCart200, handleRetrieveCart400), requestDelay);
                },

                400: function (data, textStatus, request) {
                    console.log("Received 400 in Cart.RetrieveCart");
                    handleRetrieveCart400(request)
                },
                500: function (response) {
                    console.log("Received 500 in Cart.RetrieveCart");
                    setErrorMessage("Receive 500 in Cart.RetrieveCart.ajax");
                }
            }
        }
    )
}

function handleRetrieveCart200(request) {
    console.log("Handle RetrieveCart 200 function");
    let jsonResponse = request.responseJSON;
    if (jsonResponse.resultCode == 312) {
        setErrorMessage("Shopping cart is empty")
    }
    else if(jsonResponse.resultCode == 3130){
        setErrorMessage(("Retrieve shopping cart!"))
        showCart(request);
        console.log(request);
    }
    else{
        getShoppingCartContent(request);
        setErrorMessage(jsonResponse.message);
    }
}

function handleRetrieveCart400(request){
    console.log("Handle RetrieveCart 400 function");
    let jsonResponse = request.responseJSON;
    getShoppingCartContent();
    setErrorMessage(jsonResponse.message);
}

function showCart(request){
    clearMainAndErrorMessage();
    let jsonResponse = request.responseJSON;
    let rowHTML = "";
    rowHTML = "<table id = \"cartTable\" border=\"1\" width='100%'>";
    rowHTML = "<tr><td>MovieID</td><td>Quantity</td>";

    for(let i = 0; i < jsonResponse.items.length; ++i){
        let item = jsonResponse.items[i];
        console.log(item);

        rowHTML += "<tr><td>"+item["movieId"]+"</td><td>"+item["quantity"]+"</td></tr>";
    }
    $(".main").append(rowHTML);


    rowHTML += "<tr>" + "<td>" + "movieId:  " + "</td>" + "<td>" + movie.title + "</td>" + "</tr>";
}

function checkOut(){
    clearMainAndErrorMessage();
    console.log("Cookies email is: " + Cookies.get("email"));
    $.ajax(
        {
            url: openlab+":7014/api/g/billing/order/place",
            method: "POST",
            data: JSON.stringify({
                "email": Cookies.get("email")
            }),
            headers:{
                "email": Cookies.get("email"),
                "sessionID": Cookies.get("sessionID")
            },
            dataType: "json",
            contentType: "application/json",
            statusCode: {
                200: function (data, textStatus, request){
                    console.log("Received 200 in Cart.CheckOut");
                    handleCheckOutCart200(request);
                },
                204: function (data, textStatus, request) {
                    console.log("Received 204 in Cart.CheckOut");
                    console.log(request);
                    let all = request.getAllResponseHeaders();
                    console.log(all);
                    let transactionID = request.getResponseHeader("transactionID");
                    let sessionID = request.getResponseHeader("sessionID");
                    let requestDelay = request.getResponseHeader("requestDelay");
                    console.log("TransactionID: " + transactionID);
                    console.log("SessionID: " + sessionID);
                    setTimeout(getResponse(transactionID, requestDelay, handleCheckOut200, handleCheckOut400), requestDelay);
                },

                400: function (data, textStatus, request) {
                    console.log("Received 400 in Cart.CheckOut");
                    handleCheckOut400(request)
                },
                500: function (response) {
                    console.log("Received 500 in Cart.CheckOut");
                    setErrorMessage("Receive 500 in Cart.CheckOut.ajax");
                }
            }
        }
    )
}

function handleCheckOut200(request) {
    console.log("Handle Check out 200 function");
    let jsonResponse = request.responseJSON;
    if(jsonResponse.resultCode == 3400){
        setErrorMessage(("Placing order"))
        window.open(jsonResponse.redirectURL);

    }
    else{
        setErrorMessage(jsonResponse.message);
    }
}

function handleCheckOut400(request){
    console.log("Handle Check out 400 function");
    let jsonResponse = request.responseJSON;
    setErrorMessage(jsonResponse.message);
}

