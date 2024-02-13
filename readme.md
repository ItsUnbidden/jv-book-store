##Table of contents
- [Introduction](#introduction)
- [Technologies](#technologies)
- [Usage](#usage)
- [Configuration](#configuration)
- [Installation](#installation)
- [Issue](#issue)

## Introduction
**The goal** of this project was to implement a **stateless java server** for managing an **online book store**. Here are some of the server's capabilities:
 - Authentication using JWT
 - Role system, allowing admins to manage the store
 - Browsing of books using different options and filters
 - A shopping cart for users to add their books into
 - Ordering system
 - Swagger UI for integration into other projects and trying out the server

**You can watch an overview of the project [here](https://www.loom.com/share/4fec6d3c938b43a7874a692785b5d1b6?sid=434edd89-7879-4286-bc33-554aa2f8f9d8).**
## Technologies
The project is built using mainly **Spring Framework**. Here is a complete list:
 - Spring Boot
 - Spring Security
 - Spring Boot Web
 - Spring Data JPA
 - JWT 
 - Lombok
 - Mapstruct
 - Swagger
 - MySQL 
 - Liquibase
 - Docker
 - Docker Testcontainers using MySQL
## Usage
 Here is a quick flowchart on what users and admins can do:
![](./images/Bookstore%20roles%20diagram%20(2).png)

**Authentication controller**
This controller is available for everyone — even for **non-authenticated** users. There is always a **default user** created during the initialization of the database. It will always have *admin* role and it's credentials can be configured.
*Available endpoints:*
 - POST: /login — accepts user credentials (email, password) and returns a JWT for authentication.
 - POST: /register — accepts user details (name, address, etc.) and credentials. Returns a newly created user.

All inputted data is **verified** during these requests, so emails must follow the email pattern, and passwords must match. If request is invalid, **response code 400** will be thrown.

Some examples:

*Login request:*
![](./images/loginRequest.png)

Response will look something like this: `{"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvd25lckBib29rc3RvcmUuY29tIiwiaWF0IjoxNzA3MzI2ODg5LCJleHAiOjE3MDczMjg2ODl9.4_2yXaHkGMxtpi14Jzsvi9kET4Lis_OdxlAOnurys-ha6Bfn_t6vJnU1fD9DjCvmg1PGTq1a3_RiahhiHQ83PQ"}`

*Registration request:*
![](./images/registrationRequest.png)
*Registration response:*
![](./images/RegistrationResponse.png)
For all endpoints in other controllers authentication is **required**. If **no token** is sent with the requests, **response code 401** will be thrown.

Most of the other controllers have two different types of endpoints: **for customers** and **for admins**. **Customers** can **see** available books or categories, interact with their own shopping cart and orders while **admins** can **add** new items to the shop, **change** or **delete** them, see registered users, and give roles. 
Trying to access admin only endpoints without proper authorization will result in **response code 403**.

**Book controller**
This controller is responsible for browsing and managing the books in the store.

*Available endpoints:*
 - GET: /books — Lists all available books. Supports pagination.
 - GET: /books/{id} — Lists one specific book by it's id.
 - GET: /books/search/{params} — Searches books using two parameters: *price* and *author*. Supports pagination.
These endpoints are for **admins only**:
 - POST: /books — Creates a new book.
 - DELETE: /books/{id} — Soft-deletes a book by it's id.
 - PUT: /books/{id} — Updates a book by it's id.

Some examples:

*Create book request:*
![](./images/createBookRequest.png)

*Create book response:*
![](./images/createBookResponse.png)

*List of books:*
![](./images/listOfBooks.png)

*Search books parameters:*
![](./images/searchBooksParams.png)

*List of books by params:*
![](./images/searchBooksResponse.png)

**Category controller**
This controller is responsible for browsing and managing available book categories, as well as searching books within a specific category.

*Available endpoints:*
 - GET: /categories — Lists all available categories. Supports pagination.
 - GET: /categories/{id} — Lists one specific category by it's id.
 - GET: /categories/{id}/books — Searches books within *specific category*, specified by it's id. Supports pagination.
These endpoints are for **admins only**:
 - POST: /categories — Creates a new category.
 - DELETE: /categories/{id} — Soft-deletes a category by it's id.
 - PUT: /categories/{id} — Updates a category by it's id.
 
 Some examples:

*Create category request:*
![](./images/createCategoryRequest.png)

*Create category response:*
![](./images/createCategoryResponse.png)

*Search books by category 1:*
![](./images/category1BooksResponse.png)
(When you search books by category, categories are not shown in the response)

**Shopping cart controller**
This controller is responsible for managing user's shopping cart. Shopping cart is created during user registration and cannot be deleted, only altered. 
Shopping cart operates using **cart items**. One cart item represents a certain **type of book** with a desired **quantity** of this book.

*Available endpoints:*
 - GET: /cart — Shows shopping cart object, including all contained inside cart items.
 - POST: /cart — Adds a new cart item to user's shopping cart.
 - DELETE: /cart/cart-items — Removes all cart items from user's shopping cart.
These endpoints have a **restriction** in place: only cart items that **belong to the user** can be changed by them. Admins, however, **are not bound** by this restriction and can change any cart item providing they know its id:
 - PUT: /cart/cart-items/{id} — Changes the quantity in a certain cart item specified by it's id. 
 - DELETE: /cart/cart-items/{id} — Removes a cart item from user's shopping cart.
 
*Add book to shopping cart request:*
![](./images/addBookToSCRequest.png)

*Add book to shopping cart response:*
![](./images/addBookToSCResponse.png)

*Show shopping cart:*
![](./images/showShoppingCartResponse.png)

**Order controller**
This controller is responsible for managing user's orders. Orders are tied to the shopping cart, since when user places an order, that order will contain **all items that were present in their shopping cart** and the cart will be **cleared**.

*Available endpoints:*
 - POST: /orders — Places an order.
 - GET: /orders — Shows all user's orders.  Supports pagination.
 - PATCH: /orders/{id} — Updates order status. **Admin only.**
These endpoints have a **restriction** in place: only order items that **belong to the user** can be viewed by them. Admins, however, **are not bound** by this restriction and can view any order item providing they know required ids:
 - GET: /orders/{id}/items — Shows all orders items for a specific order by it's id.
 - GET: /orders/{orderId}/items/{itemId} — Shows a specific order item from a specific order. Both are found by their respective ids.
 
*Place order:*
![](./images/orderResponse.png)
(Takes all items from the shopping cart)

*Change order status request:*
![](./images/setStatusRequest.png)

*Change order status response:*
![](./images/setStatusResponse.png)

**User controller**
This controller is available exclusively for admins and is used to manage registered users.

*Available endpoints:*

 - GET: /users — Shows registered users. Passwords are encrypted and are not shown.
 - PATCH: /users/roles/{id} — Changes role set for a specific user.

*List users:*
![](./images/getUsersResponse.png)

*Set roles for user request:*
![](./images/setRolesRequest.png)

*Set roles for user response:*
![](./images/setRolesResponse.png)

**Swagger UI**
Using `/swagger-ui/index.html` endpoint, you can try out the server using pretty Swagger UI:
![](./images/SwaggerUI.png)

## Configuration
To configure the server you might need to change a few files, such as:
1. `application.properties` — sets several crucial variables for Spring Framework to use. Also provides settings for JWT.
2. `liquibase.propeties` — required to change only when Docker is not used. Sets database connection properties.
3. `06-insert-owner.yaml` — you need to change this file to set your own credentials for the main user. Without changing these, security of your server may be compromised.
4. `.env` — if you are using Docker, you may want to change some settings here.

***application.properties***
*Path to the file:*
![](./images/applicationPropertiesPath.png)

Mandatory JWT settings:
 - `jwt.expiration` determines how long one token will be active for in milliseconds. 
 - `jwt.secret` sets the secret key used in forming tokens. The key is supposed to be quite long.

If Docker is not used:
 - `spring.datasource.url` — sets database URL to connect to.
 - `spring.datasource.username` — sets username for database user.
 - `spring.datasource.password` — sets password for database.
 - `spring.datasource.driver-class-name` — sets driver for your database. In case of MySQL: `com.mysql.cj.jdbc.Driver`

*Example:*
![](./images/springFile.png)

***liquibase.properties***
*Path to the file:*
![](./images/liquibasePropertiesPath.png)

Change only if Docker is not used, otherwise nothing will happen:

 - `url` — sets database URL to connect to.
 - `username` — sets username for database user.
 - `password` — sets password for database user.

*Example:*
![](./images/liquibaseFile.png)

***06-insert-owner.yaml***
*Path to the file:*
![](./images/ownerPath.png)

You can change any of the four values as shown on the screenshot below, but the most important ones are email and password. You will use these credentials to login into the main admin account.
Password must be encrypted first, you can use any online service that hashes text using BCrypt algorithm (10 rounds). For example, [this](https://www.browserling.com/tools/bcrypt) one.

*Be aware, that at the moment, these creadentials cannot be changed after first DB initialization. You will have to alter the DB directly.*

*Example:*
![](./images/ownerFile.png)

***.env***
*Path to the file:*
![](./images/envFilePath.png)

This file sets constants for Docker virtual environment. These are not necessary to alter, even if you are using Docker.

 - `MYSQL_PASSWORD` — password for MySQL database.
 - `MYSQL_DATABASE` — name for the database.
 - `MYSQL_LOCAL_PORT` — port, on which the database will be available for access.
 - `MYSQL_DOCKER_PORT` — port, on which the database is running inside Docker.
 - `SPRING_LOCAL_PORT` — port, on which the server will be available for access.
 - `SPRING_DOCKER_PORT` — port, on which the server is running inside Docker.
 - `DEBUG_PORT` — port, on which remote debug function is available.

*Example:*
![](./images/envFile.png)

## Installation
First you need to compile the project. You must have **Docker**, **Maven** and **JDK 17** installed on your device in order for this to work.
1. Download the repository and unzip it into a directory.
2. Configure **all files** as it was shown in the previous section.
3. If you want to launch the server using **Docker**, then you can **skip** this step. Otherwise, you need to **remove Docker dependency** so that application starts without it.
*pom.xml is in the root directory:*
![](./images/dockerDependency.png)

1. Open terminal in the project's directory.
2. Use `mvn clean package` in order to build the project. **You must have Docker opened during this process regardless of whether you want to use it in the end or not**, since testcontainers will be created.

When all tests pass and the project is compiled, you can **launch** the server.

If you are using **Docker**:
Use `docker-compose build` to create a container. After that is done, you can use `docker-compose up` or **Docker app** to launch it.
Server will be accessible at port, specified in the `.env` file.

If you are launching **without Docker**:
**Ensure that all files are configured properly and the database is running**. After that use `java -jar jv-book-store-0.0.1-SNAPSHOT.jar` in terminal opened at `<project_folder>/target` directory. Server will be available for access at default `8080` port.

*It is recommended to launch with Docker, since it is more reliable and requires fewer changes to the configuration.*

## Issue
Although most of the features are implemented, the project is still in **development**. If you found some bug or problem, please [raise an issue](https://github.com/ItsUnbidden/jv-book-store/issues/new).