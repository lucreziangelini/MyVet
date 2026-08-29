package it.ispwproject.myvet.model;

import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.pattern.observer.Observable;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Booking extends Observable {

    private int id;

    private PetOwner petOwner;
    private Veterinarian veterinarian;
    private Pet pet;
    private TimeSlot timeSlot;

    private BookingStatus status;
    private LocalDateTime createdAt;

    public Booking() {}

    public Booking(PetOwner petOwner,
                   Veterinarian veterinarian,
                   Pet pet,
                   TimeSlot timeSlot) {
        this.petOwner         = petOwner;
        this.veterinarian     = veterinarian;
        this.pet              = pet;
        this.timeSlot         = timeSlot;
        this.status           = BookingStatus.PENDING;
        this.createdAt        = LocalDateTime.now(ZoneId.systemDefault());
    }

    public void confirm() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending booking can be confirmed"
            );
        }

        if (timeSlot == null) {
            throw new IllegalStateException(
                    "The selected time slot is not available"
            );
        }

        if (timeSlot.isReserved()) {
            timeSlot.confirmReservation();
        } else if (timeSlot.isAvailable()) {
            timeSlot.reserve();
        } else {
            throw new IllegalStateException(
                    "The selected time slot is not available"
            );
        }

        status = BookingStatus.CONFIRMED;
        notifyObservers();
    }

    public void cancel() {
        if (status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only a confirmed booking can be cancelled"
            );
        }

        status = BookingStatus.CANCELLED;

        if (timeSlot != null) {
            timeSlot.release();
        }

        notifyObservers();
    }


    public boolean belongsTo(PetOwner owner) {
        return petOwner != null
                && owner != null
                && petOwner.getId() == owner.getId();
    }

    public boolean isCancellable() {
        if (status != BookingStatus.CONFIRMED || timeSlot == null
                || timeSlot.getDate() == null || timeSlot.getStartTime() == null) {
            return false;
        }

        LocalDateTime appointmentStart = LocalDateTime.of(
                timeSlot.getDate(),
                timeSlot.getStartTime()
        );

        return appointmentStart.isAfter(
                LocalDateTime.now(ZoneId.systemDefault())
        );
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public PetOwner getPetOwner() {return petOwner;}
    public void setPetOwner(PetOwner petOwner) {this.petOwner = petOwner;}

    public Veterinarian getVeterinarian() {return veterinarian;}
    public void setVeterinarian(Veterinarian veterinarian) {this.veterinarian = veterinarian;}

    public Pet getPet() {return pet;}
    public void setPet(Pet pet) {this.pet = pet;}

    public TimeSlot getTimeSlot() {return timeSlot;}
    public void setTimeSlot(TimeSlot timeSlot) {this.timeSlot = timeSlot;}

    public BookingStatus getStatus() {return status;}
    public void setStatus(BookingStatus status) {this.status = status;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}
