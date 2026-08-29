package it.ispwproject.myvet.model;

import java.time.LocalDate;

public class Pet {

    private int id;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;

    public Pet() {
        // Used when reconstructing a Pet from persistence
    }

    public Pet(int id,
               String name,
               String species,
               String breed,
               LocalDate birthDate) {
        this.id        = id;
        this.name      = name;
        this.species   = species;
        this.breed     = breed;
        this.birthDate = birthDate;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSpecies() {return species;}
    public void setSpecies(String species) {this.species = species;}

    public String getBreed() {return breed;}
    public void setBreed(String breed) {this.breed = breed;}

    public LocalDate getBirthDate() {return birthDate;}
    public void setBirthDate(LocalDate birthDate) {this.birthDate = birthDate;}
}