package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Progress;

public interface ProgressDAO {

    void saveOrUpdate(Progress progress) throws DAOException;

    Progress findByPetAndVeterinarian(
            int veterinarianId,
            int petId
    ) throws DAOException;
}