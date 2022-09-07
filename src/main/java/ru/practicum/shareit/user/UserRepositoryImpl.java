package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository<User> {
    private Long id = 0L;
    private final Map<Long, User> userMap = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        if (userMap.containsKey(id)) {
            return Optional.of(userMap.get(id));
        } else {
            throw new NotFoundException("Пользователь с ID =" + id + " не найден.");
        }
    }

    @Override
    public User add(User user) {
        user.setId(++id);
        userMap.put(user.getId(), user);
        uniqueEmails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        User userUpdate = userMap.get(user.getId());
        if ((user.getName() != null) && (!user.getName().equals(userUpdate.getName()))) {
            userUpdate.setName(user.getName());
        }
        if ((user.getEmail() != null) && (!user.getEmail().equals(userUpdate.getEmail()))) {
            uniqueEmails.remove(userUpdate.getEmail());
            uniqueEmails.add(user.getEmail());
            userUpdate.setEmail(user.getEmail());
        }
        userMap.put(userUpdate.getId(), userUpdate);
        return userUpdate;
    }

    @Override
    public void delete(Long id) {
        uniqueEmails.remove(userMap.get(id).getEmail());
        userMap.remove(id);
    }
}