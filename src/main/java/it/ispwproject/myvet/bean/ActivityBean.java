package it.ispwproject.myvet.bean;

import java.time.LocalDateTime;

public class ActivityBean {

    private int id;
    private PetBean pet;
    private VeterinarianBean veterinarian;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public ActivityBean() {
    }

    public ActivityBean(int id,
                        PetBean pet,
                        VeterinarianBean veterinarian,
                        String description,
                        boolean completed,
                        LocalDateTime createdAt) {
        this.id = id;
        this.pet = pet;
        this.veterinarian = veterinarian;
        this.description = description;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    // Costruttore utilizzabile quando il veterinario non è ancora associato
    public ActivityBean(int id,
                        PetBean pet,
                        String description,
                        boolean completed,
                        LocalDateTime createdAt) {
        this(id, pet, null, description, completed, createdAt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
