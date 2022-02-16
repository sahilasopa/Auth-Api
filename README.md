
# Basic Authentication API

This project is currently a simple Authentication API and I am looking forward to make it much better

# Endpoints

* /api/v1/users [GET] 
    * Returns list of all users in database
* /api/v1/users/authenticate [POST]
    * Generates a new JWT token
    * Requires Username And Password In RequstBody
    * Returns JWT token
* /api/v1/users/register [POST]
    * used to register new users to database
    * Requires: 
        * Username [String]
        * Email [String]
        * Password [String]
    * Returns JWT token
* /api/v1/users/delete [DELETE]
    * deletes current logged in user (Based on token)
    * Requires JWT token in header
* /api/v1/users/profile [GET]
    * Returns profile of specefic user based on token 
    * Requires JWT token in header
* /api/v1/users/update [PUT]
    * Updates the details of user 
    * Requires: 
        * JWT token In Header
    * Optional
        * New Userame [String]
        * New Email [String]
        * New Password [String]
## How To Set It Up 
Create a new file `application.properties` inside `/src/main/resources/`

add the following fields

```
spring.datasource.url=jdbc:postgresql://localhost:{port}/{database name}
spring.datasource.username={username}
spring.datasource.password={password}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
server.error.include-message=always
jwt.secret={JWT secret token}
```

and now you are ready to run the project

## Technical Information
This projects uses postgresql as main database and currently only authenticates user based on username and password

