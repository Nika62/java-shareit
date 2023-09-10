package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;


import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.HelperCreationEntities.getCommentDto;
import static ru.practicum.shareit.HelperCreationEntities.getItemDto;
import static ru.practicum.shareit.HelperCreationEntities.getItemDtoWithoutComments;
import static ru.practicum.shareit.HelperCreationEntities.getLastBookingDto;
import static ru.practicum.shareit.HelperCreationEntities.getListCommentsDto;
import static ru.practicum.shareit.HelperCreationEntities.getListItemDto;
import static ru.practicum.shareit.HelperCreationEntities.getNextBookingDto;
import static ru.practicum.shareit.HelperCreationEntities.getUserDto;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;


    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = getItemDto();

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("item name"))
                .andExpect(jsonPath("$.description").value("item description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.user", is(getUserDto()), UserDto.class))
                .andExpect(jsonPath("$.requestId").value(2));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("item name"))
                .andExpect(jsonPath("$.description").value("item description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.user", is(getUserDto()), UserDto.class))
                .andExpect(jsonPath("$.requestId").value(2));
    }

    @Test
    void getItemById() throws Exception {
        ItemDto itemDto = getItemDtoWithoutComments();

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        when(commentService.getAllCommentsByItemId(anyLong()))
                .thenReturn(getListCommentsDto());

        mvc.perform(get("/items/{itemId}", 2)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("item name"))
                .andExpect(jsonPath("$.description").value("item description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.user", is(getUserDto()), UserDto.class))
                .andExpect(jsonPath("$.requestId").value(2))
                .andExpect(jsonPath("$.comments[0].text").value("text comment"))
                .andExpect(jsonPath("$.lastBooking").value(getLastBookingDto()))
                .andExpect(jsonPath("$.nextBooking").value(getNextBookingDto()));
    }

    @Test
    void getSearchItem() throws Exception {
        when(itemService.getSearchItem(anyString()))
                .thenReturn(getListItemDto());

        mvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(getListItemDto())));
    }

    @Test
    void getAllItemsUser() throws Exception {
        when(itemService.getAllItemsUser(anyLong()))
                .thenReturn(getListItemDto());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(getListItemDto())));
    }

    @Test
    void deleteItemBy() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItemById(1);
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = getCommentDto();
        when(commentService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));

    }
}