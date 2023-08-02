package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserEmailValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public UserEmailValidator() {
       pattern = Pattern.compile(EMAIL_PATTERN);
    }

    public boolean validateUserEmail(final String userEmail) {
        matcher = pattern.matcher(userEmail);
        return matcher.matches();
    }
}
