package it.ispwproject.myvet.dao.file;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.myvet.dao.AbstractBookingDAO;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Booking;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.model.TimeSlot;
import it.ispwproject.myvet.util.logger.AppLogger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOFile extends AbstractBookingDAO {

    private static final String FILE_PATH =
            "bookings.json";

    private static final String SLOTS_FILE_PATH =
            "timeslots.json";

    private final Gson gson;

    public BookingDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(
                        LocalDateTime.class,
                        new LocalDateTimeAdapter()
                )
                .registerTypeAdapter(
                        LocalDate.class,
                        new LocalDateAdapter()
                )
                .registerTypeAdapter(
                        LocalTime.class,
                        new LocalTimeAdapter()
                )
                .addSerializationExclusionStrategy(
                        new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(
                                    FieldAttributes field) {

                                return field.getName()
                                        .equals("observers");
                            }

                            @Override
                            public boolean shouldSkipClass(
                                    Class<?> clazz) {

                                return false;
                            }
                        }
                )
                .addDeserializationExclusionStrategy(
                        new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(
                                    FieldAttributes field) {

                                return field.getName()
                                        .equals("observers");
                            }

                            @Override
                            public boolean shouldSkipClass(
                                    Class<?> clazz) {

                                return false;
                            }
                        }
                )
                .setPrettyPrinting()
                .create();

        loadAllFromFile().forEach(
                this::addToCache
        );
    }

    @Override
    public void save(Booking booking)
            throws DAOException {

        booking.setId(generateId());
        booking.setStatus(
                BookingStatus.CONFIRMED
        );

        addToCache(booking);
        saveToFile();

        markSlotAsBooked(
                booking.getTimeSlot().getId()
        );
    }

    @Override
    public List<Booking> findByPetOwner(
            int petOwnerId) throws DAOException {

        List<Booking> cached =
                findInCacheByPetOwner(petOwnerId);

        if (!cached.isEmpty()) {
            return cached;
        }

        loadAllFromFile().forEach(
                this::addToCache
        );

        return findInCacheByPetOwner(
                petOwnerId
        );
    }

    @Override
    public List<Booking> findByVeterinarian(
            int veterinarianId) throws DAOException {

        return identityMap.stream()
                .filter(booking ->
                        booking.getVeterinarian() != null
                                && booking.getVeterinarian()
                                .getId() == veterinarianId
                                && booking.getStatus()
                                == BookingStatus.CONFIRMED
                )
                .toList();
    }

    @Override
    public List<Booking> findPastByPetOwner(
            int petOwnerId) throws DAOException {

        LocalDate today =
                LocalDate.now(
                        ZoneId.systemDefault()
                );

        LocalTime now =
                LocalTime.now(
                        ZoneId.systemDefault()
                );

        return identityMap.stream()
                .filter(booking ->
                        booking.getPetOwner() != null
                                && booking.getPetOwner()
                                .getId() == petOwnerId
                )
                .filter(booking ->
                        booking.getStatus()
                                == BookingStatus.CONFIRMED
                )
                .filter(booking ->
                        booking.getTimeSlot() != null
                )
                .filter(booking ->
                        booking.getTimeSlot()
                                .getDate()
                                .isBefore(today)
                                || (booking.getTimeSlot()
                                .getDate()
                                .isEqual(today)
                                && booking.getTimeSlot()
                                .getEndTime()
                                .isBefore(now))
                )
                .toList();
    }

    @Override
    public List<Booking> findAll()
            throws DAOException {

        return new ArrayList<>(
                identityMap
        );
    }

    @Override
    public void cancel(
            int bookingId,
            int petOwnerId) throws DAOException {

        Booking booking =
                findInCache(bookingId);

        if (booking == null) {
            throw new DAOException(
                    "Appuntamento non trovato (ID: "
                            + bookingId + ")"
            );
        }

        PetOwner owner =
                booking.getPetOwner();

        if (owner == null
                || owner.getId() != petOwnerId) {

            throw new DAOException(
                    "Non puoi annullare un appuntamento "
                            + "che non ti appartiene."
            );
        }

        try {
            booking.cancel();
        } catch (IllegalStateException e) {
            throw new DAOException(
                    "L'appuntamento non può essere annullato: "
                            + e.getMessage(),
                    e
            );
        }

        saveToFile();

        markSlotAsAvailable(
                booking.getTimeSlot().getId()
        );
    }

    private int generateId() {
        return identityMap.stream()
                .mapToInt(Booking::getId)
                .max()
                .orElse(0) + 1;
    }

    private List<Booking> loadAllFromFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader =
                     new FileReader(file)) {

            Type listType =
                    new TypeToken<List<Booking>>() {
                    }.getType();

            List<Booking> loaded =
                    gson.fromJson(
                            reader,
                            listType
                    );

            return loaded != null
                    ? loaded
                    : new ArrayList<>();

        } catch (IOException e) {
            AppLogger.logError(
                    "Errore caricamento bookings da file: "
                            + e.getMessage()
            );

            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try (Writer writer =
                     new FileWriter(FILE_PATH)) {

            gson.toJson(
                    identityMap,
                    writer
            );

        } catch (IOException e) {
            AppLogger.logError(
                    "Errore salvataggio bookings su file: "
                            + e.getMessage()
            );
        }
    }

    private void markSlotAsBooked(int slotId) {
        updateSlotAvailability(
                slotId,
                false
        );
    }

    private void markSlotAsAvailable(int slotId) {
        updateSlotAvailability(
                slotId,
                true
        );
    }

    private void updateSlotAvailability(
            int slotId,
            boolean available) {

        File file =
                new File(SLOTS_FILE_PATH);

        if (!file.exists()) {
            return;
        }

        try (Reader reader =
                     new FileReader(file)) {

            Type listType =
                    new TypeToken<List<TimeSlot>>() {
                    }.getType();

            List<TimeSlot> slots =
                    gson.fromJson(
                            reader,
                            listType
                    );

            if (slots == null) {
                return;
            }

            slots.stream()
                    .filter(slot ->
                            slot.getId() == slotId)
                    .findFirst()
                    .ifPresent(slot -> {
                        slot.setAvailable(
                                available
                        );

                        slot.setReservedUntil(null);
                    });

            try (Writer writer =
                         new FileWriter(file)) {

                gson.toJson(
                        slots,
                        writer
                );
            }

        } catch (IOException e) {
            AppLogger.logError(
                    "Errore nell'aggiornamento della fascia oraria: "
                            + e.getMessage()
            );
        }
    }
}
