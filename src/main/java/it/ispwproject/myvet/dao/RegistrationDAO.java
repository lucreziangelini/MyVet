package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.User;

public interface RegistrationDAO {

    boolean emailExists(String email) throws DAOException;

    void save(User user) throws DAOException;
}