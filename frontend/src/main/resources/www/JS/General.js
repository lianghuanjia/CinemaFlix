let openlab = "http://andromeda-70.ics.uci.edu";
let localhost = "http://localhost";

function setErrorMessage(errorMessage){
    $(".ErrorMessage").empty();

    $(".ErrorMessage").append(errorMessage);
}

function clearMainAndErrorMessage(){
    $(".main").empty();
    $(".ErrorMessage").empty();
    $(".AddToCartInfo").empty();
    $(".movies").empty();
}


function getResponse(transactionID, requestDelay,handle200, handle400){
    console.log("in get Response, transactionID: " + transactionID);
    $.ajax(
        {
            url: openlab+":7014/api/g/report",
            method: "GET",
            headers: {
              "transactionID": transactionID
            },
            statusCode: {
                200: function (data, textStatus, request) {
                    console.log(request);
                    handle200(request);
                },
                204: function (data, textStatus, request) {
                    setTimeout(getResponse(transactionID, requestDelay, handle200, handle400),requestDelay);
                },
                400: function (data, textStatus, request) {
                    handle400(request);
                },
                500: function (response) {
                    getRegisterContent();
                    setErrorMessage("Receive 500 internal service error in getResponse.ajax");
                }
            }
        }
    )
}

function setCookies(email, sessionID){
    console.log("In setCookies function, params email: " + email + ", and sessionID: " + sessionID);
    Cookies.set("email",email);
    Cookies.set("sessionID", sessionID);
    console.log("After setting cookies, in cookies the email is: " + Cookies.get("email")+ ", and sessionID is: " + Cookies.get("sessionID"));
}


