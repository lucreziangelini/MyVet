package it.ispwproject.myvet.pattern.observer;

import it.ispwproject.myvet.bean.*;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.NotificationException;
import it.ispwproject.myvet.model.Booking;
import it.ispwproject.myvet.service.NotificationService;
import it.ispwproject.myvet.util.logger.AppLogger;

public class BookingCancellationObserver implements Observer {

    private final Booking booking;

    public BookingCancellationObserver(Booking booking) {
        this.booking = booking;
    }

    @Override
    public void update() {
        if (booking.getStatus() != BookingStatus.CANCELLED) {
            return;
        }

        try {
            BookingResponseBean response = buildResponse();

            NotificationService.sendBookingCancellationToOwner(
                    booking.getPetOwner().getEmail(),
                    response
            );

            NotificationService.sendBookingCancellationToVeterinarian(
                    booking.getVeterinarian().getEmail(),
                    response
            );

        } catch (NotificationException e) {
            AppLogger.logWarning(
                    "Notifica di cancellazione non inviata: " + e.getMessage()
            );
        }
    }

    private BookingResponseBean buildResponse() {
        PetOwnerBean petOwnerBean = new PetOwnerBean(
                booking.getPetOwner().getId(),
                booking.getPetOwner().getName(),
                booking.getPetOwner().getSurname(),
                booking.getPetOwner().getEmail()
        );

        VeterinarianBean veterinarianBean = new VeterinarianBean(
                booking.getVeterinarian().getId(),
                booking.getVeterinarian().getName(),
                booking.getVeterinarian().getSurname(),
                booking.getVeterinarian().getBio(),
                booking.getVeterinarian().getEmail(),
                booking.getVeterinarian().getSpecialization(),
                false
        );

        PetBean petBean = new PetBean(
                booking.getPet().getId(),
                booking.getPet().getName(),
                booking.getPet().getSpecies(),
                booking.getPet().getBreed(),
                booking.getPet().getBirthDate()
        );

        TimeSlotBean timeSlotBean = new TimeSlotBean(
                booking.getTimeSlot().getId(),
                booking.getTimeSlot().getDate(),
                booking.getTimeSlot().getStartTime(),
                booking.getTimeSlot().getEndTime(),
                booking.getTimeSlot().isAvailable()
        );

        return new BookingResponseBean(
                booking.getId(),
                booking.getStatus(),
                petOwnerBean,
                petBean,
                veterinarianBean,
                timeSlotBean
        );
    }
}
