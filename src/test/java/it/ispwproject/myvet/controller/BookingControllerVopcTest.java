package it.ispwproject.myvet.controller;

import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.VeterinarianBean;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingControllerVopcTest {

    private final VeterinarianSearchController controller =
            new VeterinarianSearchController();

    @Test
    void returnsVeterinariansAssociatedWithSelectedPetSpecies() {
        PetBean pet = new PetBean(
                1,
                "Luna",
                "Cat",
                "European Shorthair",
                LocalDate.of(2021, 9, 3)
        );
        VeterinarianBean favorite = veterinarian(1, "Anna", true);
        VeterinarianBean other = veterinarian(2, "Marco", false);

        List<VeterinarianBean> results =
                controller.getVeterinariansForPet(
                        pet,
                        List.of(other, favorite)
                );

        assertEquals(2, results.size());
        assertEquals(1, results.get(0).getId());
    }

    @Test
    void returnsEmptyListWhenPersistenceFindsNoVeterinarianForSpecies() {
        PetBean pet = new PetBean();
        pet.setSpecies("Exotic species");

        assertTrue(controller.getVeterinariansForPet(
                pet,
                List.of()
        ).isEmpty());
    }

    private VeterinarianBean veterinarian(
            int id,
            String name,
            boolean favorite) {
        return new VeterinarianBean(
                id,
                name,
                "Vet",
                name.toLowerCase() + "@myvet.it",
                "Clinic address",
                favorite
        );
    }
}
