package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.PetOwner;

import java.util.List;

public interface PetOwnerDAO {

    PetOwner findById(int id) throws DAOException;

    PetOwner findByPetId(int petId) throws DAOException;

    List<PetOwner> getByVeterinarian(int veterinarianId)
            throws DAOException;

    void addFavouriteVeterinarian(
            int petOwnerId,
            int veterinarianId
    ) throws DAOException;

    void removeFavouriteVeterinarian(
            int petOwnerId,
            int veterinarianId
    ) throws DAOException;

    boolean isFavouriteVeterinarian(
            int petOwnerId,
            int veterinarianId
    ) throws DAOException;
}