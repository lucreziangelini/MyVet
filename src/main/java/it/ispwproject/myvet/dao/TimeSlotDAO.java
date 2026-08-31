package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.TimeSlot;
import it.ispwproject.myvet.model.Veterinarian;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotDAO {

    List<TimeSlot> getAvailableByVeterinarian(
            Veterinarian veterinarian
    ) throws DAOException;

    List<TimeSlot> getAvailableByVeterinarianAndDate(
            int veterinarianId,
            LocalDate date
    ) throws DAOException;

    List<Integer> getAvailableVeterinarianIds(
            LocalDate date
    ) throws DAOException;

    List<TimeSlot> getAllByVeterinarian(
            int veterinarianId
    ) throws DAOException;

    List<TimeSlot> getPastByVeterinarian(
            int veterinarianId
    ) throws DAOException;

    TimeSlot findById(int id) throws DAOException;

    void save(
            TimeSlot slot,
            int veterinarianId
    ) throws DAOException;

    boolean reserveSlot(
            int slotId,
            int minutes
    ) throws DAOException;

    void releaseSlot(int slotId) throws DAOException;

    void delete(
            int slotId,
            int veterinarianId
    ) throws DAOException;
}
