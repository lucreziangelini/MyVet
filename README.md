# MyVet

MyVet is a Java 17 Maven application for veterinary appointment and pet-health management,
structured according to the BCE/MVC approach used in the ISPW course.

## Core features

1. Book an appointment for a registered pet.
2. Find veterinarians associated with the selected pet species, highlighting saved favourites.
3. Temporarily reserve an available time slot for five minutes before confirmation.
4. Cancel a future appointment and release its time slot.
5. Manage `PENDING`, `CONFIRMED`, `CANCELLED` and `COMPLETED` booking states.
6. Assign and complete pet care activities and monitor their progress.
7. Upload and access pet medical documents.
8. Analyze booking statistics and send booking or care-activity notifications.

## Run with IntelliJ

1. Open `C:\Users\mange\Desktop\MyVet` in IntelliJ IDEA.
2. Import `pom.xml` as a Maven project.
3. Select JDK 17 or newer.
4. Run `it.ispwproject.myvet.Main`.

## Run and test from a terminal

```shell
mvn clean test
mvn exec:java
```

The project includes in-memory demo data. Email delivery through `NotificationService` requires
valid SendGrid configuration in `db.properties`.
