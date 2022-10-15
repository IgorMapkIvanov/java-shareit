package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static ru.practicum.shareit.data.UserAndUserDtoData.*;

@Transactional
@SpringBootTest(
        properties = "spring.config.activate.on-profile=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final UserService userService;

    @Test
    public void addUserTest() {
        UserDto createdUser = userService.add(userDto1);
        TypedQuery<User> query = entityManager.createQuery(
                "select u from User u where u.id = : id", User.class);
        User user = query.setParameter("id", createdUser.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void getByIdUserTest() {
        UserDto createdUser = userService.add(userDto1);
        UserDto userFromGet = userService.getById(createdUser.getId());

        assertThat(userFromGet, notNullValue());
        assertThat(userFromGet.getName(), equalTo(userDto1.getName()));
        assertThat(userFromGet.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void getAllUsersTest() {
        userService.add(userDto3);
        userService.add(userDto4);
        List<UserDto> users = userService.getAll(PageRequest.of(0, 10));

        assertThat(users, hasSize(2));
        assertThat(users.get(0).getName(), equalTo(userDto3.getName()));
        assertThat(users.get(1).getName(), equalTo(userDto4.getName()));
    }

    @Test
    void updateUserNameTest() {
        UserDto oldUser = userService.add(userDto1);
        UserDto oldUserFromGet = userService.getById(oldUser.getId());

        assertThat(oldUserFromGet, notNullValue());

        UserDto updatedUser = userService.update(UserDto.builder()
                .id(oldUser.getId())
                .name("userNameUpdate").build());
        UserDto updateUserFromGet = userService.getById(updatedUser.getId());
        assertThat(updateUserFromGet, notNullValue());

        TypedQuery<User> query = entityManager.createQuery(
                "select u from User u where u.id = : id", User.class);
        User user = query.setParameter("id", updatedUser.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo("userNameUpdate"));
    }

    @Test
    public void deleteUserTest() {
        UserDto createdUser = userService.add(userDto1);
        userService.delete(createdUser.getId());
        User user = entityManager.find(User.class, createdUser.getId());
        assertThat(user, nullValue());
    }
}