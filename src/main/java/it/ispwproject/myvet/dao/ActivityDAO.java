package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.CareActivity;

import java.util.List;

public interface ActivityDAO {

    void save(CareActivity activity) throws DAOException;

    List<CareActivity> getByPetAndVeterinarian(
            int veterinarianId,
            int petId
    ) throws DAOException;

    List<CareActivity> getByPetOwner(
            int petOwnerId
    ) throws DAOException;

    void markAsCompleted(
            int activityId,
            int petOwnerId
    ) throws DAOException;

    CareActivity findByIdForOwner(
            int activityId,
            int petOwnerId
    ) throws DAOException;
}