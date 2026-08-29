package it.ispwproject.myvet.service;

import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.VeterinarianBean;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VeterinarianSearchServiceTest {

    private final VeterinarianSearchService service =
            new VeterinarianSearchService();

    @Test
    void placesFavoriteVeterinariansFirst() {
        PetBean pet = pet("Cat");
        VeterinarianBean other = veterinarian(1, "Anna", false);
        VeterinarianBean favorite = veterinarian(2, "Marco", true);

        List<VeterinarianBean> results = service.search(
                pet,
                List.of(other, favorite)
        );

        assertEquals(2, results.size());
        assertEquals(2, results.get(0).getId());
        assertTrue(results.get(0).isFavorite());
    }

    @Test
    void rejectsPetWithoutSpecies() {
        PetBean pet = pet(" ");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.search(pet, List.of())
        );
    }

    private PetBean pet(String species) {
        return new PetBean(
                1,
                "Luna",
                species,
                "European Shorthair",
                LocalDate.of(2021, 9, 3)
        );
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
