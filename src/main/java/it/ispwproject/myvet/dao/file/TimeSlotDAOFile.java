package it.ispwproject.myvet.dao.file;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.myvet.dao.TimeSlotDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.TimeSlot;
import it.ispwproject.myvet.model.Veterinarian;
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

public class TimeSlotDAOFile implements TimeSlotDAO {

    private static final String FILE_PATH =
            "timeslots.json";

    private final Gson gson;
    private final List<TimeSlot> cache;

    public TimeSlotDAOFile() {
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

        this.cache = loadFromFile();
    }

    @Override
    public List<TimeSlot> getAvailableByVeterinarian(
            Veterinarian veterinarian)
            throws DAOException {

        LocalDate today =
                LocalDate.now(
                        ZoneId.systemDefault()
                );

        LocalTime now =
                LocalTime.now(
                        ZoneId.systemDefault()
                );

        return cache.stream()
                .filter(slot ->
                        slot.getVeterinarian() != null
                                && slot.getVeterinarian().getId()
                                == veterinarian.getId()
                )
                .filter(TimeSlot::isAvailable)
                .filter(slot ->
                        slot.getDate().isAfter(today)
                                || (slot.getDate().isEqual(today)
                                && slot.getStartTime().isAfter(now))
                )
                .toList();
    }

    @Override
    public List<TimeSlot> getAvailableByVeterinarianAndDate(
            int veterinarianId,
            LocalDate date) throws DAOException {

        LocalDate today =
                LocalDate.now(
                        ZoneId.systemDefault()
                );

        LocalTime now =
                LocalTime.now(
                        ZoneId.systemDefault()
                );

        return cache.stream()
                .filter(slot ->
                        slot.getVeterinarian() != null
                                && slot.getVeterinarian().getId()
                                == veterinarianId
                )
                .filter(slot ->
                        slot.getDate().equals(date)
                )
                .filter(TimeSlot::isAvailable)
                .filter(slot ->
                        slot.getDate().isAfter(today)
                                || (slot.getDate().isEqual(today)
                                && slot.getStartTime().isAfter(now))
                )
                .toList();
    }

    @Override
    public List<Integer> getAvailableVeterinarianIds(
            LocalDate date) throws DAOException {

        LocalDate today =
                LocalDate.now(
                        ZoneId.systemDefault()
                );

        LocalTime now =
                LocalTime.now(
                        ZoneId.systemDefault()
                );

        return cache.stream()
                .filter(slot -> slot.getVeterinarian() != null)
                .filter(slot -> slot.getDate().equals(date))
                .filter(TimeSlot::isAvailable)
                .filter(slot ->
                        slot.getDate().isAfter(today)
                                || (slot.getDate().isEqual(today)
                                && slot.getStartTime().isAfter(now))
                )
                .map(slot -> slot.getVeterinarian().getId())
                .distinct()
                .toList();
    }

    @Override
    public List<TimeSlot> getAllByVeterinarian(
            int veterinarianId) throws DAOException {

        LocalDate today =
                LocalDate.now(
                        ZoneId.systemDefault()
                );

        LocalTime now =
                LocalTime.now(
                        ZoneId.systemDefault()
                );

        return cache.stream()
                .filter(slot ->
                        slot.getVeterinarian() != null
                                && slot.getVeterinarian().getId()
                                == veterinarianId
                )
                .filter(slot ->
                        slot.getDate().isAfter(today)
                                || (slot.getDate().isEqual(today)
                                && slot.getStartTime().isAfter(now))
                )
                .toList();
    }

    @Override
    public List<TimeSlot> getPastByVeterinarian(
            int veterinarianId) throws DAOException {

        LocalDate today =
                LocalDate.now(
                        ZoneId.systemDefault()
                );

        LocalTime now =
                LocalTime.now(
                        ZoneId.systemDefault()
                );

        return cache.stream()
                .filter(slot ->
                        slot.getVeterinarian() != null
                                && slot.getVeterinarian().getId()
                                == veterinarianId
                )
                .filter(slot ->
                        slot.getDate().isBefore(today)
                                || (slot.getDate().isEqual(today)
                                && slot.getEndTime().isBefore(now))
                )
                .toList();
    }

    @Override
    public TimeSlot findById(int id)
            throws DAOException {

        return cache.stream()
                .filter(slot ->
                        slot.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(
            TimeSlot slot,
            int veterinarianId) throws DAOException {

        if (slot.getVeterinarian() == null) {
            Veterinarian veterinarian =
                    new Veterinarian();

            veterinarian.setId(veterinarianId);
            slot.setVeterinarian(veterinarian);
        }

        slot.setId(generateId());
        cache.add(slot);
        saveToFile();
    }

    @Override
    public boolean reserveSlot(
            int slotId,
            int minutes) throws DAOException {

        synchronized (cache) {
            TimeSlot slot = cache.stream()
                    .filter(current ->
                            current.getId() == slotId)
                    .findFirst()
                    .orElse(null);

            if (slot == null || !slot.isAvailable()) {
                return false;
            }

            try {
                slot.reserveForMinutes(minutes);
            } catch (IllegalStateException |
                     IllegalArgumentException e) {

                return false;
            }

            saveToFile();
            return true;
        }
    }

    @Override
    public void releaseSlot(int slotId)
            throws DAOException {

        cache.stream()
                .filter(slot ->
                        slot.getId() == slotId)
                .findFirst()
                .ifPresent(slot -> {
                    slot.release();
                    saveToFile();
                });
    }

    @Override
    public void delete(
            int slotId,
            int veterinarianId) throws DAOException {

        boolean removed = cache.removeIf(slot ->
                slot.getId() == slotId
                        && slot.getVeterinarian() != null
                        && slot.getVeterinarian().getId()
                        == veterinarianId
                        && slot.isAvailable()
        );

        if (!removed) {
            throw new DAOException(
                    "Fascia oraria non trovata o già prenotata."
            );
        }

        saveToFile();
    }

    private int generateId() {
        return cache.stream()
                .mapToInt(TimeSlot::getId)
                .max()
                .orElse(0) + 1;
    }

    private List<TimeSlot> loadFromFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader =
                     new FileReader(file)) {

            Type listType =
                    new TypeToken<List<TimeSlot>>() {
                    }.getType();

            List<TimeSlot> loaded =
                    gson.fromJson(
                            reader,
                            listType
                    );

            return loaded != null
                    ? loaded
                    : new ArrayList<>();

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try (Writer writer =
                     new FileWriter(FILE_PATH)) {

            gson.toJson(cache, writer);

        } catch (IOException e) {
            AppLogger.logError(
                    "Errore nel salvataggio delle fasce orarie su file: "
                            + e.getMessage()
            );
        }
    }
}
