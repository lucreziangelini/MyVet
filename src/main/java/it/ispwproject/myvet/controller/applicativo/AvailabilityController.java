package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.TimeSlotBean;
import it.ispwproject.myvet.dao.BookingDAO;
import it.ispwproject.myvet.dao.DAOFactory;
import it.ispwproject.myvet.dao.TimeSlotDAO;
import it.ispwproject.myvet.dao.UserDAO;
import it.ispwproject.myvet.exception.AvailabilityException;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Booking;
import it.ispwproject.myvet.model.TimeSlot;
import it.ispwproject.myvet.model.Veterinarian;
import it.ispwproject.myvet.pattern.singleton.SessionManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailabilityController {

    private final TimeSlotDAO timeSlotDAO;
    private final BookingDAO bookingDAO;
    private final UserDAO userDAO;

    public AvailabilityController() {
        this.timeSlotDAO = DAOFactory.getTimeSlotDAO();
        this.bookingDAO = DAOFactory.getBookingDAO();
        this.userDAO = DAOFactory.getUserDAO();
    }

    public void addSlot(TimeSlotBean slotBean)
            throws DAOException, AvailabilityException {

        if (slotBean.getDate().isBefore(
                LocalDate.now(ZoneId.systemDefault()))) {
            throw new AvailabilityException(
                    "Non puoi aggiungere slot nel passato."
            );
        }

        if (!slotBean.getStartTime().isBefore(
                slotBean.getEndTime())) {
            throw new AvailabilityException(
                    "L'ora di inizio deve essere precedente "
                            + "all'ora di fine."
            );
        }

        Veterinarian veterinarian = getLoggedVeterinarian();

        TimeSlot newSlot = new TimeSlot(
                0,
                veterinarian,
                slotBean.getDate(),
                slotBean.getStartTime(),
                slotBean.getEndTime()
        );

        for (TimeSlot slot :
                timeSlotDAO.getAvailableByVeterinarian(
                        veterinarian)) {

            if (newSlot.overlaps(slot)) {
                throw new AvailabilityException(
                        "Lo slot si sovrappone con uno già esistente: "
                                + slot.getDate() + " "
                                + slot.getStartTime() + " - "
                                + slot.getEndTime()
                );
            }
        }

        timeSlotDAO.save(
                newSlot,
                veterinarian.getId()
        );

        slotBean.setId(newSlot.getId());
    }

    public List<TimeSlotBean> getSlots()
            throws DAOException {

        Veterinarian veterinarian = getLoggedVeterinarian();

        return buildSlotBeans(
                timeSlotDAO.getAllByVeterinarian(
                        veterinarian.getId()
                ),
                veterinarian
        );
    }

    public List<TimeSlotBean> getPastSlots()
            throws DAOException {

        Veterinarian veterinarian = getLoggedVeterinarian();

        return buildSlotBeans(
                timeSlotDAO.getPastByVeterinarian(
                        veterinarian.getId()
                ),
                veterinarian
        );
    }

    public Map<Integer, String> getPetBySlot()
            throws DAOException {

        Veterinarian veterinarian = getLoggedVeterinarian();
        Map<Integer, String> result = new HashMap<>();

        for (Booking booking :
                bookingDAO.findByVeterinarian(
                        veterinarian.getId())) {

            if (booking.getTimeSlot() != null
                    && booking.getPet() != null) {

                result.put(
                        booking.getTimeSlot().getId(),
                        booking.getPet().getName()
                );
            }
        }

        return result;
    }

    public void deleteSlot(int slotId)
            throws DAOException {

        Veterinarian veterinarian = getLoggedVeterinarian();

        timeSlotDAO.delete(
                slotId,
                veterinarian.getId()
        );
    }

    private List<TimeSlotBean> buildSlotBeans(
            List<TimeSlot> slots,
            Veterinarian veterinarian)
            throws DAOException {

        Map<Integer, Booking> bookingBySlot =
                new HashMap<>();

        for (Booking booking :
                bookingDAO.findByVeterinarian(
                        veterinarian.getId())) {

            if (booking.getTimeSlot() != null) {
                bookingBySlot.put(
                        booking.getTimeSlot().getId(),
                        booking
                );
            }
        }

        List<TimeSlotBean> result = new ArrayList<>();

        for (TimeSlot slot : slots) {
            TimeSlotBean bean = new TimeSlotBean(
                    slot.getId(),
                    slot.getDate(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.isAvailable()
            );

            Booking booking =
                    bookingBySlot.get(slot.getId());

            if (booking != null
                    && booking.getPet() != null) {
                bean.setBookedPetName(
                        booking.getPet().getName()
                );
            }

            result.add(bean);
        }

        return result;
    }

    private Veterinarian getLoggedVeterinarian()
            throws DAOException {

        String email = SessionManager
                .getInstance()
                .getLoggedUser()
                .getEmail();

        return (Veterinarian) userDAO.findByEmail(email);
    }
}