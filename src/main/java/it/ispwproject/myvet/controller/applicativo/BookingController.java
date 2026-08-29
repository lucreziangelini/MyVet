package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.*;
import it.ispwproject.myvet.dao.*;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.BookingException;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.*;
import it.ispwproject.myvet.pattern.observer.BookingCancellationObserver;
import it.ispwproject.myvet.pattern.observer.BookingConfirmationObserver;
import it.ispwproject.myvet.pattern.singleton.SessionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingController {

    private final BookingDAO bookingDAO;
    private final PetDAO petDAO;
    private final VeterinarianDAO veterinarianDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final PetOwnerDAO petOwnerDAO;
    private final UserDAO userDAO;

    public BookingController() {
        this.bookingDAO = DAOFactory.getBookingDAO();
        this.petDAO = DAOFactory.getPetDAO();
        this.veterinarianDAO = DAOFactory.getVeterinarianDAO();
        this.timeSlotDAO = DAOFactory.getTimeSlotDAO();
        this.petOwnerDAO = DAOFactory.getPetOwnerDAO();
        this.userDAO = DAOFactory.getUserDAO();
    }

    public List<PetBean> getRegisteredPets() throws DAOException {
        PetOwner owner = getLoggedPetOwner();
        List<PetBean> result = new ArrayList<>();

        for (Pet pet : petDAO.getByOwner(owner.getId())) {
            result.add(new PetBean(
                    pet.getId(),
                    pet.getName(),
                    pet.getSpecies(),
                    pet.getBreed(),
                    pet.getBirthDate()
            ));
        }

        return result;
    }

    public List<VeterinarianBean> getAvailableVeterinarians(LocalDate date)
            throws DAOException {

        PetOwner owner = getLoggedPetOwner();
        List<VeterinarianBean> result = new ArrayList<>();

        for (Veterinarian veterinarian :
                veterinarianDAO.getAvailableByDate(date)) {

            boolean favourite =
                    petOwnerDAO.isFavouriteVeterinarian(
                            owner.getId(),
                            veterinarian.getId()
                    );

            result.add(new VeterinarianBean(
                    veterinarian.getId(),
                    veterinarian.getName(),
                    veterinarian.getSurname(),
                    veterinarian.getBio(),
                    veterinarian.getEmail(),
                    veterinarian.getSpecialization(),
                    favourite
            ));
        }

        return result;
    }

    public List<TimeSlotBean> getVeterinarianAvailability(
            VeterinarianBean veterinarianBean,
            LocalDate date) throws DAOException {

        List<TimeSlotBean> result = new ArrayList<>();

        for (TimeSlot slot :
                timeSlotDAO.getAvailableByVeterinarianAndDate(
                        veterinarianBean.getId(),
                        date
                )) {

            result.add(new TimeSlotBean(
                    slot.getId(),
                    slot.getDate(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.isAvailable()
            ));
        }

        return result;
    }

    private static final int RESERVATION_MINUTES = 5;

    public BookingResponseBean prepareBookingSummary(
            BookingRequestBean request)
            throws DAOException, BookingException {

        PetOwner owner = getLoggedPetOwner();

        Pet pet = petDAO.findById(
                request.getPet().getId()
        );

        Veterinarian veterinarian =
                veterinarianDAO.findById(
                        request.getVeterinarian().getId()
                );

        TimeSlot slot = timeSlotDAO.findById(
                request.getTimeSlot().getId()
        );

        if (pet == null) {
            throw new DAOException("Animale non trovato.");
        }

        if (veterinarian == null) {
            throw new DAOException("Veterinario non trovato.");
        }

        if (slot == null) {
            throw new DAOException("Slot non trovato.");
        }

        boolean reserved = timeSlotDAO.reserveSlot(
                slot.getId(),
                RESERVATION_MINUTES
        );

        if (!reserved) {
            throw new BookingException(
                    "Lo slot è stato appena prenotato da un altro utente. "
                            + "Seleziona un altro slot."
            );
        }

        return new BookingResponseBean(
                0,
                BookingStatus.PENDING,
                new PetOwnerBean(
                        owner.getId(),
                        owner.getName(),
                        owner.getSurname(),
                        owner.getEmail()
                ),
                new PetBean(
                        pet.getId(),
                        pet.getName(),
                        pet.getSpecies(),
                        pet.getBreed(),
                        pet.getBirthDate()
                ),
                new VeterinarianBean(
                        veterinarian.getId(),
                        veterinarian.getName(),
                        veterinarian.getSurname(),
                        veterinarian.getBio(),
                        veterinarian.getEmail(),
                        veterinarian.getSpecialization(),
                        false
                ),
                new TimeSlotBean(
                        slot.getId(),
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        slot.isAvailable()
                )
        );
    }

    public void releaseSlot(int slotId) throws DAOException {
        timeSlotDAO.releaseSlot(slotId);
    }

    public BookingResponseBean createBooking(
            BookingRequestBean request)
            throws DAOException, BookingException {

        PetOwner owner = getLoggedPetOwner();

        Pet pet = petDAO.findById(
                request.getPet().getId()
        );

        Veterinarian veterinarian =
                veterinarianDAO.findById(
                        request.getVeterinarian().getId()
                );

        TimeSlot slot = timeSlotDAO.findById(
                request.getTimeSlot().getId()
        );

        if (pet == null) {
            throw new DAOException("Animale non trovato.");
        }

        if (veterinarian == null) {
            throw new DAOException("Veterinario non trovato.");
        }

        if (slot == null) {
            throw new DAOException("Slot non trovato.");
        }

        for (Booking booking :
                bookingDAO.findByPetOwner(owner.getId())) {

            if (booking.getStatus()
                    == BookingStatus.CANCELLED) {
                continue;
            }

            TimeSlot existing = booking.getTimeSlot();

            if (existing != null
                    && slot.overlaps(existing)) {

                throw new BookingException(
                        "Hai già una prenotazione sovrapposta: "
                                + existing.getDate() + " "
                                + existing.getStartTime() + " - "
                                + existing.getEndTime()
                );
            }
        }

        Booking booking = new Booking(
                owner,
                veterinarian,
                pet,
                slot
        );

        booking.attach(
                new BookingConfirmationObserver(booking)
        );

        booking.confirm();
        bookingDAO.save(booking);

        return new BookingResponseBean(
                booking.getId(),
                booking.getStatus(),
                new PetOwnerBean(
                        owner.getId(),
                        owner.getName(),
                        owner.getSurname(),
                        owner.getEmail()
                ),
                new PetBean(
                        pet.getId(),
                        pet.getName(),
                        pet.getSpecies(),
                        pet.getBreed(),
                        pet.getBirthDate()
                ),
                new VeterinarianBean(
                        veterinarian.getId(),
                        veterinarian.getName(),
                        veterinarian.getSurname(),
                        veterinarian.getBio(),
                        veterinarian.getEmail(),
                        veterinarian.getSpecialization(),
                        false
                ),
                new TimeSlotBean(
                        slot.getId(),
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        slot.isAvailable()
                )
        );
    }

    public List<BookingResponseBean> getPetOwnerBookings(
            int petOwnerId) throws DAOException {

        List<BookingResponseBean> result = new ArrayList<>();

        for (Booking booking :
                bookingDAO.findByPetOwner(petOwnerId)) {

            PetOwner owner = booking.getPetOwner();
            Veterinarian veterinarian =
                    booking.getVeterinarian();
            Pet pet = booking.getPet();
            TimeSlot slot = booking.getTimeSlot();

            if (owner == null
                    || veterinarian == null
                    || pet == null
                    || slot == null) {
                continue;
            }

            result.add(new BookingResponseBean(
                    booking.getId(),
                    booking.getStatus(),
                    new PetOwnerBean(
                            owner.getId(),
                            owner.getName(),
                            owner.getSurname(),
                            owner.getEmail()
                    ),
                    new PetBean(
                            pet.getId(),
                            pet.getName(),
                            pet.getSpecies(),
                            pet.getBreed(),
                            pet.getBirthDate()
                    ),
                    new VeterinarianBean(
                            veterinarian.getId(),
                            veterinarian.getName(),
                            veterinarian.getSurname(),
                            veterinarian.getBio(),
                            veterinarian.getEmail(),
                            veterinarian.getSpecialization(),
                            false
                    ),
                    new TimeSlotBean(
                            slot.getId(),
                            slot.getDate(),
                            slot.getStartTime(),
                            slot.getEndTime(),
                            slot.isAvailable()
                    )
            ));
        }

        return result;
    }

    public List<BookingResponseBean> getPetOwnerPastBookings(
            int petOwnerId) throws DAOException {

        List<BookingResponseBean> result = new ArrayList<>();

        for (Booking booking :
                bookingDAO.findPastByPetOwner(petOwnerId)) {

            PetOwner owner = booking.getPetOwner();
            Veterinarian veterinarian =
                    booking.getVeterinarian();
            Pet pet = booking.getPet();
            TimeSlot slot = booking.getTimeSlot();

            if (owner == null
                    || veterinarian == null
                    || pet == null
                    || slot == null) {
                continue;
            }

            result.add(new BookingResponseBean(
                    booking.getId(),
                    booking.getStatus(),
                    new PetOwnerBean(
                            owner.getId(),
                            owner.getName(),
                            owner.getSurname(),
                            owner.getEmail()
                    ),
                    new PetBean(
                            pet.getId(),
                            pet.getName(),
                            pet.getSpecies(),
                            pet.getBreed(),
                            pet.getBirthDate()
                    ),
                    new VeterinarianBean(
                            veterinarian.getId(),
                            veterinarian.getName(),
                            veterinarian.getSurname(),
                            veterinarian.getBio(),
                            veterinarian.getEmail(),
                            veterinarian.getSpecialization(),
                            false
                    ),
                    new TimeSlotBean(
                            slot.getId(),
                            slot.getDate(),
                            slot.getStartTime(),
                            slot.getEndTime(),
                            slot.isAvailable()
                    )
            ));
        }

        return result;
    }

    public void cancelBooking(
            int bookingId,
            int petOwnerId) throws DAOException {

        List<Booking> bookings =
                bookingDAO.findByPetOwner(petOwnerId);

        Booking booking = bookings.stream()
                .filter(current ->
                        current.getId() == bookingId)
                .findFirst()
                .orElse(null);

        if (booking != null) {
            booking.attach(
                    new BookingCancellationObserver(booking)
            );
        }

        bookingDAO.cancel(
                bookingId,
                petOwnerId
        );
    }

    public void addVeterinarianToFavourites(
            int veterinarianId)
            throws DAOException {

        PetOwner owner = getLoggedPetOwner();

        Veterinarian veterinarian =
                veterinarianDAO.findById(veterinarianId);

        if (veterinarian == null) {
            throw new DAOException(
                    "Veterinario non trovato."
            );
        }

        owner.addFavorite(veterinarian);

        petOwnerDAO.addFavouriteVeterinarian(
                owner.getId(),
                veterinarianId
        );
    }

    public void removeVeterinarianFromFavourites(
            int veterinarianId)
            throws DAOException {

        PetOwner owner = getLoggedPetOwner();

        Veterinarian veterinarian =
                veterinarianDAO.findById(veterinarianId);

        if (veterinarian == null) {
            throw new DAOException(
                    "Veterinario non trovato."
            );
        }

        owner.removeFavorite(veterinarianId);

        petOwnerDAO.removeFavouriteVeterinarian(
                owner.getId(),
                veterinarianId
        );
    }

    private PetOwner getLoggedPetOwner()
            throws DAOException {

        String email = SessionManager
                .getInstance()
                .getCurrentSession()
                .getEmail();

        return (PetOwner) userDAO.findByEmail(email);
    }
}