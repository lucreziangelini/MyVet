package it.ispwproject.myvet.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class TimeSlot {

    private int id;
    private Veterinarian veterinarian;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
    private LocalDateTime reservedUntil;

    public TimeSlot() {
        // Used when reconstructing a TimeSlot from persistence
    }

    public TimeSlot(int id,
                    LocalDate date,
                    LocalTime startTime,
                    LocalTime endTime) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = true;
    }

    public TimeSlot(int id,
                    Veterinarian veterinarian,
                    LocalDate date,
                    LocalTime startTime,
                    LocalTime endTime) {
        this.id = id;
        this.veterinarian = veterinarian;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = true;
    }

    public void reserve() {
        if (!available) {
            throw new IllegalStateException(
                    "La fascia oraria non è disponibile"
            );
        }

        available = false;
        reservedUntil = null;
    }

    public void reserveForMinutes(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException(
                    "La durata della prenotazione deve essere maggiore di zero"
            );
        }

        releaseExpiredReservation();

        if (!available) {
            throw new IllegalStateException(
                    "La fascia oraria non è disponibile"
            );
        }

        available = false;
        reservedUntil = LocalDateTime.now(ZoneId.systemDefault())
                .plusMinutes(minutes);
    }

    public void confirmReservation() {
        if (!isReserved()) {
            throw new IllegalStateException(
                    "La prenotazione temporanea della fascia oraria è scaduta"
            );
        }

        reservedUntil = null;
    }

    public void release() {
        available = true;
        reservedUntil = null;
    }

    public boolean isReserved() {
        releaseExpiredReservation();
        return !available && reservedUntil != null;
    }

    private void releaseExpiredReservation() {
        if (reservedUntil != null
                && !reservedUntil.isAfter(
                        LocalDateTime.now(ZoneId.systemDefault()))) {
            release();
        }
    }

    public boolean overlaps(TimeSlot other) {
        return other != null
                && date != null
                && date.equals(other.date)
                && startTime.isBefore(other.endTime)
                && endTime.isAfter(other.startTime);
    }

    @Override
    public String toString() {
        return date + " " + startTime + " - " + endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Veterinarian getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(Veterinarian veterinarian) {
        this.veterinarian = veterinarian;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        releaseExpiredReservation();
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
        if (available) {
            reservedUntil = null;
        }
    }

    public LocalDateTime getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(LocalDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
        if (reservedUntil != null) {
            this.available = false;
        }
    }
}
