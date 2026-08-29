package it.ispwproject.myvet.controller;

import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.controller.applicativo.BookingController;
import it.ispwproject.myvet.model.Booking;
import it.ispwproject.myvet.model.Pet;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.model.TimeSlot;
import it.ispwproject.myvet.model.Veterinarian;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingControllerTest {

    @Test
    void veterinarianKeepsClinicInformation() {
        Veterinarian veterinarian = new Veterinarian(
                1,
                "Anna",
                "Neri",
                "anna@myvet.it",
                "password",
                "Via Roma 10"
        );

        assertTrue(veterinarian.getClinicAddress().contains("Roma"));
    }

    @Test
    void petOwnerKeepsRegisteredPetsAndFavoriteVeterinarians() {
        PetOwner owner = new PetOwner(
                1,
                "Giulia",
                "Rossi",
                "owner@myvet.it",
                "password"
        );
        Pet pet = new Pet(
                1,
                "Luna",
                "Dog",
                "Labrador",
                LocalDate.of(2021, 4, 12)
        );
        Veterinarian veterinarian = new Veterinarian(
                2,
                "Marco",
                "Verdi",
                "marco@myvet.it",
                "password",
                "Via Milano 20"
        );

        owner.addPet(pet);
        owner.addFavorite(veterinarian);

        assertTrue(owner.hasRegisteredPets());
        assertTrue(owner.hasFavorite(veterinarian.getId()));
    }

    @Test
    void confirmsBookingFromFiveMinuteReservation() {
        PetOwner owner = new PetOwner(
                1, "Giulia", "Rossi", "owner@myvet.it", "password"
        );
        Pet pet = new Pet(
                1, "Luna", "Cat", "European Shorthair",
                LocalDate.of(2021, 4, 12)
        );
        Veterinarian veterinarian = new Veterinarian(
                2, "Marco", "Verdi", "marco@myvet.it", "password",
                "Via Milano 20"
        );
        TimeSlot slot = new TimeSlot(
                1, veterinarian, LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(10, 30)
        );
        Booking booking = new Booking(owner, veterinarian, pet, slot);

        BookingController controller = new BookingController();
        controller.reserveTimeSlot(slot);
        controller.confirmBooking(booking);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertFalse(slot.isAvailable());
        assertFalse(slot.isReserved());
    }

    @Test
    void expiredReservationMakesSlotAvailableAgain() {
        TimeSlot slot = new TimeSlot(
                1, LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(10, 30)
        );

        slot.setReservedUntil(LocalDateTime.now().minusSeconds(1));

        assertTrue(slot.isAvailable());
    }
}
