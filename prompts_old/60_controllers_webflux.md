You are @web. Convert controllers to WebFlux:
- Return Mono/Flux
- Create/update WebClient bean if RestTemplate was used
- Keep @Valid; add basic @Slf4j logs
- ResponseEntity<Mono<T>> for create/update with proper status codes
Show DIFF per controller; 1 controller per run.