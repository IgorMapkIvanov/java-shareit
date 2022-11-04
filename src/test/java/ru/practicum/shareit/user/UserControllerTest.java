package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.data.UserAndUserDtoData.user1;
import static ru.practicum.shareit.data.UserAndUserDtoData.userDto1;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    UserService userService;
    @Autowired
    MockMvc mvc;

    @Test
    void shouldGetAllIsOkTest() throws Exception {
        when(userService.getAll(PageRequest.ofSize(10)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/users"))
                .andExpectAll(status().isOk(),
                        content().json("[]"));

        verify(userService, times(1))
                .getAll(PageRequest.ofSize(10));
    }

    @Test
    void shouldGetAllWhenRequestParamFromIsWrongTest() throws Exception {
        mvc.perform(get("/users&from=-1"))
                .andExpect(status().is4xxClientError());

        verify(userService, times(0)).getAll(any());
    }

    @Test
    void shouldGetAllWhenRequestParamSizeIsWrongTest() throws Exception {
        mvc.perform(get("/users&size=0"))
                .andExpectAll(status().is4xxClientError());
        mvc.perform(get("/users&size=-1"))
                .andExpectAll(status().is4xxClientError());

        verify(userService, times(0)).getAll(any());

    }

    @Test
    void getByIdTest() throws Exception {
        when(userService.getById(anyLong()))
                .thenReturn(userDto1);

        mvc.perform(get("/users/1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(userDto1.getId()),
                        jsonPath("$.name").value(userDto1.getName()),
                        jsonPath("$.email").value(userDto1.getEmail()));

        verify(userService, times(1))
                .getById(anyLong());
    }

    @Test
    void addUserTest() throws Exception {
        when(userService.add(any())).thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));
    }

    @Test
    void shouldUpdateIsOkTest() throws Exception {
        when(userService.update(any(UserDto.class))).thenReturn(userDto1);

        mvc.perform(mockAction(patch("/users/1"), user1.getId(), user1))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(userDto1.getId()),
                        jsonPath("$.name").value(userDto1.getName()),
                        jsonPath("$.email").value(userDto1.getEmail()));
    }

    @Test
    void shouldUpdateIsNotOkTest() throws Exception {
        when(userService.update(any(UserDto.class)))
                .thenThrow(new NoSuchElementException("Пользователь с таким ID не найден."));

        mvc.perform(mockAction(patch("/users/1"), user1.getId(), user1))
                .andExpect(status().is4xxClientError());

        verify(userService, times(1))
                .update(any(UserDto.class));
    }

    @Test
    void shouldDeleteIsOKTest() throws Exception {
        mvc.perform(mockAction(delete("/users/1"), user1.getId(), user1))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .delete(anyLong());
    }

    @Test
    void shouldDeleteIsNotOkTest() throws Exception {
        when(userService.delete(anyLong())).thenThrow(new NoSuchElementException("Пользователь с таким ID не найден."));

        mvc.perform(mockAction(delete("/users/1"), user1.getId(), user1))
                .andExpect(status().is4xxClientError());

        verify(userService, times(1))
                .delete(anyLong());
    }

    private MockHttpServletRequestBuilder mockAction(MockHttpServletRequestBuilder mockMvc, Long userId, User user)
            throws JsonProcessingException {
        return mockMvc
                .content(mapper.writeValueAsString(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId);
    }
}