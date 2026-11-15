# NovaBank - Banking & Loan Management System (SGA 2, Project 9)

Spring Boot 3 + JPA/Hibernate + MySQL + JSP/JSTL + jQuery/AJAX, MVC layered.

## Prerequisites
JDK 17+ (21 works), Maven 3.9+, MySQL 8 running locally.

## Run
1. Edit `src/main/resources/application.properties` (MySQL username/password).
2. `mvn spring-boot:run`  (Hibernate creates the tables in database `bank_db` on first start).
3. Stop the app, load demo data: `mysql -u root -p < sql/seed.sql`, then start again.
4. Open http://localhost:8080/

## Run the tests (use H2, no MySQL needed)
`mvn test`

## Layout
```
controller/  web layer (MVC controllers + ApiController for AJAX JSON)
service/     business rules and @Transactional operations
repository/  Spring Data JPA repositories and custom JPQL queries
model/       JPA entities
dto/         form objects with validation
exception/   BusinessRuleException, ResourceNotFoundException, GlobalExceptionHandler
util/        EmiCalculator, PageUtil
src/main/webapp/WEB-INF/jsp   views (JSP + JSTL), common/ has header, footer, pager
src/main/resources/static     css/style.css, js/app.js (jQuery validation + AJAX)
sql/seed.sql                  demo data
```
