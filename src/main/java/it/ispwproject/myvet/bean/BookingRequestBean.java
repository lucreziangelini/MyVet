package it.ispwproject.myvet.bean;

public class BookingRequestBean {

    private PetOwnerBean petOwner;
    private PetBean pet;
    private VeterinarianBean veterinarian;
    private TimeSlotBean timeSlot;

    public BookingRequestBean() {
    }

    public BookingRequestBean(PetOwnerBean petOwner,
                              PetBean pet,
                              VeterinarianBean veterinarian,
                              TimeSlotBean timeSlot) {
        this.petOwner = petOwner;
        this.pet = pet;
        this.veterinarian = veterinarian;
        this.timeSlot = timeSlot;
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