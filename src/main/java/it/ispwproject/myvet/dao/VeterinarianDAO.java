package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Veterinarian;

import java.time.LocalDate;
import java.util.List;

public interface VeterinarianDAO {

    List<Veterinarian> getAvailableByDate(LocalDate date)
            throws DAOException;

    Veterinarian findById(int id) throws DAOException;
}