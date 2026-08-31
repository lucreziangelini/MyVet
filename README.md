## MyVet - Your pet's health, in one place!

Developed for the Software Engineering and Web Design course, University of Rome Tor Vergata.

<p align="center">
  <img src="src/main/resources/images/myvet_logo.png" width="300" style="background-color: white; padding: 10px; border-radius: 10px;"/>
</p>

## Description

MyVet is a Java-based veterinary platform that connects Pet Owners and Veterinarians. It enables Pet Owners to register and manage their pets, choose a preferred date, view the Veterinarians available on that date and book an available time slot. Favourite Veterinarians are highlighted during the booking process. Veterinarians can manage their availability, assign care activities, monitor pet progress and upload medical documents. An Administrator role provides access to platform usage statistics and reports. The application supports both a graphical interface developed with JavaFX and a command-line interface, with email notifications for booking confirmations, cancellations and new care activities.

- **Pet Owners** can register and update their pets, book and cancel appointments, manage care activities and access medical documents
- **Veterinarians** can manage their availability, monitor pets and upload medical documents
- **Administrators** can view reports and statistics on platform usage

## Technologies

- Java 17
- Maven
- MySQL
- JavaFX
- Gson
- SendGrid API *(email notifications)*

## Architecture

**BCE** (Boundary-Control-Entity) and **MVC** (Model-View-Controller) patterns with clear separation between:

- `controller/applicativo` — business logic
- `controller/cli` — CLI controllers
- `controller/gui` — GUI controllers
- `view/cli` — CLI boundary views
- `view/gui` — GUI boundary views
- `dao` — data access layer (Database, File and Memory)
- `model` — domain entities
- `bean` — data transfer objects
- `service` — external services
- `pattern` — GoF patterns (Singleton, Observer, State)

The system supports three persistence modes:

- **DATABASE** — MySQL persistence
- **FILE** — JSON persistence for bookings and time slots; the remaining data uses MySQL
- **MEMORY** — in-memory demo version

## Getting started

At startup, the application asks the user to select the persistence mode:

- `Demo` → simulated in-memory data
- `Database` → MySQL persistence
- `File` → JSON persistence for bookings and time slots; MySQL is still required for users, pets, activities and medical documents

Then, the interface must be selected:

- `CLI` → text-based interface
- `GUI` → graphical JavaFX interface

To use Database or File mode, execute:

```text
database/MyVet_db.sql
```

Then configure the following file:

```text
src/main/resources/db.properties
```

with the following content:

```properties
# Database connection
CONNECTION_URL=jdbc:mysql://localhost:3306/myvetdb

# Login user
LOGIN_USER=myvet_login
LOGIN_PASS=myvet_login

# Pet Owner
PET_OWNER_USER=myvet_pet_owner
PET_OWNER_PASS=myvet_pet_owner

# Veterinarian
VETERINARIAN_USER=myvet_veterinarian
VETERINARIAN_PASS=myvet_veterinarian

# Admin
ADMIN_USER=myvet_admin
ADMIN_PASS=myvet_admin
```

### SendGrid

Keep real SendGrid credentials out of `db.properties`. Configure these
environment variables in the IDE run configuration instead:

```text
SENDGRID_API_KEY=SG.xxxxx
SENDGRID_FROM_EMAIL=verified-sender@example.com
SENDGRID_TEMPLATE_CONFIRMATION_OWNER=d-xxxxx
SENDGRID_TEMPLATE_CANCELLATION_OWNER=d-xxxxx
SENDGRID_TEMPLATE_CONFIRMATION_VETERINARIAN=d-xxxxx
SENDGRID_TEMPLATE_CANCELLATION_VETERINARIAN=d-xxxxx
SENDGRID_TEMPLATE_NEW_CARE_ACTIVITY=d-xxxxx
```

In IntelliJ IDEA open **Run > Edit Configurations**, select the Maven
configuration that runs `javafx:run`, and add the entries under
**Environment variables**. Restart the application after changing them.

⚠️ The `db.properties` file contains database credentials and SendGrid
placeholders. Never commit real credentials or API keys to GitHub.

## Demo credentials

In memory mode, any non-empty password can be used.

| Role | Email | Password |
|---|---|---|
| Pet Owner | `anna@demo.it` | qualsiasi |
| Veterinarian | `luca.vet@demo.it` | qualsiasi |
| Admin | `admin@demo.it` | qualsiasi |

## Database credentials (MySQL mode)

| Role | Email | Password |
|---|---|---|
| Pet Owner | `anna@test.com` | `password123` |
| Veterinarian | `luca.vet@test.com` | `password123` |
| Admin | `admin@test.com` | `password123` |

Other test accounts are available in `database/MyVet_db.sql`.

## Author

Lucrezia Angelini
