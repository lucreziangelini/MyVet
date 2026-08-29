package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.ActivityDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.CareActivity;
import it.ispwproject.myvet.model.PetOwner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class ActivityDAOMemory implements ActivityDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public void save(CareActivity activity) throws DAOException {
        activity.setId(store.nextActivityId());
        activity.setCreatedAt(
                LocalDateTime.now(ZoneId.systemDefault())
        );
        store.getCareActivities().add(activity);
    }

    @Override
    public List<CareActivity> getByPetAndVeterinarian(
            int veterinarianId,
            int petId) throws DAOException {

        return store.getCareActivities().stream()
                .filter(activity ->
                        activity.getVeterinarian() != null
                                && activity.getVeterinarian().getId()
                                == veterinarianId
                                && activity.getPet() != null
                                && activity.getPet().getId() == petId)
                .toList();
    }

    @Override
    public List<CareActivity> getByPetOwner(
            int petOwnerId) throws DAOException {

        return store.getCareActivities().stream()
                .filter(activity ->
                        activity.getPet() != null
                                && belongsToOwner(
                                activity.getPet().getId(),
                                petOwnerId))
                .toList();
    }

    @Override
    public void markAsCompleted(
            int activityId,
            int petOwnerId) throws DAOException {

        store.getCareActivities().stream()
                .filter(activity ->
                        activity.getId() == activityId
                                && activity.getPet() != null
                                && belongsToOwner(
                                activity.getPet().getId(),
                                petOwnerId))
                .findFirst()
                .ifPresent(CareActivity::complete);
    }

    @Override
    public CareActivity findByIdForOwner(
            int activityId,
            int petOwnerId) throws DAOException {

        return store.getCareActivities().stream()
                .filter(activity ->
                        activity.getId() == activityId
                                && activity.getPet() != null
                                && belongsToOwner(
                                activity.getPet().getId(),
                                petOwnerId))
                .findFirst()
                .orElse(null);
    }

    private boolean belongsToOwner(
            int petId,
            int petOwnerId) {

        return store.getUsers().stream()
                .filter(PetOwner.class::isInstance)
                .map(PetOwner.class::cast)
                .filter(owner -> owner.getId() == petOwnerId)
                .flatMap(owner -> owner.getPets().stream())
                .anyMatch(pet -> pet.getId() == petId);
    }
}