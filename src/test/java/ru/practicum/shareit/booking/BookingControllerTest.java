package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.HelperCreationEntities.getItemDto;
import static ru.practicum.shareit.HelperCreationEntities.getUserDtoId3;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private BookingDto bookingDto = new BookingDto(1, LocalDateTime.of(2024, 4, 4, 12, 12, 1), LocalDateTime.of(2024, 4, 4, 13, 12, 1), getUserDtoId3(), getItemDto(), WAITING);
    private BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(bookingDto.getStart(), bookingDto.getEnd(), bookingDto.getItem().getId());

    @Test
    @SneakyThrows
    void createBooking() {
        when(bookingService.createBooking(3, bookingDtoCreate))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value("2024-04-04T12:12:01"))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.status").value(WAITING.name()));
    }

    @Test
    @SneakyThrows
    void shouldExceptionCreateBooking() {

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(new BookingDtoCreate(LocalDateTime.of(1999, 9, 9, 9, 12, 12), LocalDateTime.of(2000, 9, 9, 9, 12, 12), 1)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации данных. Проверьте правильность заполнения полей"));
    }


    @Test
    void updateBooking() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value("2024-04-04T12:12:01"))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.status").value(WAITING.name()));

    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value("2024-04-04T12:12:01"))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.status").value(WAITING.name()));
    }

    @Test
    void getAllBookingsByUserIdAndStatus() throws Exception {
        when(bookingService.getAllBookingsByUserIdAndStatus(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

    }

    @Test
    void getAllBookingsByOwner() throws Exception {
        when(bookingService.getAllBookingsByOwnerAndStatus(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void shouldReturnExceptionGetAllBookingsByOwnerWrongSize() throws Exception {
        when(bookingService.getAllBookingsByOwnerAndStatus(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "CURRENT")
                        .param("from", "-1")
                        .param("size", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Параметры запроса from = -1 или size = 3 введены некорректно"));
    }

    @Test
    void shouldReturnExceptionGetAllBookingsByUserWrongSize() throws Exception {
        when(bookingService.getAllBookingsByUserIdAndStatus(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "CURRENT")
                        .param("from", "-1")
                        .param("size", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Параметры запроса from = -1 или size = 3 введены некорректно"));
    }

}