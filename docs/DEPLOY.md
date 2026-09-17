# Deploying the demo

The app runs entirely on an in-memory H2 database, rebuilt and reseeded on every
start (`DataInitializer`) — no external database, mail server, or file storage to
provision. That's the only mode this app has, so the free tier runs exactly what
you'd run locally.

## Sign in

Every seeded account's password equals its username.

| User | Password | Role |
|---|---|---|
| `admin` | `admin` | Administrator |
| `jugador` | `jugador` | Player (member) |

Other seeded accounts follow the same pattern (`recepcio`/`recepcio`,
`marina`/`marina`, etc.) — see `DataInitializer.java` for the full roster.

## Render (Docker blueprint)

1. Push this repo to GitHub.
2. Render dashboard → **New → Blueprint**, point it at the repo. It reads
   `render.yaml`: one free Docker web service, health check on `/login`.
3. Deploy. First build takes a few minutes (Maven downloads dependencies).
4. Paste the live URL into the root `README.md` and the profile README row.

The free tier sleeps after ~15 minutes idle; the first request after that takes
30–50 seconds while the container wakes and reseeds.

## Run it locally without Docker

```bash
./mvnw spring-boot:run
```

Then open <http://localhost:8086/login>. Requires JDK 21.

## Run it locally with Docker

```bash
docker build -t tennis-club-manager .
docker run --rm -p 8086:8086 tennis-club-manager
```
