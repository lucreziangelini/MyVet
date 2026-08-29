package it.ispwproject.myvet.enumerator;

public enum Role {
    PET_OWNER,
    VETERINARIAN,
    ADMIN;

    public static Role fromString(String role) {
        return switch (role.toUpperCase()) {
            case "PET_OWNER" -> PET_OWNER;
            case "VETERINARIAN" -> VETERINARIAN;
            case "ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException(
                    "Ruolo non valido: " + role);
        };
    }
}
