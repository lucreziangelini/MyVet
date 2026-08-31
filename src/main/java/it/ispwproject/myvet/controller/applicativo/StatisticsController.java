package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.StatisticsBean;
import it.ispwproject.myvet.dao.BookingDAO;
import it.ispwproject.myvet.dao.DAOFactory;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Booking;
import it.ispwproject.myvet.pattern.singleton.SessionManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsController {

    private final BookingDAO bookingDAO;

    public StatisticsController() {
        this.bookingDAO = DAOFactory.getBookingDAO();
    }

    public StatisticsBean getStatistics()
            throws DAOException {

        if (!SessionManager.getInstance().isAdmin()) {
            throw new DAOException(
                    "Solo un amministratore può visualizzare le statistiche."
            );
        }

        List<Booking> all = bookingDAO.findAll();

        int total = all.size();

        int cancelled = (int) all.stream()
                .filter(booking ->
                        booking.getStatus()
                                == BookingStatus.CANCELLED)
                .count();

        double cancellationRate =
                total == 0
                        ? 0.0
                        : Math.round(
                        (cancelled * 100.0 / total)
                                * 10.0
                ) / 10.0;

        Map<String, Integer> topVeterinarians =
                all.stream()
                        .filter(booking ->
                                booking.getVeterinarian() != null
                                        && booking.getStatus()
                                        != BookingStatus.CANCELLED)
                        .collect(Collectors.groupingBy(
                                booking ->
                                        booking.getVeterinarian()
                                                .getName()
                                                + " "
                                                + booking.getVeterinarian()
                                                .getSurname(),
                                Collectors.collectingAndThen(
                                        Collectors.counting(),
                                        Long::intValue
                                )
                        ))
                        .entrySet()
                        .stream()
                        .sorted(
                                Map.Entry
                                        .<String, Integer>
                                                comparingByValue()
                                        .reversed()
                        )
                        .limit(3)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (first, second) -> first,
                                LinkedHashMap::new
                        ));

        return new StatisticsBean(
                total,
                cancelled,
                cancellationRate,
                topVeterinarians
        );
    }
}
