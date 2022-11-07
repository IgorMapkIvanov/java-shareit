package ru.practicum.shareit.data;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserAndUserDtoData {
    /**
     * {@link User User 1}
     * <p>id = 1
     * <p>name = user1Name
     * <p>email = user1Name@email.ru
     */
    public static User user1 = User
            .builder()
            .id(1L)
            .name("user1Name")
            .email("user1Name@email.ru")
            .build();
    /**
     * {@link User User 2}
     * <p>id = 2
     * <p>name = user2Name
     * <p>email = user2Name@email.ru
     */
    public static User user2 = User
            .builder()
            .id(2L)
            .name("user2Name")
            .email("user2Name@email.ru")
            .build();
    /**
     * {@link UserDto UserDto 1}
     * <p>id = 1
     * <p>name = name
     * <p>email = name@email.ru
     */
    public static UserDto userDto1 = UserDto
            .builder()
            .id(1L)
            .name("name")
            .email("name@email.ru")
            .build();
    /**
     * {@link UserDto UserDto 2}
     * <p>id = 2
     * <p>name = user2Name
     * <p>email = user2Name@email.ru
     */
    public static UserDto userDto2 = UserDto
            .builder()
            .id(2L)
            .name("user2Name")
            .email("user2Name@email.ru")
            .build();
    /**
     * {@link User User 3}
     * <p>id = 3
     * <p>name = name3
     * <p>email = name3@email.ru
     */
    public static User user3 = User
            .builder()
            .id(3L)
            .name("name3")
            .email("name3@email.ru")
            .build();
    /**
     * {@link UserDto UserDto 3}
     * <p>name = name3
     * <p>email = name3@email.ru
     */
    public static UserDto userDto3 = UserDto
            .builder()
            .name("name3")
            .email("name3@email.ru")
            .build();
    /**
     * {@link UserDto UserDto 4}
     * <p>name = name4
     * <p>email = name4@email.ru
     */
    public static UserDto userDto4 = UserDto
            .builder()
            .name("name4")
            .email("name4@email.ru")
            .build();
    /**
     * {@link User userNameUpdate}
     * <p>id = 1
     * <p>name = userNameUpdate
     * <p>email = name@email.ru
     */
    public static User userNameUpdate = User
            .builder()
            .id(1L)
            .name("userNameUpdate")
            .email("name@email.ru")
            .build();
    /**
     * {@link UserDto userNameUpdateDto}
     * <p>id = 1
     * <p>name = userNameUpdate
     * <p>email = name@email.ru
     */
    public static UserDto userNameUpdateDto = UserDto
            .builder()
            .id(1L)
            .name("userNameUpdate")
            .email("name@email.ru")
            .build();
    /**
     * {@link User userEmailUpdate}
     * <p>id = 1
     * <p>name = name
     * <p>email = nameUpdate@email.ru
     */
    public static User userEmailUpdate = User
            .builder()
            .id(1L)
            .name("name")
            .email("nameUpdate@email.ru")
            .build();
    /**
     * {@link UserDto userEmailUpdateDto}
     * <p>id = 1
     * <p>name = name
     * <p>email = nameUpdate@email.ru
     */
    public static UserDto userEmailUpdateDto = UserDto
            .builder()
            .id(1L)
            .name("name")
            .email("nameUpdate@email.ru")
            .build();
}