package ru.practicum.shareit.user.interfaces;

import java.util.HashSet;
import java.util.Set;

public interface UniqueEmails {
    Set<String> uniqueEmails = new HashSet<>();

    default Set<String> getUniqueEmails() {
        return uniqueEmails;
    }
}
