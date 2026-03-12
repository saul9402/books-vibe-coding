You are @model. For each JPA entity in src/main/java/**/model:
- @Entity/@Table -> @Document("<plural-lowercase>")
- javax.persistence.Id -> org.springframework.data.annotation.Id
- remove @GeneratedValue; id is String
- Keep validation; consider @Indexed for frequently queried fields (like name/title)
Batch in groups of 2-3 files, showing DIFF for each. Do not touch DTOs.