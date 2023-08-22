function getLoginContent() {
    clearMainAndErrorMessage();

    let loginForm = "<div class = \"LoginBlock\">  " +
        "      <label> Email </label>\n" +
        "        <input type = \"text\", id = \"loginEmail\", placeholder = \"Enter your email\">\n" +
        "        <label> Password </label>\n" +
        "        <input type = \"password\", id = \"loginPassword\", placeholder = \"Enter your password\">\n" +
        "        <button class = \"LoginButton\" onclick='handleLogin()'> Login </button>" +
        "</div>";
    $(".main").append(loginForm);
}

function getRegisterContent(){
    clearMainAndErrorMessage();

    let registerForm = "    <div class = \"RegisterBlock\">\n" +
        "        <label> Email </label>\n" +
        "        <input type = \"text\", id = \"registerEmail\", placeholder = \"Enter your email\">\n" +
        "        <label> Password </label>\n" +
        "        <input type = \"password\", id = \"registerPassword\", placeholder = \"Enter your password\">\n" +
        "        <button class = \"RegisterButton\" onclick='handleRegister()'> Register </button>\n" +
        "    </div>"

    $(".main").append(registerForm);
}

function getBrowseContent(){
    clearMainAndErrorMessage();

    let browseForm = "<div class = \"BrowseBlock\">\n" +
        "    <select>\n" +
        "    <option value = \"title\"> title </option>\n" +
        "    <option value = \"genre\"> genre </option>\n" +
        "    </select>\n" +
        "    <button class = \"BrowseButton\"> Browse </button>\n" +
        "    </div>"

    $(".main").append(browseForm);
}

function getQuickSearchContent(){
    clearMainAndErrorMessage();

    let quickSearchForm = "    <div class = \\\"QuickSearchBlock\\\">\n" +
        "        <label> Movie Title: </label>\n" +
        "        <input type = \"text\", id = \"QuickSearchTitle\", placeholder = \"Enter movie title\">\n" +
        "        <button class = \"QuickSearchButton\" onclick=\"clickSearch()\"> Search </button>\n" +
        "\n" +
        "        <div class = \"eachSelection\">\n" +
        "            <label>Order By</label>\n" +
        "            <select id = \"orderby\" onchange=\"clickSearch()\">\n" +
        "                <option label = \"orderby\">orderby</option>\n" +
        "                <option label = \"title\">title</option>\n" +
        "                <option label = \"rating\">rating</option>\n" +
        "            </select>\n" +
        "        </div>\n" +
        "        <div class = \"eachSelection\">\n" +
        "            <label>direction</label>\n" +
        "            <select id = \"direction\" onchange=\"clickSearch()\">\n" +
        "                <option label = \"direction\">direction</option>\n" +
        "                <option label = \"asc\">asc</option>\n" +
        "                <option label = \"desc\">desc</option>\n" +
        "            </select>\n" +
        "        </div>\n" +
        "        <div class = \"eachSelection\">\n" +
        "            <label>limit</label>\n" +
        "            <select id = \"limit\" onchange=\"clickSearch()\">\n" +
        "                <option label = limit>limit</option>\n" +
        "                <option label = 10>10</option>\n" +
        "                <option label = 25>25</option>\n" +
        "                <option label = 50>50</option>\n" +
        "                <option label = 100>100</option>\n" +
        "            </select>\n" +
        "        </div>\n" +
        "    </div>";

    $(".main").append(quickSearchForm);
}


function getAdvancedSearchContent(){
    clearMainAndErrorMessage();

    let advancedSearchForm = "<div class = \"AdvancedSearchBlock\">\n" +
        "        <div class = \"AdvancedTextInputBlock\">\n" +
        "            <div>\n" +
        "                <label> Title </label>\n" +
        "                <input type = \"text\", id = \"AdvancedSearchTitle\" , placeholder = \"Enter movie title\" >\n" +
        "            </div>\n" +
        "            <div>\n" +
        "                <label> Genre </label>\n" +
        "                <input type = \"text\", id = \"AdvancedSearchGenre\", placeholder = \"Enter movie genre\" >\n" +
        "            </div>\n" +
        "            <div>\n" +
        "                <label> Year </label>\n" +
        "                <input type = \"text\", id = \"AdvancedSearchYear\", placeholder = \"Enter movie year\" >\n" +
        "            </div>\n" +
        "            <div>\n" +
        "                <label> Director </label>\n" +
        "                <input type = \"text\", id = \"AdvancedSearchDirector\", placeholder = \"Enter movie director\" >\n" +
        "            </div>\n" +
        "            <div>\n" +
        "                <label> Hidden </label>\n" +
        "                <input type = \"text\", id = \"AdvancedSearchHidden\", placeholder = \"Enter movie hidden attribute\" >\n" +
        "            </div>\n" +
        "        </div>\n" +
        "\n" +
        "        <div class = \"AdvancedSelectionBlock\">\n" +
        "            <div class = \"eachSelection\">\n" +
        "                <label>Order By</label>\n" +
        "                <select id = \"orderby\" onchange=\"AdvancedSearch()\">\n" +
        "                   <option label = \"orderby\">orderby</option>\n" +
        "                    <option label = \"title\">title</option>\n" +
        "                    <option label = \"rating\">rating</option>\n" +
        "                </select>\n" +
        "            </div>\n" +
        "            <div class = \"eachSelection\">\n" +
        "                <label>Direction</label>\n" +
        "                <select id = \"direction\" onchange=\"AdvancedSearch()\">\n" +
        "                   <option label = \"direction\">direction</option>\n" +
        "                    <option label = \"asc\">asc</option>\n" +
        "                    <option label = \"desc\">desc</option>\n" +
        "                </select>\n" +
        "            </div>\n" +
        "            <div class = \"eachSelection\">\n" +
        "                <label>Limit</label>\n" +
        "                <select id = \"limit\" onchange=\"AdvancedSearch()\">\n" +
        "                   <option label = limit>limit</option>\n" +
        "                    <option label = 10>10</option>\n" +
        "                    <option label = 25>25</option>\n" +
        "                    <option label = 50>50</option>\n" +
        "                    <option label = 100>100</option>\n" +
        "                </select>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "        <div>\n" +
        "            <button class = \"searchButton\" onclick='AdvancedSearch()'> Search </button>\n" +
        "        </div>\n" +
        "\n" +
        "    </div>\n"

    $(".main").append(advancedSearchForm);
}


