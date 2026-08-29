package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.TimeSlotDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.TimeSlot;
import it.ispwproject.myvet.model.Veterinarian;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class TimeSlotDAOMemory implements TimeSlotDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public List<TimeSlot> getAvailableByVeterinarian(
            Veterinarian veterinarian) throws DAOException {

        return store.getTimeSlots().stream()
                .filter(slot ->
                        slot.getVeterinarian() != null
                                && slot.getVeterinarian().getId()
                                == veterinarian.getId()
                                && slot.isAvailable()
                                && !slot.getDate().isBefore(
                                LocalDate.now(
                                        ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public List<TimeSlot> getAvailableByVeterinarianAndDate(
            int veterinarianId,
            LocalDate date) throws DAOException {

        return store.getTimeSlots().stream()
                .filter(slot ->
                        slot.getVeterinarian() != null
                                && slot.getVeterinarian().getId()
                                == veterinarianId
                                && slot.getDate().equals(date)
                                && slot.isAvailable())
                .toList();
    }

    @Override
    public List<TimeSlot> getAllByVeterinarian(
            int veterinarianId) throws DAOException {

        LocalDate today =
                LocalDate.now(ZoneId.systemDefault());

        LocalTime now =
                LocalTime.now(ZoneId.systemDefault());

        return store.getTimeSlots().stream()
                .filter(slot ->
                        slot.getVeterinarian() != null
                                && slot.getVeterinarian().getId()
                                == veterinarianId)
                .filter(slot ->
                        slot.getDate().isAfter(today)
                                || (slot.getDate().isEqual(today)
                                && slot.getStartTime().isAfter(now)))
                .toList();
    }

    @Override
    public List<TimeSlot> getPastByVeterinarian(
            int veterinarianId) throws DAOException {

        LocalDate today =
                LocalDate.now(ZoneId.systemDefault());

        LocalTime now =
                LocalTime.now(ZoneId.systemDefault());

        return store.getTimeSlots().stream()
                .filter(slot ->
                        slot.getVeterinarian() != null
                                && slot.getVeterinarian().getId()
                                == veterinarianId)
                .filter(slot ->
                        slot.getDate().isBefore(today)
                                || (slot.getDate().isEqual(today)
                                && slot.getEndTime().isBefore(now)))
                .toList();
    }

    @Override
    public TimeSlot findById(int id)
            throws DAOException {

        return store.getTimeSlots().stream()
                .filter(slot -> slot.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(
            TimeSlot slot,
            int veterinarianId) throws DAOException {

        slot.setId(store.nextSlotId());

        store.getUsers().stream()
                .filter(Veterinarian.class::isInstance)
                .map(Veterinarian.class::cast)
                .filter(veterinarian ->
                        veterinarian.getId() == veterinarianId)
                .findFirst()
                .ifPresent(slot::setVeterinarian);

        store.getTimeSlots().add(slot);
    }

    @Override
    public boolean reserveSlot(
            int slotId,
            int minutes) throws DAOException {

        synchronized (store.getTimeSlots()) {
            TimeSlot slot = findById(slotId);

            if (slot == null || !slot.isAvailable()) {
                return false;
            }

            try {
                slot.reserveForMinutes(minutes);
                return true;
            } catch (IllegalStateException |
                     IllegalArgumentException e) {
                return false;
            }
        }
    }

    @Override
    public void releaseSlot(int slotId)
            throws DAOException {

        TimeSlot slot = findById(slotId);

        if (slot != null) {
            slot.release();
        }
    }

    @Override
    public void delete(
            int slotId,
            int veterinarianId) throws DAOException {

        boolean removed =
                store.getTimeSlots().removeIf(slot ->
                        slot.getId() == slotId
                                && slot.getVeterinarian() != null
                                && slot.getVeterinarian().getId()
                                == veterinarianId
                                && slot.isAvailable());

        if (!removed) {
            throw new DAOException(
                    "Slot non trovato o già prenotato.");
        }
    }
}