package edu.uci.ics.huanjial.service.api_gateway.configs;

import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.GatewayService;

public class MovieConfigs {
    // Movie service configs
    private String scheme;
    private String hostName;
    private int port;
    private String path;

    // Movie endpoints
    private String EPMovieSearch;
    private String EPMovieGet;
    private String EPMovieAdd;
    private String EPMovieDelete;
    private String EPGenreGet;
    private String EPGenreAdd;
    private String EPGenreMovie;
    private String EPStarSearch;
    private String EPStarGet;
    private String EPStarAdd;
    private String EPStarIn;
    private String EPRating;

    public MovieConfigs() { }

    public MovieConfigs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException(GatewayService.ANSI_RED + "Unable to create Configs from ConfigsModel." + GatewayService.ANSI_RESET);
        }

        scheme = cm.getMoviesConfig().get("scheme");
        if (scheme == null) {
            System.err.println(GatewayService.ANSI_RED + "IDM scheme not found in configuration file." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies] scheme: " + scheme);
        }

        hostName = cm.getMoviesConfig().get("hostName");
        if (hostName == null) {
            System.err.println(GatewayService.ANSI_RED + "IDM host name not found in configuration file." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies] hostName: " + hostName);
        }

        port = Integer.parseInt(cm.getMoviesConfig().get("port"));
        if (port == 0) {
            System.err.println(GatewayService.ANSI_RED + "No port found in configuration file." + GatewayService.ANSI_RESET);
        } else if (port < GatewayConfigs.MIN_SERVICE_PORT || port > GatewayConfigs.MAX_SERVICE_PORT) {
            System.err.println(GatewayService.ANSI_RED + "Port is not within valid range." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies] port: " + port);
        }

        path = cm.getMoviesConfig().get("path");
        if (path == null) {
            System.err.println(GatewayService.ANSI_RED + "IDM path not found in configuration file." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies] path: " + path);
        }

        // ======================================= Endpoints ===========================================

        EPMovieSearch = cm.getMoviesEndpoints().get("EPMovieSearch");
        if (EPMovieSearch == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/search found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Search Movie: " + EPMovieSearch);
        }

        EPMovieGet = cm.getMoviesEndpoints().get("EPMovieGet");
        if (EPMovieGet == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/{movieid} found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Get Movie: " + EPMovieGet);
        }

        EPMovieAdd = cm.getMoviesEndpoints().get("EPMovieAdd");
        if (EPMovieAdd == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/add found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Add Movie: " + EPMovieAdd);
        }

        EPMovieDelete = cm.getMoviesEndpoints().get("EPMovieDelete");
        if (EPMovieDelete == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/delete found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Delete Movie: " + EPMovieDelete);
        }

        EPGenreGet = cm.getMoviesEndpoints().get("EPGenreGet");
        if (EPGenreGet == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/genre found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Get All Genres: " + EPGenreGet);
        }

        EPGenreAdd = cm.getMoviesEndpoints().get("EPGenreAdd");
        if (EPGenreAdd == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/genre/add found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Add Genre: " + EPGenreAdd);
        }

        EPGenreMovie = cm.getMoviesEndpoints().get("EPGenreMovie");
        if (EPGenreMovie == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/genre/{movieid} found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Get All Genres For Movie: " + EPGenreMovie);
        }

        EPStarSearch = cm.getMoviesEndpoints().get("EPStarSearch");
        if (EPStarSearch == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/star/search found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Search Star: " + EPStarSearch);
        }

        EPStarGet = cm.getMoviesEndpoints().get("EPStarGet");
        if (EPStarGet == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/star/{starid} found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Get Star: " + EPStarGet);
        }

        EPStarAdd = cm.getMoviesEndpoints().get("EPStarAdd");
        if (EPStarAdd == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/star/add found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Add Star: " + EPStarAdd);
        }

        EPStarIn = cm.getMoviesEndpoints().get("EPStarIn");
        if (EPStarIn == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/star/starsin found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Add Star to Movie: " + EPStarIn);
        }

        EPRating = cm.getMoviesEndpoints().get("EPRating");
        if (EPRating == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/movies/EPRating found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Movies][EP] Update Movie Rating: " + EPRating);
        }
    }

    public void currentConfigs() {
        ServiceLogger.LOGGER.config("[Movies] scheme: " + scheme);
        ServiceLogger.LOGGER.config("[Movies] hostname: " + hostName);
        ServiceLogger.LOGGER.config("[Movies] port: " + port);
        ServiceLogger.LOGGER.config("[Movies] path: " + path);
        ServiceLogger.LOGGER.config("[Movies][EP] Search Movie: " + EPMovieSearch);
        ServiceLogger.LOGGER.config("[Movies][EP] Get Movie: " + EPMovieGet);
        ServiceLogger.LOGGER.config("[Movies][EP] Add Movie: " + EPMovieAdd);
        ServiceLogger.LOGGER.config("[Movies][EP] Delete Movie: " + EPMovieDelete);
        ServiceLogger.LOGGER.config("[Movies][EP] Get All Genres: " + EPGenreGet);
        ServiceLogger.LOGGER.config("[Movies][EP] Add Genre: " + EPGenreAdd);
        ServiceLogger.LOGGER.config("[Movies][EP] Get All Genres For Movie: " + EPGenreMovie);
        ServiceLogger.LOGGER.config("[Movies][EP] Search Star: " + EPStarSearch);
        ServiceLogger.LOGGER.config("[Movies][EP] Get Star: " + EPStarGet);
        ServiceLogger.LOGGER.config("[Movies][EP] Add Star to Movie: " + EPStarIn);
        ServiceLogger.LOGGER.config("[Movies][EP] Update Movie Rating: " + EPRating);
    }

    public String getMoviesUri() {
        return scheme + hostName + ":" + port + path;
    }

    public String getEPMovieSearch() {
        return EPMovieSearch;
    }

    public String getEPMovieGet() {
        return EPMovieGet;
    }

    public String getEPMovieAdd() {
        return EPMovieAdd;
    }

    public String getEPMovieDelete() {
        return EPMovieDelete;
    }

    public String getEPGenreGet() {
        return EPGenreGet;
    }

    public String getEPGenreAdd() {
        return EPGenreAdd;
    }

    public String getEPGenreMovie() {
        return EPGenreMovie;
    }

    public String getEPStarSearch() {
        return EPStarSearch;
    }

    public String getEPStarGet() {
        return EPStarGet;
    }

    public String getEPStarAdd() {
        return EPStarAdd;
    }

    public String getEPStarIn() {
        return EPStarIn;
    }

    public String getEPRating() {
        return EPRating;
    }
}