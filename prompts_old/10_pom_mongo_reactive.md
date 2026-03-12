You are @pom. Edit pom.xml:
- remove spring-boot-starter-data-jpa and org.postgresql:postgresql
- ensure spring-boot-starter-webflux is present
- add spring-boot-starter-data-mongodb-reactive
- keep validation, modelmapper, lombok
- keep spring-boot-starter-test (test) and add reactor-test (test)
Show a DIFF before applying. Apply only to pom.xml.