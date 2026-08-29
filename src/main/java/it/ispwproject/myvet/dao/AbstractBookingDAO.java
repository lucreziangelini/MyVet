package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.model.Booking;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBookingDAO implements BookingDAO {

    // Identity Map — mantiene le istanze di Booking già caricate
    protected final List<Booking> identityMap = new ArrayList<>();

    protected Booking findInCache(int id) {
        return identityMap.stream()
                .filter(booking -> booking.getId() == id)
                .findFirst()
                .orElse(null);
    }

    protected List<Booking> findInCacheByPetOwner(int petOwnerId) {
        return identityMap.stream()
                .filter(booking ->
                        booking.getPetOwner() != null
                                && booking.getPetOwner().getId() == petOwnerId)
                .toList();
    }

    protected void addToCache(Booking booking) {
        if (findInCache(booking.getId()) == null) {
            identityMap.add(booking);
        }
    }

    protected void updateInCache(int bookingId) {
        Booking cachedBooking = findInCache(bookingId);

        if (cachedBooking != null) {
            cachedBooking.cancel();
        }
    }
}