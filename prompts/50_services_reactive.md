You are @service. Convert services to reactive:
- List -> Flux, Optional/Entity -> Mono
- Replace blocking repo calls with reactive repos
- If you must call blocking code, wrap with Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic()) and add TODO to remove later
Show DIFF per file; 2 files per run.