package ru.practicum.shareit.user;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll(PageRequest pageRequest);

    UserDto getById(Long id);

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto);

    UserDto delete(Long id);
}