package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Pet;

import java.util.List;

public interface PetDAO {

    Pet findById(int petId) throws DAOException;

    List<Pet> getByOwner(int petOwnerId) throws DAOException;

    List<Pet> getByVeterinarian(int veterinarianId)
            throws DAOException;
}