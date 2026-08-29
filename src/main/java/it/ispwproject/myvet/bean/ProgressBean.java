package it.ispwproject.myvet.bean;

import java.time.LocalDateTime;

public class ProgressBean {

    private PetBean pet;
    private String notes;
    private LocalDateTime updatedAt;

    public ProgressBean() {
    }

    public ProgressBean(PetBean pet,
                        String notes,
                        LocalDateTime updatedAt) {
        this.pet = pet;
        this.notes = notes;
        this.updatedAt = updatedAt;
    }

    public PetBean getPet() {
        return pet;
    }

    public void setPet(PetBean pet) {
        this.pet = pet;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
