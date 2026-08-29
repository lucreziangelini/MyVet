package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.User;

import java.util.List;

public interface UserDAO {

    User findByEmail(String email) throws DAOException;

    void updateEmail(int userId, String newEmail)
            throws DAOException;

    List<User> getAll() throws DAOException;
}