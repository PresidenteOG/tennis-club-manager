# Architecture

A server-rendered Spring Boot MVC app. The browser gets HTML built by Thymeleaf and there is
no JavaScript framework. Two features reach past that and call JSON endpoints with `fetch`:
the admin dashboard widgets hit `/api/dashboard/*` (court occupancy, member count) and the
bulk-mail form posts to `/api/mail/*` (`MailRestController`, a `@RestController` with
send-massive / send-to-category / send-to-user). That is the whole API surface — everything
else is a form post that returns a rendered page.

## The layers

```
Browser
  │  form posts, link clicks
  ▼
controller/            @Controller classes, one per area
  │  calls a *Service
  ▼
service/               interfaces + *Impl, the business rules live here
  │  calls a *DAO / *Repository
  ▼
dao/  repository/       Spring Data JPA interfaces — the only code that hits the DB
  │
  ▼
domain/                the JPA entities Hibernate maps to H2 tables
```

Spring Security sits in front of the controller layer as a servlet filter chain
(`springsecurity/SecurityConfig`). `CustomUserDetailsService` loads a `Usuario` by username
and hands Spring its roles; the filter chain then decides per-URL what `ROLE_ADMIN` /
`ROLE_JUGADOR` may reach. Passwords are BCrypt.

## How a request crosses the layers

Loading the match list, `GET /partidos`:

1. `PartidosController.listarPartidos` takes the optional filter params from the query
   string.
2. It calls `partidoService.findFiltered(...)`, which builds the query.
3. `PartidoService` goes through `PartidoRepository` (a `JpaRepository`) — a `@Query` with
   the filter predicates.
4. The returned `Partido` entities, plus the category and competition lists for the filter
   dropdowns, go into the `Model`.
5. `partidos/lista.html` renders. Lazy associations on the entities (the squad, the
   captain) are read during rendering — open-session-in-view is on, so the Hibernate
   session is still open at that point.

## Where it is not perfectly clean

Worth knowing before reading the code, and the kind of thing a review would flag:

- A few controllers inject a `*DAO` directly for simple lookups instead of going through a
  service — `PartidosController` pulls `UsuarioDAO` and `CategoriaDAO` straight in for the
  squad-selection dropdowns.
- Most services are an interface plus an `*Impl`. `PartidoService` is a single concrete
  class — the pair was never split.
- `FileUploadService` and the `springsecurity/WebConfig` resource handler both write and
  read uploaded avatars from an `uploads/` folder next to the working directory. That is
  local disk, not a network call, but it is state outside the database and it is
  gitignored.
- The data-access interfaces are split across two packages for no real reason — `dao/`
  (eight) and `repository/` (two). Both hold plain `JpaRepository` interfaces; the names
  should have been one or the other.

## Persistence

`spring.jpa.hibernate.ddl-auto=create-drop` — Hibernate builds the schema from the entities
at startup and drops it at shutdown. `DataInitializer` (a `CommandLineRunner`) then inserts
the demo dataset through the repositories, so foreign keys and the BCrypt hashing go
through the same code paths the app uses. There is no `schema.sql` or `data.sql`.

## Entities

`Usuario` (member, with a `Set<Rol>`), `Rol`, `Liga` → `Categoria` → `Equipo`,
`UsuarioCategoria` (the join row between a member and a category, carrying `activo` /
`suplente`), `Pista` (court) and `Event` (a calendar entry on a court), `Partido` (a match,
with a captain and a many-to-many squad), `Convocatoria` (an announcement, optionally
scoped to one category).
