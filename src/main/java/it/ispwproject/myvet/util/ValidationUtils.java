package it.ispwproject.myvet.util;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            );

    private ValidationUtils() {
        // Prevents instantiation
    }

    public static boolean isValidEmail(String email) {
        return email != null
                && !email.isBlank()
                && EMAIL_PATTERN.matcher(email).matches();
    }
}