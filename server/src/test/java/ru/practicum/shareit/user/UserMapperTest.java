package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.data.UserAndUserDtoData.user1;
import static ru.practicum.shareit.data.UserAndUserDtoData.userDto1;

class UserMapperTest {

    @Test
    void fromUserToUserDtoTest() {
        UserDto userDto = UserMapper.toDto(user1);
        assertNotNull(userDto);
        assertEquals(1, userDto.getId());
        assertEquals("user1Name", userDto.getName());
        assertEquals("user1Name@email.ru", userDto.getEmail());
    }

    @Test
    void fromUserDtoToUserTest() {
        User user = UserMapper.fromDto(userDto1);
        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("name", user.getName());
        assertEquals("name@email.ru", user.getEmail());
    }
}