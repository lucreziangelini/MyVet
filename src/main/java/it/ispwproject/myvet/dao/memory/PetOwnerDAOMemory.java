package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.PetOwnerDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.model.Veterinarian;

import java.util.List;

public class PetOwnerDAOMemory implements PetOwnerDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public PetOwner findById(int id) throws DAOException {
        return store.getUsers().stream()
                .filter(PetOwner.class::isInstance)
                .map(PetOwner.class::cast)
                .filter(owner -> owner.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public PetOwner findByPetId(int petId)
            throws DAOException {

        return store.getUsers().stream()
                .filter(PetOwner.class::isInstance)
                .map(PetOwner.class::cast)
                .filter(owner -> owner.getPets().stream()
                        .anyMatch(pet -> pet.getId() == petId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<PetOwner> getByVeterinarian(
            int veterinarianId) throws DAOException {

        List<Integer> ownerIds = store.getBookings().stream()
                .filter(booking ->
                        booking.getVeterinarian() != null
                                && booking.getVeterinarian().getId()
                                == veterinarianId
                                && booking.getPetOwner() != null)
                .map(booking ->
                        booking.getPetOwner().getId())
                .distinct()
                .toList();

        return store.getUsers().stream()
                .filter(PetOwner.class::isInstance)
                .map(PetOwner.class::cast)
                .filter(owner -> ownerIds.contains(owner.getId()))
                .toList();
    }

    @Override
    public void addFavouriteVeterinarian(
            int petOwnerId,
            int veterinarianId) throws DAOException {

        PetOwner owner = findById(petOwnerId);

        Veterinarian veterinarian =
                findVeterinarian(veterinarianId);

        if (owner == null || veterinarian == null) {
            throw new DAOException(
                    "Proprietario o veterinario non trovato.");
        }

        owner.addFavorite(veterinarian);
    }

    @Override
    public void removeFavouriteVeterinarian(
            int petOwnerId,
            int veterinarianId) throws DAOException {

        PetOwner owner = findById(petOwnerId);

        if (owner != null) {
            owner.removeFavorite(veterinarianId);
        }
    }

    @Override
    public boolean isFavouriteVeterinarian(
            int petOwnerId,
            int veterinarianId) throws DAOException {

        PetOwner owner = findById(petOwnerId);

        return owner != null
                && owner.hasFavorite(veterinarianId);
    }

    private Veterinarian findVeterinarian(int id) {
        return store.getUsers().stream()
                .filter(Veterinarian.class::isInstance)
                .map(Veterinarian.class::cast)
                .filter(veterinarian ->
                        veterinarian.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
