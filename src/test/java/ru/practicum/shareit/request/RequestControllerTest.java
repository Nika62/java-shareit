package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private RequestService requestService;
    @Autowired
    private MockMvc mvc;

    private List<ItemRequestDto> items = List.of(new ItemRequestDto(1, "item name", "item description", true, 1));
    private RequestDto requestDto = new RequestDto(1, "description", LocalDateTime.of(2023, 9, 12, 12, 12, 12), items);

    @Test
    void saveRequest() throws Exception {
        when(requestService.save(anyLong(), any(RequestDto.class)))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.created").value("2023-09-12T12:12:12"));
    }

    @Test
    void getRequestById() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.created").value("2023-09-12T12:12:12"));
    }

    @Test
    void getALLRequestsUser() throws Exception {
        when(requestService.getAllRequestsUser(anyLong()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto))));
    }

    @Test
    void getALLRequests() throws Exception {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto))));
    }

    @Test
    void shouldReturnExceptionGetALLRequests() throws Exception {

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", "0")
                        .param("size", "-4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Параметры запроса from = 0 или size = -4 введены некорректно"));
    }
}