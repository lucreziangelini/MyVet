package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.MedicalDocumentBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.VeterinarianBean;
import it.ispwproject.myvet.dao.DAOFactory;
import it.ispwproject.myvet.dao.MedicalDocumentDAO;
import it.ispwproject.myvet.dao.PetDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.MedicalDocument;
import it.ispwproject.myvet.model.Pet;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.model.User;
import it.ispwproject.myvet.model.Veterinarian;
import it.ispwproject.myvet.pattern.singleton.SessionManager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class MedicalDocumentController {

    private final MedicalDocumentDAO medicalDocumentDAO;
    private final PetDAO petDAO;

    public MedicalDocumentController() {
        this.medicalDocumentDAO =
                DAOFactory.getMedicalDocumentDAO();
        this.petDAO = DAOFactory.getPetDAO();
    }

    public void uploadDocument(MedicalDocumentBean bean)
            throws DAOException {

        User loggedUser = getLoggedUser();

        if (!(loggedUser instanceof Veterinarian veterinarian)) {
            throw new DAOException(
                    "Solo un veterinario può caricare documenti medici."
            );
        }

        if (bean == null || bean.getPet() == null) {
            throw new DAOException(
                    "Dati del documento incompleti."
            );
        }

        if (bean.getTitle() == null
                || bean.getTitle().isBlank()
                || bean.getType() == null
                || bean.getStorageReference() == null
                || bean.getStorageReference().isBlank()) {
            throw new DAOException(
                    "Titolo, tipo e riferimento del documento sono obbligatori."
            );
        }

        Pet pet = petDAO.findById(
                bean.getPet().getId()
        );

        if (pet == null) {
            throw new DAOException(
                    "Animale non trovato."
            );
        }

        validateVeterinarianAccess(veterinarian, pet);

        LocalDateTime uploadedAt =
                LocalDateTime.now(
                        ZoneId.systemDefault()
                );

        MedicalDocument document =
                new MedicalDocument(
                        0,
                        pet,
                        veterinarian,
                        bean.getTitle(),
                        bean.getType(),
                        bean.getStorageReference(),
                        uploadedAt
                );

        medicalDocumentDAO.save(document);

        bean.setId(document.getId());
        bean.setPet(toPetBean(pet));
        bean.setVeterinarian(
                toVeterinarianBean(veterinarian)
        );
        bean.setUploadedAt(uploadedAt);
    }

    public List<MedicalDocumentBean> getDocuments(
            int petId) throws DAOException {

        Pet pet = petDAO.findById(petId);

        if (pet == null) {
            throw new DAOException(
                    "Animale non trovato."
            );
        }

        validateDocumentAccess(getLoggedUser(), pet);

        List<MedicalDocumentBean> result =
                new ArrayList<>();

        for (MedicalDocument document :
                medicalDocumentDAO.findByPet(petId)) {

            result.add(toMedicalDocumentBean(document));
        }

        return result;
    }

    private User getLoggedUser() throws DAOException {
        User loggedUser = SessionManager
                .getInstance()
                .getLoggedUser();

        if (loggedUser == null) {
            throw new DAOException(
                    "Nessun utente autenticato."
            );
        }

        return loggedUser;
    }

    private void validateDocumentAccess(
            User loggedUser,
            Pet pet) throws DAOException {

        if (loggedUser instanceof PetOwner owner) {
            boolean ownsPet = petDAO
                    .getByOwner(owner.getId())
                    .stream()
                    .anyMatch(ownedPet ->
                            ownedPet.getId() == pet.getId());

            if (!ownsPet) {
                throw new DAOException(
                        "Non puoi visualizzare i documenti di questo animale."
                );
            }

            return;
        }

        if (loggedUser instanceof Veterinarian veterinarian) {
            validateVeterinarianAccess(veterinarian, pet);
            return;
        }

        throw new DAOException(
                "Non sei autorizzato a visualizzare documenti medici."
        );
    }

    private void validateVeterinarianAccess(
            Veterinarian veterinarian,
            Pet pet) throws DAOException {

        boolean assignedPet = petDAO
                .getByVeterinarian(veterinarian.getId())
                .stream()
                .anyMatch(availablePet ->
                        availablePet.getId() == pet.getId());

        if (!assignedPet) {
            throw new DAOException(
                    "Il veterinario non è associato a questo animale."
            );
        }
    }

    private MedicalDocumentBean toMedicalDocumentBean(
            MedicalDocument document) {

        return new MedicalDocumentBean(
                document.getId(),
                toPetBean(document.getPet()),
                toVeterinarianBean(
                        document.getVeterinarian()
                ),
                document.getTitle(),
                document.getType(),
                document.getStorageReference(),
                document.getUploadedAt()
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