function setMovieListContent(res) {
    // Manually build the HTML Table with the response
    let rowHTML = "<table id = \"movies\" border=\"1\" width='100%'><tr><td>Movie ID</td><td>Title</td><td>Director</td><td>year</td><td>rating</td><td>number of votes</td>";
    let movieList = res.movies;

    for (let i = 0; i < movieList.length; ++i) {
        rowHTML += "<tr>";
        let movieObject = movieList[i];

        rowHTML += "<td>" + movieObject["movieId"] + "</td>";
        rowHTML += "<td>" + movieObject["title"] + "</td>";
        rowHTML += "<td>" + movieObject["director"] + "</td>";
        rowHTML += "<td>" + movieObject["year"] + "</td>";
        rowHTML += "<td>" + movieObject["rating"] + "</td>";
        rowHTML += "<td>" + movieObject["numVotes"] + "</td>";
        rowHTML += "</tr>";
    }
    rowHTML += "</table>";

    $(".movies").append(rowHTML);

    let movies = $("#movies");

    movies.find("tr").each(function () {
        try{
            let eachMovieId = this.cells[0];
            this.onclick = function () {
                console.log("The movie ID is: " + eachMovieId.innerText);
                searchByMovieID(eachMovieId.innerText);
            }
        }
        catch (e) {
            console.log(e);
            setErrorMessage("In the end of setMovieListContent, "+e);
        }
    })
}

function setDetailedMovieContent(res){
    $(".movies").empty();

    $(".AddToCartInfo").empty();
    let cartInfo = "        <div>\n" +
        "            <button class = \"AddToCartButton\">\"Add To Cart\"</button>\n" +
        "        </div>\n" +
        "        <div>\n" +
        "            <input type=\"text\" id=\"Quantity\" placeholder=\"Enter the quantity\">\n" +
        "        </div>";
    $(".AddToCartInfo").append(cartInfo);

    console.log("In setDetailedMovieContent");
    console.log(res.movie);
    let movie = res.movie;
    let rowHTML = "<table id = \"movies\" border=\"1\" width='100%'>";


    rowHTML += "<tr>" + "<td>" + "Title:  " + "</td>" + "<td>" + movie.title + "</td>" + "</tr>";
    rowHTML += "<tr>" + "<td>" + "Director:  " + "</td>" + "<td>" + movie.director + "</td>" + "</tr>";
    rowHTML += "<tr>" + "<td>" + "Rating:  " + "</td>" + "<td>" + movie.rating + "</td>" + "</tr>";
    rowHTML += "<tr>" + "<td>" + "Number of votes:  " + "</td>" + "<td>" + movie.numVotes + "</td>" + "</tr>";
    rowHTML += "<tr>" + "<td>" + "Year:  " + "</td>" + "<td>" + movie.year + "</td>" + "</tr>";
    rowHTML += "<tr>" + "<td>" + "Overview:  " + "</td>" + "<td>" + movie.overview + "</td>" + "</tr>";



    let starTable = "<table id = \"starTable\">";
    let starList = movie.stars;

    for (let i = 0; i < starList.length; ++i) {
        starTable += "<tr align='center'>";
        let starObject = starList[i];

        starTable += "<td align='center'>" + starObject["name"] + "</td>";

        starTable += "</tr>";
    }
    starTable += "</table>";

    rowHTML += "<tr align='center'>" + "<td align='center'>" + "Stars:  " + "</td>" + "<td>" + starTable + "</td>" + "</tr>";

    let genreTable = "<table id = \"genreTable\">";
    let genreList = movie.genres;

    for (let i = 0; i < genreList.length; ++i) {
        genreTable += "<tr align='center'>";
        let genreObject = genreList[i];

        genreTable += "<td align='center'>" + genreObject["name"] + "</td>";

        genreTable += "</tr>";

    }
    genreTable += "</table>";

    rowHTML += "<tr align='center'>" + "<td align='center'>" + "Genres:  " + "</td>" + "<td>" + genreTable + "</td>" + "</tr>";

    rowHTML += "</table>";

    $(".movies").append(rowHTML);

    let movieID = movie.movieId;

    console.log("MovieID: " + movieID);

    $(".AddToCartButton").click(function(){
        console.log("About to call addToCart");
        addToCart(movieID);
        console.log("Finish calling addtocar");
    });
}

function getShoppingCartContent(request){
    clearMainAndErrorMessage();
    console.log(request);
}
