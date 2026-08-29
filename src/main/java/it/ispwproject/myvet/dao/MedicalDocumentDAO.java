package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.MedicalDocument;

import java.util.List;

public interface MedicalDocumentDAO {

    void save(MedicalDocument document) throws DAOException;

    List<MedicalDocument> findByPet(int petId)
            throws DAOException;
}