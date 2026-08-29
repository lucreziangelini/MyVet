package it.ispwproject.myvet.bean;

import it.ispwproject.myvet.enumerator.BookingStatus;

public class BookingResponseBean {

    private int id;
    private BookingStatus status;
    private PetOwnerBean petOwner;
    private PetBean pet;
    private VeterinarianBean veterinarian;
    private TimeSlotBean timeSlot;

    public BookingResponseBean() {
    }

    public BookingResponseBean(int id,
                               BookingStatus status,
                               PetOwnerBean petOwner,
                               PetBean pet,
                               VeterinarianBean veterinarian,
                               TimeSlotBean timeSlot) {
        this.id = id;
        this.status = status;
        this.petOwner = petOwner;
        this.pet = pet;
        this.veterinarian = veterinarian;
        this.timeSlot = timeSlot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public PetOwnerBean getPetOwner() {
        return petOwner;
    }

    public void setPetOwner(PetOwnerBean petOwner) {
        this.petOwner = petOwner;
    }

    public PetBean getPet() {
        return pet;
    }

    public void setPet(PetBean pet) {
        this.pet = pet;
    }

    public VeterinarianBean getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(VeterinarianBean veterinarian) {
        this.veterinarian = veterinarian;
    }

    public TimeSlotBean getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlotBean timeSlot) {
        this.timeSlot = timeSlot;
    }
}