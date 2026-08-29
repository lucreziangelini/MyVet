package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.ProgressDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Progress;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ProgressDAOMemory implements ProgressDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public void saveOrUpdate(Progress progress)
            throws DAOException {

        Progress existing =
                findByPetAndVeterinarian(
                        progress.getVeterinarian().getId(),
                        progress.getPet().getId()
                );

        if (existing == null) {
            progress.setId(store.nextProgressId());
            progress.setUpdatedAt(
                    LocalDateTime.now(
                            ZoneId.systemDefault()
                    )
            );
            store.getProgresses().add(progress);
        } else {
            existing.setNotes(progress.getNotes());
            existing.setUpdatedAt(
                    LocalDateTime.now(
                            ZoneId.systemDefault()
                    )
            );
        }
    }

    @Override
    public Progress findByPetAndVeterinarian(
            int veterinarianId,
            int petId) throws DAOException {

        return store.getProgresses().stream()
                .filter(progress ->
                        progress.getVeterinarian() != null
                                && progress.getVeterinarian().getId()
                                == veterinarianId
                                && progress.getPet() != null
                                && progress.getPet().getId() == petId)
                .findFirst()
                .orElse(null);
    }
}