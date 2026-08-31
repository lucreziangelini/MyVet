package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Pet;

import java.util.List;

public interface PetDAO {

    void save(Pet pet, int petOwnerId) throws DAOException;

    void update(Pet pet, int petOwnerId) throws DAOException;

    Pet findById(int petId) throws DAOException;

    List<Pet> getByOwner(int petOwnerId) throws DAOException;

    List<Pet> getByVeterinarian(int veterinarianId)
            throws DAOException;
}
