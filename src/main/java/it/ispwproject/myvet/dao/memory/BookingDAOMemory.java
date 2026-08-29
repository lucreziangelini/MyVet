package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.BookingDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Booking;
import it.ispwproject.myvet.model.PetOwner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOMemory implements BookingDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public void save(Booking booking) throws DAOException {
        booking.setId(store.nextBookingId());
        booking.setStatus(BookingStatus.CONFIRMED);
        store.getBookings().add(booking);
        booking.getTimeSlot().setAvailable(false);
    }

    @Override
    public List<Booking> findByPetOwner(
            int petOwnerId) throws DAOException {

        return store.getBookings().stream()
                .filter(booking ->
                        booking.getPetOwner() != null
                                && booking.getPetOwner().getId()
                                == petOwnerId)
                .toList();
    }

    @Override
    public List<Booking> findByVeterinarian(
            int veterinarianId) throws DAOException {

        return store.getBookings().stream()
                .filter(booking ->
                        booking.getVeterinarian() != null
                                && booking.getVeterinarian().getId()
                                == veterinarianId
                                && booking.getStatus()
                                == BookingStatus.CONFIRMED)
                .toList();
    }

    @Override
    public List<Booking> findPastByPetOwner(
            int petOwnerId) throws DAOException {

        LocalDate today =
                LocalDate.now(ZoneId.systemDefault());

        LocalTime now =
                LocalTime.now(ZoneId.systemDefault());

        return store.getBookings().stream()
                .filter(booking ->
                        booking.getPetOwner() != null
                                && booking.getPetOwner().getId()
                                == petOwnerId
                                && booking.getStatus()
                                == BookingStatus.CONFIRMED
                                && booking.getTimeSlot() != null)
                .filter(booking ->
                        booking.getTimeSlot().getDate().isBefore(today)
                                || (booking.getTimeSlot().getDate().isEqual(today)
                                && booking.getTimeSlot().getEndTime().isBefore(now)))
                .toList();
    }

    @Override
    public List<Booking> findAll() throws DAOException {
        return new ArrayList<>(store.getBookings());
    }

    @Override
    public void cancel(
            int bookingId,
            int petOwnerId) throws DAOException {

        Booking booking = store.getBookings().stream()
                .filter(current -> current.getId() == bookingId)
                .findFirst()
                .orElseThrow(() -> new DAOException(
                        "Appuntamento non trovato (ID: "
                                + bookingId + ")"));

        PetOwner owner = booking.getPetOwner();

        if (owner == null || owner.getId() != petOwnerId) {
            throw new DAOException(
                    "Non puoi annullare un appuntamento "
                            + "che non ti appartiene.");
        }

        try {
            booking.cancel();
        } catch (IllegalStateException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
}