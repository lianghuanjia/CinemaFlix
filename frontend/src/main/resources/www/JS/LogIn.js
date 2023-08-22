function handleLogin() {
    let email = $("#loginEmail").val();
    let password = $("#loginPassword").val();

    console.log(email);
    console.log(password);

    try {
        let passwordArray = password.split("");
        console.log(passwordArray);
        password = passwordArray; //Don't say: let password = passwordArray;
    } catch (err) {
        console.log(err);
        getLoginContent()
        setErrorMessage(err);
    }

    $.ajax(
        {
            url: openlab+":7014/api/g/idm/login",
            method: "POST",
            data: JSON.stringify({
                "email": email,
                "password": password
            }),
            dataType: "json",
            contentType: "application/json",
            statusCode: {
                200: function (data, textStatus, request) {
                    console.log("Received 200 in Login");
                    handleLogin200(request);
                },
                204: function (data, textStatus, request) {
                    console.log("Received 204 in Login");
                    console.log(request);
                    let allHeader = request.getAllResponseHeaders();
                    console.log(allHeader);
                    let transactionID = request.getResponseHeader("transactionID");
                    let sessionID = request.getResponseHeader("sessionID");
                    let requestDelay = request.getResponseHeader("requestDelay");
                    console.log("TransactionID: " + transactionID);
                    console.log("SessionID: " + sessionID);
                    setTimeout(getResponse(transactionID, requestDelay, handleLogin200, handleLogin400), requestDelay);
                },

                400: function (data, textStatus, request) {
                    console.log("Received 400 in Login");
                    handleLogin400(request)
                },
                500: function (response) {
                    console.log("Received 500 in Login");
                    getLoginContent()
                    setErrorMessage("Receive 500 in Login.ajax");
                }
            }
        }
    )
}

function handleLogin200(request) {
    console.log("Handle Login 200 function");
    console.log(request);
    let email = $("#loginEmail").val();
    console.log("In handleLogin200, user email is: " + email);
    let jsonResponse = request.responseJSON;
    if (jsonResponse.resultCode == 120) {
        let sessionID = jsonResponse.sessionID;
        setCookies(email, sessionID);
        getQuickSearchContent();
        setErrorMessage("Login successfully! Now you can quick search by movie Title");
    }
    else{
        getLoginContent()
        setErrorMessage(jsonResponse.message);
    }
}

function handleLogin400(request){
    console.log("Handle Login 400 function");
    let jsonResponse = request.responseJSON;
    getLoginContent()
    setErrorMessage(jsonResponse.message);
}
