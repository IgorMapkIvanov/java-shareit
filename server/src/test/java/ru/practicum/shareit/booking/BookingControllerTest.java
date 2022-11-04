package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingService service;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Test
    void getUserBookingTest() throws Exception {
        ResponseBookingDto bookingDto = ResponseBookingDto
                .builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1).withNano(0))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1).withNano(0))
                .build();
        when(service.getUserBookings(1L, "ALL", PageRequest.of(0, 10)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .param("state", "ALL")
                )
                .andExpect(status().isOk());
    }

    @Test
    void getBookingTest() throws Exception {
        ResponseBookingDto bookingDto = ResponseBookingDto
                .builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1).withNano(0))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1).withNano(0))
                .build();
        when(service.getBooking(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                )
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(bookingDto.getId()),
                        jsonPath("$.status").value(bookingDto.getStatus().name()),
                        jsonPath("$.start").value(bookingDto.getStart().toString() + ":00"),
                        jsonPath("$.end").value(bookingDto.getEnd().toString() + ":00"));
    }

    @Test
    void getOwnerBookingTest() throws Exception {
        ResponseBookingDto bookingDto = ResponseBookingDto
                .builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1).withNano(0))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1).withNano(0))
                .build();
        when(service.getOwnerBookings(1L, "ALL", PageRequest.of(0, 10)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .param("state", "ALL")
                )
                .andExpect(status().isOk());
    }

    @Test
    void addBookingTest() throws Exception {
        BookingDto bookingDto = BookingDto
                .builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .itemId(1L)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1).withNano(0))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1).withNano(0))
                .build();
        when(service.addBooking(1L, bookingDto)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(bookingDto.getId()),
                        jsonPath("$.status").value(bookingDto.getStatus().name()),
                        jsonPath("$.itemId").value(bookingDto.getItemId()),
                        jsonPath("$.start").value(bookingDto.getStart().toString() + ":00"),
                        jsonPath("$.end").value(bookingDto.getEnd().toString() + ":00"));
    }

    @Test
    void approveBookingTest() throws Exception {
        ResponseBookingDto bookingDto = ResponseBookingDto
                .builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2023, 1, 1, 1, 1).withNano(0))
                .end(LocalDateTime.of(2023, 1, 2, 1, 1).withNano(0))
                .build();
        when(service.approveBooking(1L, 1L, true)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(bookingDto.getId()),
                        jsonPath("$.status").value(bookingDto.getStatus().name()),
                        jsonPath("$.start").value(bookingDto.getStart().toString() + ":00"),
                        jsonPath("$.end").value(bookingDto.getEnd().toString() + ":00"));
    }
}