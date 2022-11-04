package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.data.UserAndUserDtoData.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAll(PageRequest.of(0, 10));

        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());
        assertEquals(result.size(), 2);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldGetUserByIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        UserDto result = userService.getById(1L);
        assertEquals(result.getName(), user1.getName());
        assertEquals(result.getEmail(), user1.getEmail());

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldNewUserCreateTest() {
        when(userRepository.save(any())).thenReturn(user2);

        UserDto result = userService.add(userDto2);

        assertEquals(result, userDto2);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateUserNameTest() {
        when(userRepository.save(any())).thenReturn(userNameUpdate);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userNameUpdate));

        UserDto result = userService.update(userNameUpdateDto);

        assertEquals(result.getName(), "userNameUpdate");
        assertEquals(result.getEmail(), "name@email.ru");

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateUserEmailTest() {
        when(userRepository.save(any())).thenReturn(userEmailUpdate);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEmailUpdate));

        UserDto result = userService.update(userEmailUpdateDto);
        assertEquals(result.getName(), "name");
        assertEquals(result.getEmail(), "nameUpdate@email.ru");

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldDeleteUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(anyLong());
    }
}