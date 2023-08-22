# CinemaFlix

## Description:
This is an application built in microservice architecture. It allows user to register/login, browse movie information, search movies, and buy movies. This application is deployed into two forms: web application and android application.

## Demo:
+ Web Application: https://drive.google.com/file/d/1udIQk3nUCZwPMeBweZGLizK8MuPK5rK5/view?usp=sharing
+ Android Application: https://drive.google.com/file/d/1wOmPEUTk5H2kPJ86ItMHVv9uZsaVO8oC/view?usp=sharing

## Technologies:
+ Programming language: Java
+ Frontend: JavaScript, CSS, HTML, Android Studio
+ Backend: Java, JDBC, MySQL, REST API

## Diagram:
Application Architecture Diagram <br>
![CinemaFlix Diagram](https://github.com/lianghuanjia/CinemaFlix/assets/36748450/ec912039-e6bb-40eb-ad05-67073e9b99af)

## Services Details:
### IDM Service: 
+ User Register/Login: User register using their username and password. The password is stored in database with Salt and Hashing using SHA256.
+ User Session Validation: User login will generate a session ID. The session ID will be stored in database with timestamp. Any user activity will check the user's session ID, if it's in valid time, the session's timestamp will be extended. If the session is expired, user will be forced to login again in order to use the application
### Movie Service:
+ Quick Movie Search: Search movies only through their names. The service will look up the movies that include the input string in the database and return those found movies to user
+ Advanced Movie Search: Search movies with other detailed fields, including: Genre, Title, Director, Published Year
### Billing Service:
+ Shopping Cart: User add their desired movies to their shopping cart, this shopping cart information will be stored in database
+ Credit Card
+ PayPal API Integration: 


