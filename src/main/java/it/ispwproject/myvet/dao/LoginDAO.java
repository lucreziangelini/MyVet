package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.LoginException;
import it.ispwproject.myvet.model.Credentials;

public interface LoginDAO {

    Credentials execute(String email, String password)
            throws LoginException;
}