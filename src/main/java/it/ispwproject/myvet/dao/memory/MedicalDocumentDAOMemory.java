package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.MedicalDocumentDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.MedicalDocument;

import java.util.List;

public class MedicalDocumentDAOMemory
        implements MedicalDocumentDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public void save(MedicalDocument document)
            throws DAOException {

        document.setId(
                store.nextMedicalDocumentId()
        );

        store.getMedicalDocuments().add(document);
    }

    @Override
    public List<MedicalDocument> findByPet(
            int petId) throws DAOException {

        return store.getMedicalDocuments().stream()
                .filter(document ->
                        document.getPet() != null
                                && document.getPet().getId()
                                == petId)
                .toList();
    }
}