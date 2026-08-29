package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.PetDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Pet;
import it.ispwproject.myvet.model.PetOwner;

import java.util.List;

public class PetDAOMemory implements PetDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public Pet findById(int petId) throws DAOException {
        return store.getUsers().stream()
                .filter(PetOwner.class::isInstance)
                .map(PetOwner.class::cast)
                .flatMap(owner -> owner.getPets().stream())
                .filter(pet -> pet.getId() == petId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Pet> getByOwner(
            int petOwnerId) throws DAOException {

        return store.getUsers().stream()
                .filter(PetOwner.class::isInstance)
                .map(PetOwner.class::cast)
                .filter(owner -> owner.getId() == petOwnerId)
                .findFirst()
                .map(PetOwner::getPets)
                .orElseGet(List::of);
    }

    @Override
    public List<Pet> getByVeterinarian(
            int veterinarianId) throws DAOException {

        List<Integer> petIds = store.getBookings().stream()
                .filter(booking ->
                        booking.getVeterinarian() != null
                                && booking.getVeterinarian().getId()
                                == veterinarianId
                                && booking.getStatus()
                                != BookingStatus.CANCELLED
                                && booking.getPet() != null)
                .map(booking -> booking.getPet().getId())
                .distinct()
                .toList();

        return store.getUsers().stream()
                .filter(PetOwner.class::isInstance)
                .map(PetOwner.class::cast)
                .flatMap(owner -> owner.getPets().stream())
                .filter(pet -> petIds.contains(pet.getId()))
                .toList();
    }
}