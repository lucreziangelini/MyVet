package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.dao.db.*;
import it.ispwproject.myvet.dao.file.BookingDAOFile;
import it.ispwproject.myvet.dao.file.TimeSlotDAOFile;
import it.ispwproject.myvet.dao.memory.*;

import java.util.Locale;
import java.util.Set;

public final class DAOFactory {

    public static final String DATABASE = "database";
    public static final String FILE = "file";
    public static final String MEMORY = "memory";

    private static final Set<String> SUPPORTED_MODES =
            Set.of(DATABASE, FILE, MEMORY);

    private static String persistence = DATABASE;

    private DAOFactory() {
        // Prevents external instantiation
    }

    public static void setPersistence(String mode) {
        if (mode == null) {
            throw new IllegalArgumentException(
                    "Persistence mode cannot be null"
            );
        }

        String normalizedMode = mode
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!SUPPORTED_MODES.contains(normalizedMode)) {
            throw new IllegalArgumentException(
                    "Unsupported persistence mode: " + mode
            );
        }

        persistence = normalizedMode;
    }

    public static String getPersistence() {
        return persistence;
    }

    public static LoginDAO getLoginDAO() {
        if (MEMORY.equals(persistence)) {
            return new LoginDAOMemory();
        }

        return new LoginDAODB();
    }

    public static RegistrationDAO getRegistrationDAO() {
        if (MEMORY.equals(persistence)) {
            return new RegistrationDAOMemory();
        }

        return new RegistrationDAODB();
    }

    public static UserDAO getUserDAO() {
        if (MEMORY.equals(persistence)) {
            return new UserDAOMemory();
        }

        return new UserDAODB();
    }

    public static PetOwnerDAO getPetOwnerDAO() {
        if (MEMORY.equals(persistence)) {
            return new PetOwnerDAOMemory();
        }

        return new PetOwnerDAODB();
    }

    public static VeterinarianDAO getVeterinarianDAO() {
        if (MEMORY.equals(persistence)) {
            return new VeterinarianDAOMemory();
        }

        return new VeterinarianDAODB();
    }

    public static PetDAO getPetDAO() {
        if (MEMORY.equals(persistence)) {
            return new PetDAOMemory();
        }

        return new PetDAODB();
    }

    public static ActivityDAO getActivityDAO() {
        if (MEMORY.equals(persistence)) {
            return new ActivityDAOMemory();
        }

        return new ActivityDAODB();
    }

    public static ProgressDAO getProgressDAO() {
        if (MEMORY.equals(persistence)) {
            return new ProgressDAOMemory();
        }

        return new ProgressDAODB();
    }

    public static MedicalDocumentDAO getMedicalDocumentDAO() {
        if (MEMORY.equals(persistence)) {
            return new MedicalDocumentDAOMemory();
        }

        return new MedicalDocumentDAODB();
    }

    public static BookingDAO getBookingDAO() {
        return switch (persistence) {
            case FILE -> new BookingDAOFile();
            case MEMORY -> new BookingDAOMemory();
            default -> new BookingDAODB();
        };
    }

    public static TimeSlotDAO getTimeSlotDAO() {
        return switch (persistence) {
            case FILE -> new TimeSlotDAOFile();
            case MEMORY -> new TimeSlotDAOMemory();
            default -> new TimeSlotDAODB();
        };
    }
}