package ru.practicum.shareit.Item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@Getter
@Setter
class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    private User user;
    private User user2;
    private Item item1;
    private Comment comment1;
    private int from = 0;
    private int size = 20;
    private PageRequest pageRequest;
    private ItemDto itemDto;
    private ItemDtoWithBooking itemDtoWithBooking;
    private Collection<CommentDto> comments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user = new User(1L, "user", "user@user.com");
        user2 = new User(2L, "user2", "user2@user.com");
        itemDto = new ItemDto(1L, "item", "description", false, null, null);
        itemDtoWithBooking = new ItemDtoWithBooking(1L, "item", "description", false,
                null, null, null);
        item1 = new Item(1L, "item 1", "item 1 description", true, user, null);
        comment1 = new Comment(1L, "Comment", user2, item1,
                LocalDateTime.of(2023, 10, 20, 12, 0));
        int page = from / size;
        pageRequest = PageRequest.of(page, size);
    }

    @Test
    void shouldGetAllItemsForOwnerWithIdTest() throws Exception {
        when(itemService.getAllItemsForOwnerWithId(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(itemDtoWithBooking));

        mockMvc.perform(mockAction(get("/items"), user.getId(), itemDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpectAll(jsonPath("$[0].id").value(itemDtoWithBooking.getId()),
                        jsonPath("$[0].name").value(itemDtoWithBooking.getName()),
                        jsonPath("$[0].description").value(itemDtoWithBooking.getDescription()));

        verify(itemService, times(1))
                .getAllItemsForOwnerWithId(anyLong(), any(PageRequest.class));
    }

    @Test
    void shouldGetItemByIdTest() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(ItemDtoWithBooking.builder()
                        .id(1L)
                        .name("item")
                        .description("description")
                        .available(true)
                        .nextBooking(new ItemDtoWithBooking.ItemBookingDto())
                        .lastBooking(new ItemDtoWithBooking.ItemBookingDto())
                        .comments(Collections.emptyList())
                        .build());

        mockMvc.perform(mockAction(get("/items/1"), user.getId(), itemDto))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(itemDto.getId()),
                        jsonPath("$.name").value(itemDto.getName()),
                        jsonPath("$.description").value(itemDto.getDescription()));

        verify(itemService, times(1))
                .getItemById(anyLong(), anyLong());
    }

    @Test
    void shouldSearchItemsByNameOrDescriptionContainingTextIgnoreCaseAndAvailableTest() throws Exception {
        when(itemService.searchItemByText(anyString(), any(PageRequest.class)))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(mockAction(get("/items/search?text=item 1"), user.getId(), itemDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpectAll(jsonPath("$[0].id").value(itemDto.getId()),
                        jsonPath("$[0].name").value(itemDto.getName()),
                        jsonPath("$[0].description").value(itemDto.getDescription()));

        verify(itemService, times(1))
                .searchItemByText(anyString(), any(PageRequest.class));
    }

    @Test
    void shouldAddItemForUserWithIdTest() throws Exception {
        when(itemService.addItemForUserWithId(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(mockAction(post("/items"), user.getId(), itemDto))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(itemDto.getId()),
                        jsonPath("$.name").value(itemDto.getName()),
                        jsonPath("$.description").value(itemDto.getDescription()));

        verify(itemService, times(1))
                .addItemForUserWithId(any(), anyLong());
    }

    @Test
    void shouldAddCommentTest() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Comment")
                .authorName(user2.getName())
                .created(LocalDateTime.now())
                .build();
        when(itemService.addComment(anyLong(), any(CommentDto.class), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(comment1.getId()),
                        jsonPath("$.text").value(comment1.getText()));

        verify(itemService, times(1))
                .addComment(anyLong(), any(CommentDto.class), anyLong());

    }

    @Test
    void shouldUpdateItemTest() throws Exception {
        when(itemService.updateItemForUserWithId(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(mockAction(patch("/items/1"), user.getId(), itemDto))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(itemDto.getId()),
                        jsonPath("$.name").value(itemDto.getName()),
                        jsonPath("$.description").value(itemDto.getDescription()));

        verify(itemService, times(1))
                .updateItemForUserWithId(any(ItemDto.class), anyLong());
    }

    @Test
    void shouldDeleteItemTest() throws Exception {
        mockMvc.perform(mockAction(delete("/items/1"), user.getId(), itemDto))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .deleteItemForUserWithId(anyLong(), anyLong());
    }

    private MockHttpServletRequestBuilder mockAction(MockHttpServletRequestBuilder mockMvc, Long userId, ItemDto itemDto)
            throws JsonProcessingException {
        return mockMvc
                .content(mapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId);
    }
}