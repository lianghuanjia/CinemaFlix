function clickSearch(){
    $(".movies").empty();
    console.log("*** In QuickSearch.clickSearch ***");

    let title = $("#QuickSearchTitle").val();
    let orderby = $("#orderby option:selected").text();
    let direction = $("#direction option:selected").text();
    let limit = $("#limit option:selected").text();

    if(orderby == "orderby"){
        orderby = "rating";
    }
    if(direction == "direction"){
        direction = "desc";
    }
    if(limit == "limit"){
        limit = 10;
    }

    console.log("title: " + title);
    console.log("orderby : " + orderby);
    console.log("direction: " + direction);
    console.log("limit: " + limit);

    let email = Cookies.get("email");
    let sessionID = Cookies.get("sessionID");

    $.ajax(
        {
            url: openlab+":7014/api/g/movies/search",
            method: "GET",
            data:{
                "title": title,
                "orderby": orderby,
                "direction": direction,
                "limit": limit
            },
            headers:{
                "email": email,
                "sessionID": sessionID
            },
            statusCode:{
                200: function(data, textStatus, request) {
                    console.log("Received 200 in QuickSearch");
                    handleQuickSearch200(request);
                },
                204: function(data, textStatus, request){
                    console.log("Received 204 in QuickSearch");
                    console.log(request);
                    let allHeader = request.getAllResponseHeaders();
                    console.log(allHeader);
                    let transactionID = request.getResponseHeader("transactionID");
                    let sessionID = request.getResponseHeader("sessionID");
                    let requestDelay = request.getResponseHeader("requestDelay");
                    console.log("TransactionID: " + transactionID);
                    console.log("SessionID: " + sessionID);
                    setTimeout(getResponse(transactionID, requestDelay, handleQuickSearch200, handleQuickSearch400), requestDelay);
                },
                400: function (data, textStatus, request) {
                    console.log("Received 400 in QuickSearch");
                    handleQuickSearch400(request);
                },
                500: function (response) {
                    console.log("Received 500 in QuickSearch");
                    getQuickSearchContent()
                    setErrorMessage("Receive 500 in QuickSearch.ajax");
                }
            }

        })
}

function handleQuickSearch200(request) {
    console.log("Handle quick search 200 function");
    console.log(request);
    let jsonResponse = request.responseJSON;
    if (jsonResponse.resultCode == 210) {
        setErrorMessage("Found movies with search parameters.");
        setMovieListContent(jsonResponse);
    }
    else
        if (jsonResponse.resultCode == 131 || jsonResponse.resultCode == 132
            || jsonResponse.resultCode == 133 || jsonResponse.resultCode == 134)
        {
            getLoginContent();
            setErrorMessage(jsonResponse.message + " Please login.");
        } else {
            getQuickSearchContent();
            setErrorMessage(jsonResponse.message);
        }
}

function handleQuickSearch400(request){
    console.log("Handle QuickSearch 400 function");
    let jsonResponse = request.responseJSON;
    getQuickSearchContent();
    setErrorMessage(jsonResponse.message);
}