
# About
* This is a Car registration system implementation 

# Highlights

## Libraries, Frameworks and Plugins
* Dependencies are defined [here](https://github.com/hardmettle/autoapp/blob/master/build.sbt) and
plugins [here](https://github.com/hardmettle/autoapp/blob/master/project/plugins.sbt)
* Rest API based on [akka-http](http://doc.akka.io/docs/akka-http/10.0.5/scala/http/introduction.html);
* DB layer uses [PostgreSQL](https://www.postgresql.org/) and  [Slick](http://slick.lightbend.com/) with [HikariCP](https://github.com/brettwooldridge/HikariCP) connection pool as DB Access Layer and
[Flyway](https://flywaydb.org/) for DB schema management;
* For json (de)serialization two frameworks are used: [spray-json](https://github.com/spray/spray-json) and [play-json](https://www.playframework.com/documentation/2.5.x/ScalaJson);
* Testing layer uses: [scala test](http://www.scalatest.org/) for defining test cases, [scala mock](http://scalamock.org/) for mocking dependencies in unit tests and 
[akka-http-test-kit](http://doc.akka.io/docs/akka-http/10.0.5/scala/http/routing-dsl/testkit.html) for api tests;
* Plugins configured for the project are: [s-coverage](https://github.com/scoverage/sbt-scoverage) for code test coverage, [scala-style](http://www.scalastyle.org/) for code style checking and
[scala-iform](https://github.com/scala-ide/scalariform) for code formatting.

## Architecture/Design/Coding Principles
* Code follows the following programming concepts: 
  * OOP (Object Oriented Programming) principles of abstraction (all operations (meaning function, methods and procedures) and parameters are abstracted concepts of the business domain language (i.e. DDD Domain Driven Design) 
  found in the assignment description provided, encapsulation. Along with inheritance and polymorphism implementations as well
  * FP (Funtional Programming) principles of immutability (all data is immutable), higher order functions, pattern matching, monadic constructions, recursion;
  * Declarative style where operations describe what is done and not how;
  * TDD with [ScalaTest](http://www.scalatest.org/) the test were created before the implementation;
  * Mocking (with a tool like [ScalaMock](http://scalamock.org/)) - the db layer is mocked to only test the outer service layer in
  test [CarServiceTest](https://github.com/hardmettle/autoapp/blob/master/src/test/scala/com/scout24/cars/services/CarServiceTest.scala) 
  * Iterative Development that can be traced through git commit messages;
  * Intention Revealing Operations where all parts of the method contribute to the clarity: operation name, parameter names, parameter types, and operation return type;
  * Separation of concerns: each class/object/trait has clearly defined responsibilities that are fully testable and tested;
  * Meaningful commit and messages - commit per implemented feature, and on important code changes that might be usefull to revisit;
  * Layer architecture: Each outer layer can only access the layers from the same level or a level immediate bellow it;
  
  # API Behaviour
  It's behaviour is defined by the API Integration test found [here](https://github.com/hardmettle/autoapp/blob/master/src/test/scala/com/scout24/cars/services/CarServiceTest.scala)
  
  # Run Requirements
  * This is a scala sbt project, and was developed and tested to run with: Java 1.8, Scala 2.12.4 and Sbt 1.0.3
  * Database used is PostgreSQL;
  ## Configuration
  * Create database in PostgresSQL 
  * Set database settings on application config 
    ### Changing application config
    There are two config files. Application config `src/main/resources/application.conf` and test config `src/test/resources/application.conf`.
   ## Run application
    To run application, call:
    ```
    sbt run
    ```
    If you want to restart your application without reloading of sbt, use:
    ```
    sbt re-start
    ```
    ## To run tests
     `sbt testOnly`
    
    ### Sample working of application
      
* To see application working use CLI with `cURL` or application like postman to use URL scheme for eg:
   `localhost:9000/v1/car/id/1`  in  `POST,PATCH,GET,DELETE` mode and provide http header `Content-Type:application/json`.
* Payload is accepted in body for modes `POST,PATCH` as `JSON` for eg for `POST`:
```aidl
        {
            
            "id":1,
            "title":"abc",
            "fuel":{"name":"gas", "renewable":false},
            "price":100,
            "used":true,
            "mileage":103,
            "registration":"10/12/2017"
            
        }
```
for `PATCH`

```aidl
    {
        "title":"pqr",
    }
```

* URL scheme to fetch all records is :
    `localhost:9000/v1/car/all/<INSERT KEYWORD IF ORDERING BY ANY OTHER PARAMETER>`
    
    eg: `localhost:9000/v1/car/all/registration`
   
