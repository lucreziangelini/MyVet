package it.ispwproject.myvet.demo;

import it.ispwproject.myvet.enumerator.DocumentType;
import it.ispwproject.myvet.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Fonte dati condivisa per la modalità demo in-memory.
 *
 * Implementa il Singleton pattern: esiste una sola istanza
 * del DemoDataStore per tutta l'applicazione.
 *
 * Tutti i DAO in-memory leggono e scrivono attraverso questa classe.
 */
public class DemoDataStore {

    private static DemoDataStore instance;

    private final List<User> users = new ArrayList<>();
    private final List<TimeSlot> timeSlots = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final List<CareActivity> careActivities = new ArrayList<>();
    private final List<MedicalDocument> medicalDocuments =
            new ArrayList<>();

    private int nextUserId = 6;
    private int nextPetId = 3;
    private int nextSlotId = 5;
    private int nextBookingId = 3;
    private int nextActivityId = 3;
    private int nextMedicalDocumentId = 4;

    private DemoDataStore() {
        initializeData();
    }

    public static synchronized DemoDataStore getInstance() {
        if (instance == null) {
            instance = new DemoDataStore();
        }

        return instance;
    }

    public static synchronized void reset() {
        instance = null;
    }

    private void initializeData() {
        LocalDate today = LocalDate.now(
                ZoneId.systemDefault()
        );

        LocalDateTime now = LocalDateTime.now(
                ZoneId.systemDefault()
        );

        // Users

        PetOwner owner1 = new PetOwner(
                1,
                "Anna",
                "Rossi",
                "anna@demo.it",
                "demo"
        );

        PetOwner owner2 = new PetOwner(
                2,
                "Marco",
                "Bianchi",
                "marco@demo.it",
                "demo"
        );

        Veterinarian veterinarian1 = new Veterinarian(
                3,
                "Luca",
                "Verdi",
                "luca.vet@demo.it",
                "demo",
                "Veterinario con esperienza nella cura degli animali da compagnia.",
                "Medicina generale"
        );

        Veterinarian veterinarian2 = new Veterinarian(
                4,
                "Giulia",
                "Romano",
                "giulia.vet@demo.it",
                "demo",
                "Veterinaria specializzata nella prevenzione e nel benessere animale.",
                "Medicina preventiva"
        );

        Admin admin = new Admin(
                5,
                "Admin",
                "MyVet",
                "admin@demo.it",
                "demo"
        );

        users.add(owner1);
        users.add(owner2);
        users.add(veterinarian1);
        users.add(veterinarian2);
        users.add(admin);

        // Pets

        Pet milo = new Pet(
                1,
                "Milo",
                "Dog",
                "Labrador",
                LocalDate.of(2020, 5, 12)
        );

        Pet luna = new Pet(
                2,
                "Luna",
                "Cat",
                "European Shorthair",
                LocalDate.of(2021, 9, 3)
        );

        owner1.addPet(milo);
        owner2.addPet(luna);

        // Favorite Veterinarians

        owner1.addFavorite(veterinarian1);
        owner2.addFavorite(veterinarian2);

        // Time slots

        TimeSlot slot1 = new TimeSlot(
                1,
                veterinarian1,
                today.plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30)
        );

        TimeSlot slot2 = new TimeSlot(
                2,
                veterinarian1,
                today.plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30)
        );

        TimeSlot slot3 = new TimeSlot(
                3,
                veterinarian2,
                today.plusDays(1),
                LocalTime.of(11, 0),
                LocalTime.of(11, 30)
        );

        TimeSlot slot4 = new TimeSlot(
                4,
                veterinarian2,
                today.plusDays(2),
                LocalTime.of(15, 0),
                LocalTime.of(15, 30)
        );

        timeSlots.add(slot1);
        timeSlots.add(slot2);
        timeSlots.add(slot3);
        timeSlots.add(slot4);

        // Bookings

        Booking booking1 = new Booking(
                owner1,
                veterinarian1,
                milo,
                slot1
        );

        booking1.setId(1);
        booking1.confirm();
        bookings.add(booking1);

        Booking booking2 = new Booking(
                owner2,
                veterinarian2,
                luna,
                slot3
        );

        booking2.setId(2);
        booking2.confirm();
        bookings.add(booking2);

        // Care activities

        CareActivity activity1 = new CareActivity(
                veterinarian1,
                milo,
                "Administer medication",
                "Give one tablet after the evening meal for five days."
        );

        activity1.setId(1);
        activity1.setCreatedAt(now.minusDays(5));
        activity1.complete();
        activity1.setCompletedAt(now.minusDays(4));

        CareActivity activity2 = new CareActivity(
                veterinarian2,
                luna,
                "Monitor appetite",
                "Check whether Luna completes each daily meal."
        );

        activity2.setId(2);
        activity2.setCreatedAt(now.minusDays(2));

        careActivities.add(activity1);
        careActivities.add(activity2);

        // Medical documents

        MedicalDocument document1 = new MedicalDocument(
                1,
                milo,
                veterinarian1,
                "Medical report",
                DocumentType.MEDICAL_REPORT,
                "demo/documents/milo-medical-report.pdf",
                now.minusDays(10)
        );

        MedicalDocument document2 = new MedicalDocument(
                2,
                luna,
                veterinarian2,
                "Vaccination certificate",
                DocumentType.VACCINATION_CERTIFICATE,
                "demo/documents/luna-vaccination-certificate.pdf",
                now.minusDays(5)
        );

        MedicalDocument document3 = new MedicalDocument(
                3,
                milo,
                veterinarian1,
                "Antibiotic prescription",
                DocumentType.PRESCRIPTION,
                "demo/documents/milo-prescription.pdf",
                now.minusDays(9)
        );

        medicalDocuments.add(document1);
        medicalDocuments.add(document2);
        medicalDocuments.add(document3);
    }

    public List<User> getUsers() {
        return users;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public List<CareActivity> getCareActivities() {
        return careActivities;
    }

    public List<MedicalDocument> getMedicalDocuments() {
        return medicalDocuments;
    }

    public int nextUserId() {
        return nextUserId++;
    }

    public int nextPetId() {
        return nextPetId++;
    }

    public int nextSlotId() {
        return nextSlotId++;
    }

    public int nextBookingId() {
        return nextBookingId++;
    }

    public int nextActivityId() {
        return nextActivityId++;
    }

    public int nextMedicalDocumentId() {
        return nextMedicalDocumentId++;
    }
}
