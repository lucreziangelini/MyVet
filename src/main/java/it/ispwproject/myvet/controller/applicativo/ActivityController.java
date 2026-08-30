package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.*;
import it.ispwproject.myvet.dao.*;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.exception.NotificationException;
import it.ispwproject.myvet.model.*;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.service.NotificationService;
import it.ispwproject.myvet.util.logger.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class ActivityController {

    private final PetDAO petDAO;
    private final PetOwnerDAO petOwnerDAO;
    private final ActivityDAO activityDAO;
    private final ProgressDAO progressDAO;
    private final UserDAO userDAO;

    public ActivityController() {
        this.petDAO = DAOFactory.getPetDAO();
        this.petOwnerDAO = DAOFactory.getPetOwnerDAO();
        this.activityDAO = DAOFactory.getActivityDAO();
        this.progressDAO = DAOFactory.getProgressDAO();
        this.userDAO = DAOFactory.getUserDAO();
    }

    public List<PetBean> getPets() throws DAOException {
        Veterinarian veterinarian = getLoggedVeterinarian();
        List<PetBean> result = new ArrayList<>();

        for (Pet pet : petDAO.getByVeterinarian(
                veterinarian.getId())) {

            result.add(toPetBean(pet));
        }

        return result;
    }

    public void assignActivity(ActivityBean bean)
            throws DAOException {

        Veterinarian veterinarian =
                getLoggedVeterinarian();

        Pet pet = petDAO.findById(
                bean.getPet().getId()
        );

        if (pet == null) {
            throw new DAOException("Animale non trovato.");
        }

        CareActivity activity = new CareActivity(
                veterinarian,
                pet,
                bean.getDescription()
        );

        activityDAO.save(activity);

        bean.setId(activity.getId());
        bean.setPet(toPetBean(pet));
        bean.setVeterinarian(
                toVeterinarianBean(veterinarian)
        );
        bean.setCreatedAt(activity.getCreatedAt());

        PetOwner owner =
                petOwnerDAO.findByPetId(pet.getId());

        if (owner != null) {
            try {
                NotificationService.sendNewCareActivity(
                        owner.getEmail(),
                        bean
                );
            } catch (NotificationException e) {
                AppLogger.logWarning(
                        "Notifica attività non inviata: "
                                + e.getMessage()
                );
            }
        }
    }

    public List<ActivityBean> getActivities(int petId)
            throws DAOException {

        Veterinarian veterinarian =
                getLoggedVeterinarian();

        List<ActivityBean> result = new ArrayList<>();

        for (CareActivity activity :
                activityDAO.getByPetAndVeterinarian(
                        veterinarian.getId(),
                        petId
                )) {

            result.add(toActivityBean(activity));
        }

        return result;
    }

    public List<ActivityBean> getMyActivities()
            throws DAOException {

        PetOwner owner = getLoggedPetOwner();
        List<ActivityBean> result = new ArrayList<>();

        for (CareActivity activity :
                activityDAO.getByPetOwner(owner.getId())) {

            result.add(toActivityBean(activity));
        }

        return result;
    }

    public void markActivityCompleted(int activityId)
            throws DAOException {

        PetOwner owner = getLoggedPetOwner();

        CareActivity activity =
                activityDAO.findByIdForOwner(
                        activityId,
                        owner.getId()
                );

        if (activity == null) {
            throw new DAOException(
                    "Attività non trovata o non autorizzata."
            );
        }

        activity.complete();

        activityDAO.markAsCompleted(
                activityId,
                owner.getId()
        );
    }

    public void updateProgress(ProgressBean bean)
            throws DAOException {

        Veterinarian veterinarian =
                getLoggedVeterinarian();

        Pet pet = petDAO.findById(
                bean.getPet().getId()
        );

        if (pet == null) {
            throw new DAOException("Animale non trovato.");
        }

        Progress progress =
                progressDAO.findByPetAndVeterinarian(
                        veterinarian.getId(),
                        pet.getId()
                );

        if (progress == null) {
            progress = new Progress(
                    veterinarian,
                    pet,
                    bean.getNotes()
            );
        } else {
            progress.updateNotes(bean.getNotes());
        }

        progressDAO.saveOrUpdate(progress);
    }

    public ProgressBean getProgress(int petId)
            throws DAOException {

        Veterinarian veterinarian =
                getLoggedVeterinarian();

        Progress progress =
                progressDAO.findByPetAndVeterinarian(
                        veterinarian.getId(),
                        petId
                );

        if (progress == null) {
            return null;
        }

        return new ProgressBean(
                toPetBean(progress.getPet()),
                progress.getNotes(),
                progress.getUpdatedAt()
        );
    }

    private User getLoggedUser() throws DAOException {
        SessionBean session = SessionManager
                .getInstance()
                .getLoggedUser();

        User user = userDAO.findByEmail(
                session.getEmail()
        );

        if (user == null) {
            throw new DAOException(
                    "Utente autenticato non trovato."
            );
        }

        return user;
    }

    private Veterinarian getLoggedVeterinarian()
            throws DAOException {

        User user = getLoggedUser();

        if (!(user instanceof Veterinarian veterinarian)) {
            throw new DAOException(
                    "L'utente autenticato non è un veterinario."
            );
        }

        return veterinarian;
    }

    private PetOwner getLoggedPetOwner()
            throws DAOException {

        User user = getLoggedUser();

        if (!(user instanceof PetOwner owner)) {
            throw new DAOException(
                    "L'utente autenticato non è un Pet Owner."
            );
        }

        return owner;
    }

    private ActivityBean toActivityBean(
            CareActivity activity) {

        return new ActivityBean(
                activity.getId(),
                toPetBean(activity.getPet()),
                toVeterinarianBean(
                        activity.getVeterinarian()
                ),
                activity.getDescription(),
                activity.isCompleted(),
                activity.getCreatedAt()
        );
    }

    private PetBean toPetBean(Pet pet) {
        return new PetBean(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getBirthDate()
        );
    }

    private VeterinarianBean toVeterinarianBean(
            Veterinarian veterinarian) {

        return new VeterinarianBean(
                veterinarian.getId(),
                veterinarian.getName(),
                veterinarian.getSurname(),
                veterinarian.getBio(),
                veterinarian.getEmail(),
                veterinarian.getSpecialization(),
                false
        );
    }
}