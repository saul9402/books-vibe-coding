You are @infra. Migrate the project's infrastructure from PostgreSQL (JPA) to MongoDB Reactive.

**Scope**
- Work on the following files if present in the repository root:
  - `docker-compose.yml`
  - `.env`
  - `src/main/resources/application.yml` (or `.properties` if used)

---

**Docker Compose Migration**
- Open `docker-compose.yml` and:
  - Remove the existing `postgres_db` service and any `postgres` image references.
  - Add a new service called `mongo_db` with:
    - `image: mongo:7`
    - `container_name: mongo_db_iad`
    - `restart: unless-stopped`
    - Port mapping: `${MONGO_LOCAL_PORT}:27017`
    - Environment variables:
      - `MONGO_INITDB_ROOT_USERNAME=${MONGO_INITDB_ROOT_USERNAME}`
      - `MONGO_INITDB_ROOT_PASSWORD=${MONGO_INITDB_ROOT_PASSWORD}`
      - `MONGO_INITDB_DATABASE=${MONGO_INITDB_DATABASE}`
    - Volume mapping: `mongo-data:/data/db`
  - Remove any healthcheck or Postgres volume configuration.
  - Keep the `volumes:` section at the bottom and ensure `mongo-data:` is defined.

Example target structure:
```yaml
version: "3.8"
services:
  mongo_db:
    container_name: mongo_db_iad
    image: mongo:7
    restart: unless-stopped
    ports:
      - "${MONGO_LOCAL_PORT}:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: ${MONGO_INITDB_ROOT_USERNAME}
      MONGO_INITDB_ROOT_PASSWORD: ${MONGO_INITDB_ROOT_PASSWORD}
      MONGO_INITDB_DATABASE: ${MONGO_INITDB_DATABASE}
    volumes:
      - mongo-data:/data/db

volumes:
  mongo-data: