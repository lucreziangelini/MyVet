package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Booking;

import java.util.List;

public interface BookingDAO {

    void save(Booking booking) throws DAOException;

    List<Booking> findByPetOwner(int petOwnerId)
            throws DAOException;

    List<Booking> findByVeterinarian(int veterinarianId)
            throws DAOException;

    List<Booking> findPastByPetOwner(int petOwnerId)
            throws DAOException;

    void cancel(
            int bookingId,
            int petOwnerId
    ) throws DAOException;

    List<Booking> findAll() throws DAOException;
}