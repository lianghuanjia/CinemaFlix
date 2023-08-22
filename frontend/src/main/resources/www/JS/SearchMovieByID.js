function searchByMovieID(movieID){
    $(".movies").empty();
    console.log("*** In SearchMovieByID.searchByMovieID ***");

    let email = Cookies.get("email");
    let sessionID = Cookies.get("sessionID");

    $.ajax(
        {
            url: openlab+ ":7014/api/g/movies"+"/get/" + movieID,
            method: "GET",
            headers:{
                "email": email,
                "sessionID": sessionID
            },
            statusCode:{
                200: function(data, textStatus, request) {
                    console.log("Received 200 in SearchByMovieID");
                    handleSearchByMovieID200(request);
                },
                204: function(data, textStatus, request){
                    console.log("Received 204 in SearchByMovieID");
                    console.log(request);
                    let allHeader = request.getAllResponseHeaders();
                    console.log(allHeader);
                    let transactionID = request.getResponseHeader("transactionID");
                    let sessionID = request.getResponseHeader("sessionID");
                    let requestDelay = request.getResponseHeader("requestDelay");
                    console.log("TransactionID: " + transactionID);
                    console.log("SessionID: " + sessionID);
                    setTimeout(getResponse(transactionID, requestDelay, handleSearchByMovieID200, handleSearchByMovieID400), requestDelay);
                },
                400: function (data, textStatus, request) {
                    console.log("Received 400 in SearchByMovieID");
                    handleSearchByMovieID400();
                },
                500: function (response) {
                    console.log("Received 500 in SearchByMovieID");
                    getAdvancedSearchContent();
                    setErrorMessage("Receive 500 in SearchByMovieID.ajax");
                }
            }

        })
}

function handleSearchByMovieID200(request) {
    console.log("Handle SearchByMovieID 200 function");
    console.log(request);
    let jsonResponse = request.responseJSON;
    if (jsonResponse.resultCode == 210) {
        setErrorMessage("Found movies with search parameters.");
        setDetailedMovieContent(jsonResponse);
    }
    else
    if (jsonResponse.resultCode == 131 || jsonResponse.resultCode == 132
        || jsonResponse.resultCode == 133 || jsonResponse.resultCode == 134)
    {
        getLoginContent();
        setErrorMessage(jsonResponse.message + " Please login.");
    } else {
        getAdvancedSearchContent();
        setErrorMessage(jsonResponse.message);
    }
}

function handleSearchByMovieID400(request){
    console.log("Handle SearchByMovieID 400 function");
    let jsonResponse = request.responseJSON;
    getAdvancedSearchContent();
    setErrorMessage(jsonResponse.message);
}