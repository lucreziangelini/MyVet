package it.ispwproject.myvet.model;

import it.ispwproject.myvet.enumerator.Role;

public class Veterinarian extends User {

    private String bio;
    private String specialization;

    public Veterinarian() {
        super();
    }

    public Veterinarian(int id,
                        String name,
                        String surname,
                        String email,
                        String password,
                        String bio,
                        String specialization) {
        super(
                id,
                name,
                surname,
                email,
                password,
                Role.VETERINARIAN
        );

        this.bio = bio;
        this.specialization = specialization;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

}
