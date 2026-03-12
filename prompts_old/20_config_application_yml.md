You are @config. Create or edit application.yml:
- remove spring.datasource.*, hikari and jpa properties
- add:
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mitobooks
      database: mitobooks
logging:
  level:
    org.springframework.data.mongodb: INFO
Show DIFF before applying. Only config files.