You are @repo. For files in src/main/java/**/repo:
- JpaRepository -> ReactiveMongoRepository<Domain, String>
- Convert methods to Mono/Flux
- Derived queries only; if @Query needed, use Mongo query JSON
Show DIFF. Batch by 2 files per run.