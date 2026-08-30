package it.ispwproject.myvet.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Progress {

    private int id;

    private Veterinarian veterinarian;
    private Pet pet;

    private String notes;
    private LocalDateTime updatedAt;

    public Progress() {}

    public Progress(Veterinarian veterinarian, Pet pet, String notes) {
        this.veterinarian = veterinarian;
        this.pet = pet;
        this.notes = notes;
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

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public String getNotes() {
        return notes;
    }

    public void updateNotes(String newNotes) {
        this.notes = newNotes;
        this.updatedAt = LocalDateTime.now(ZoneId.systemDefault());
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