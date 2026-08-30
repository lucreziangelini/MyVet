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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        validateBookingRequest(request);
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

        validateBookingSelection(
                owner,
                pet,
                veterinarian,
                slot,
                request.getTimeSlot()
        );

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

        validateBookingRequest(request);
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

        validateBookingSelection(
                owner,
                pet,
                veterinarian,
                slot,
                request.getTimeSlot()
        );

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

        validateLoggedPetOwner(petOwnerId);
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

        validateLoggedPetOwner(petOwnerId);
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

        validateLoggedPetOwner(petOwnerId);

        List<Booking> bookings =
                bookingDAO.findByPetOwner(petOwnerId);

        Booking booking = bookings.stream()
                .filter(current ->
                        current.getId() == bookingId)
                .findFirst()
                .orElse(null);

        if (booking == null) {
            throw new DAOException(
                    "Appuntamento non trovato o non autorizzato."
            );
        }

        if (!booking.isCancellable()) {
            throw new DAOException(
                    "L'appuntamento non può più essere annullato."
            );
        }

        booking.attach(
                new BookingCancellationObserver(booking)
        );

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

        User loggedUser = SessionManager
                .getInstance()
                .getLoggedUser();

        if (!(loggedUser instanceof PetOwner)) {
            throw new DAOException(
                    "L'utente autenticato non è un pet owner."
            );
        }

        User persistedUser = userDAO.findByEmail(
                loggedUser.getEmail()
        );

        if (!(persistedUser instanceof PetOwner owner)) {
            throw new DAOException(
                    "Pet owner autenticato non trovato."
            );
        }

        return owner;
    }

    private void validateLoggedPetOwner(int petOwnerId)
            throws DAOException {

        PetOwner owner = getLoggedPetOwner();

        if (owner.getId() != petOwnerId) {
            throw new DAOException(
                    "Non puoi accedere alle prenotazioni di un altro utente."
            );
        }
    }

    private void validateBookingRequest(
            BookingRequestBean request) throws BookingException {

        if (request == null
                || request.getPet() == null
                || request.getVeterinarian() == null
                || request.getTimeSlot() == null) {

            throw new BookingException(
                    "Dati della prenotazione incompleti."
            );
        }
    }

    private void validateBookingSelection(
            PetOwner owner,
            Pet pet,
            Veterinarian veterinarian,
            TimeSlot slot,
            TimeSlotBean requestedSlot)
            throws DAOException, BookingException {

        boolean petBelongsToOwner = petDAO
                .getByOwner(owner.getId())
                .stream()
                .anyMatch(ownedPet ->
                        ownedPet.getId() == pet.getId());

        if (!petBelongsToOwner) {
            throw new BookingException(
                    "L'animale selezionato non appartiene al pet owner autenticato."
            );
        }

        if (slot.getVeterinarian() == null
                || slot.getVeterinarian().getId()
                != veterinarian.getId()) {

            throw new BookingException(
                    "Lo slot selezionato non appartiene al veterinario scelto."
            );
        }

        if (slot.getDate() == null
                || slot.getStartTime() == null
                || slot.getEndTime() == null
                || !slot.getStartTime().isBefore(slot.getEndTime())) {

            throw new BookingException(
                    "Lo slot selezionato non è valido."
            );
        }

        if (requestedSlot.getDate() != null
                && !Objects.equals(
                requestedSlot.getDate(),
                slot.getDate())) {

            throw new BookingException(
                    "La data dello slot selezionato non è valida."
            );
        }

        LocalDateTime appointmentStart = LocalDateTime.of(
                slot.getDate(),
                slot.getStartTime()
        );

        if (!appointmentStart.isAfter(
                LocalDateTime.now(ZoneId.systemDefault()))) {

            throw new BookingException(
                    "Non puoi prenotare uno slot passato."
            );
        }

    }
}
