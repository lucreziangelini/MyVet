package it.ispwproject.myvet.model;

import it.ispwproject.myvet.enumerator.Role;

import java.util.ArrayList;
import java.util.List;

public class PetOwner extends User {

    private List<Pet> pets;
    private List<Veterinarian> favoriteVeterinarians;

    public PetOwner() {
        super();
        this.pets = new ArrayList<>();
        this.favoriteVeterinarians = new ArrayList<>();
    }

    public PetOwner(int id,
                    String name,
                    String surname,
                    String email,
                    String password) {
        super(
                id,
                name,
                surname,
                email,
                password,
                Role.PET_OWNER
        );
        this.pets = new ArrayList<>();
        this.favoriteVeterinarians = new ArrayList<>();
    }

    public boolean hasFavorite(int veterinarianId) {
        return favoriteVeterinarians.stream()
                .anyMatch(veterinarian ->
                        veterinarian.getId() == veterinarianId
                );
    }

    public void addFavorite(Veterinarian veterinarian) {
        if (veterinarian != null
                && !hasFavorite(veterinarian.getId())) {
            favoriteVeterinarians.add(veterinarian);
        }
    }

    public void removeFavorite(int veterinarianId) {
        favoriteVeterinarians.removeIf(
                veterinarian ->
                        veterinarian.getId() == veterinarianId
        );
    }

    public void addPet(Pet pet) {
        if (pet != null) {
            pets.add(pet);
        }
    }

    public boolean hasRegisteredPets() {return !pets.isEmpty();}

    public List<Pet> getPets() {return pets;}

    public void setPets(List<Pet> pets) {
        this.pets = pets == null
                ? new ArrayList<>()
                : new ArrayList<>(pets);
    }

    public List<Veterinarian> getFavoriteVeterinarians() {return favoriteVeterinarians;}

    public void setFavoriteVeterinarians(
            List<Veterinarian> veterinarians) {

        this.favoriteVeterinarians =
                veterinarians == null
                        ? new ArrayList<>()
                        : new ArrayList<>(veterinarians);
    }
}
