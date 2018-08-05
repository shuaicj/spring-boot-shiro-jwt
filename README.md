# Spring Boot Shiro JWT

Example of integrating [Spring Boot](https://spring.io/projects/spring-boot) & [Apache Shiro](https://shiro.apache.org/)
& [JWT](https://jwt.io/introduction/) together to do the authentication/authorization stuff.

### Get Started

#### 1. Start the service
```bash
mvn spring-boot:run
```

#### 2. Get tokens
```bash
curl -i -H "Content-Type: application/json" -X POST -d '{"username":"alice","password":"alice-password"}' http://localhost:8080/login
```
You will see the token in response header for user `alice`. Note that the status code `401` will be returned
if you provide incorrect username or password.
See all predefined users in section [Users, Roles and Permissions](#users-roles-and-permissions).

#### 3. Access protected APIs

The general command to verify if the auth works is as follows:
```bash
curl -i -H "Authorization: Bearer token-you-got-in-step-2" http://localhost:8080/admin-only
```
or without token:
```bash
curl -i http://localhost:8080/admin-only
```

You can change the token and the URL as need. See all predefined URLs in section [APIs](#apis).
The response status code varies in different situations:

|                                            | public api | protected api |
| ------------------------------------------ | ---------- | ------------- |
| token valid and with enough authorities    | 200        | 200           |
| token valid and without enough authorities | 200        | 403           |
| token missing or invalid                   | 200        | 401           |

### Users, Roles and Permissions

The following users are defined in the demo. For details, see [schema.sql](src/main/resources/schema.sql)
and [data.sql](src/main/resources/data.sql).

| username | password       | roles         | permissions            |
| -------- | -------------- | ------------- | ---------------------- |
| alice    | alice-password | admin         | *                      |
| bob      | bob-password   | user          | files:read,write       |
| chris    | chris-password | file-operator | files:*                |
| david    | david-password | log-archiver  | files:read,archive:log |

> Note: the in-memory database called `H2` is used by default. It's very easy to switch to `MySQL` as need.
See [application.yml](src/main/resources/application.yml).

### APIs

##### Role Based APIs

|                | roles required |
| -------------- | -------------- |
| /admin-only    | admin          |
| /user-only     | user           |
| /admin-or-user | admin or user  |
| /public-to-all |                |

##### Permission Based APIs

|                | permissions required |
| -------------- | -------------------- |
| /read          | files:read           |
| /write         | files:write          |
| /archive       | files:archive        |
| /read-log      | files:read:log       |
| /write-log     | files:write:log      |
| /archive-log   | files:archive:log    |

### Reference
- [https://shiro.apache.org/spring-boot.html](https://shiro.apache.org/spring-boot.html)
