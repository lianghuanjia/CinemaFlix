function handleRegister() {
    let email = $("#registerEmail").val();
    let password = $("#registerPassword").val();

    console.log(email);
    console.log(password);

    try {
        let passwordArray = password.split("");
        console.log(passwordArray);
        password = passwordArray; //Don't say: let password = passwordArray;
    } catch (err) {
        console.log(err);
        getRegisterContent();
        setErrorMessage(err);
    }

    $.ajax(
        {
            url: openlab+":7014/api/g/idm/register",
            method: "POST",
            data: JSON.stringify({
                "email": email,
                "password": password
            }),
            dataType: "json",
            contentType: "application/json",
            statusCode: {
                200: function (data, textStatus, request){
                    console.log("Received 200 in Register");
                    handleRegister200(request);
                },
                204: function (data, textStatus, request) {
                    console.log("Received 204 in register");
                    console.log(request);
                    let all = request.getAllResponseHeaders();
                    console.log(all);
                    let transactionID = request.getResponseHeader("transactionID");
                    let sessionID = request.getResponseHeader("sessionID");
                    let requestDelay = request.getResponseHeader("requestDelay");
                    console.log("TransactionID: " + transactionID);
                    console.log("SessionID: " + sessionID);
                    setTimeout(getResponse(transactionID, requestDelay, handleRegister200, handleRegister400), requestDelay);
                },

                400: function (data, textStatus, request) {
                    console.log("Received 400 in register");
                    handleRegister400(request)
                },
                500: function (response) {
                    console.log("Received 500 in register");
                    getRegisterContent();
                    setErrorMessage("Receive 500 in Register.ajax");
                }
            }
        }
    )
}

function handleRegister200(request) {
    console.log("Handle register 200 function");
    let jsonResponse = request.responseJSON;
    if (jsonResponse.resultCode == 110) {
        getLoginContent();
        setErrorMessage("Register successfully! Please Login")
    }
    else{
        getRegisterContent();
        setErrorMessage(jsonResponse.message);
    }
}

function handleRegister400(request){
    console.log("Handle register 400 function");
    let jsonResponse = request.responseJSON;
    getRegisterContent();
    setErrorMessage(jsonResponse.message);
}
