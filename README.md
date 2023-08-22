# CinemaFlix

## Description:
This is an application built in microservice architecture. It allows user to register/login, browse movie information, search movies, and buy movies. This application is deployed into two forms: web application and android application.

## Demo:
### Web Applicaiton
+ Register and Login:
![5ae96b77-5593-4571-aba1-12809cb1cc76](https://github.com/lianghuanjia/CinemaFlix/assets/36748450/c8b86546-9ab4-457c-ac8d-6720fd2b622a)

+ Quick Search + Advanced Search + Add Desired Movies to Cart:
![9a296da2-b2eb-4099-bb58-730624b560d3](https://github.com/lianghuanjia/CinemaFlix/assets/36748450/668ecb35-5429-4f5e-8bc8-eb555cc449fa)
+ Sort searching results:
![6fc309dc-c889-4708-832c-ece01e07bb75](https://github.com/lianghuanjia/CinemaFlix/assets/36748450/0e84b69a-aa26-4c3b-ba8c-57bf25b3dc6a)
+ Checkout Shopping Cart with PayPal API:
![5af54717-1308-4070-9891-7bde82f82b7e](https://github.com/lianghuanjia/CinemaFlix/assets/36748450/6f5565c2-8a05-4a02-b7ea-9b474dee2fdb)
### Android Application:
+ Android login:
![301293d5-2ab7-43d8-93f8-953c088ed4b3](https://github.com/lianghuanjia/CinemaFlix/assets/36748450/0c607fa7-779e-4fab-b803-1c575e21e7b2)

### Full length demo videos:
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
### API Gateway Service:
A service that is an entry point for all users' requests. It will receive a user's request first, then validate the user using IDM Service. After validating user successfully, it will direct users' queries to corresponding microservices, get the responses back, and send the responses back to the corresponding users
+ Handle Client Requests: It receives client requests and put them into a queue. There are multiple workers that handle the requests from the queue with the help of multithreading.
+ Send Response back to user: Each request has a transactionID and the user's sessionID, the API Gateway will send the responses to corresponding users based on the transactionIDs and users' sessionIDs.
### IDM Service: 
A service that handles user register/login, session validation
+ User Register/Login: User register using their username and password. The password is stored in database with Salt and Hashing using SHA256.
+ User Session Validation: User login will generate a session ID. The session ID will be stored in database with timestamp. Any user activity will check the user's session ID, if it's in valid time, the session's timestamp will be extended. If the session is expired, user will be forced to login again in order to use the application
### Movie Service:
A service that handles movie searching, movie information browsing queries
+ Quick Movie Search: Search movies only through their names. The service will look up the movies that include the input string in the database and return those found movies to user
+ Advanced Movie Search: Search movies with other detailed fields, including: Genre, Title, Director, Published Year
### Billing Service:
A service that handles queries related to transaction, including credit card input, shopping cart update, complete transaction with PayPal API
+ Shopping Cart: Users add their desired movies to their shopping cart, and it updates the users' shopping cart information in database
+ Credit Card Insert: Add user's credit card to database and use it to finish transaction 
+ Transaction with PayPal API Integration: Implement Transaction using PayPal's API


