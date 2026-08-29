package it.ispwproject.myvet.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class CareActivity {

    private int id;

    private Veterinarian veterinarian;
    private Pet pet;

    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public CareActivity() {
        // Used when reconstructing an activity from persistence
    }

    public CareActivity(Veterinarian veterinarian,
                        Pet pet,
                        String title,
                        String description) {
        this.veterinarian = veterinarian;
        this.pet          = pet;
        this.title        = title;
        this.description  = description;
        this.completed    = false;
        this.createdAt    = LocalDateTime.now(ZoneId.systemDefault());
    }

    public void complete() {
        if (!completed) {
            completed = true;
            completedAt = LocalDateTime.now(ZoneId.systemDefault());
        }
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public Veterinarian getVeterinarian() {return veterinarian;}
    public void setVeterinarian(Veterinarian veterinarian) {this.veterinarian = veterinarian;}

    public Pet getPet() {return pet;}
    public void setPet(Pet pet) {this.pet = pet;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public boolean isCompleted() {return completed;}
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (!completed) {
            this.completedAt = null;
        }
    }

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getCompletedAt() {return completedAt;}
    public void setCompletedAt(LocalDateTime completedAt) {this.completedAt = completedAt;}
}
