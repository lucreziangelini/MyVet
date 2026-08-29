package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.VeterinarianDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Veterinarian;

import java.time.LocalDate;
import java.util.List;

public class VeterinarianDAOMemory
        implements VeterinarianDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public List<Veterinarian> getAvailableByDate(
            LocalDate date) throws DAOException {

        List<Integer> veterinarianIds =
                store.getTimeSlots().stream()
                        .filter(slot ->
                                slot.getVeterinarian() != null
                                        && slot.getDate().equals(date)
                                        && slot.isAvailable())
                        .map(slot ->
                                slot.getVeterinarian().getId())
                        .distinct()
                        .toList();

        return store.getUsers().stream()
                .filter(Veterinarian.class::isInstance)
                .map(Veterinarian.class::cast)
                .filter(veterinarian ->
                        veterinarianIds.contains(
                                veterinarian.getId()))
                .toList();
    }

    @Override
    public Veterinarian findById(int id)
            throws DAOException {

        return store.getUsers().stream()
                .filter(Veterinarian.class::isInstance)
                .map(Veterinarian.class::cast)
                .filter(veterinarian ->
                        veterinarian.getId() == id)
                .findFirst()
                .orElse(null);
    }
}