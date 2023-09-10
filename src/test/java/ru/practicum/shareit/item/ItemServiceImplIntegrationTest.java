package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.HelperCreationEntities.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {
    @Autowired
    private final ItemService itemService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final RequestService requestService;

    private UserDto userDto;

    private UserDto userCreateRequest;

    private RequestDto requestDto;

    @BeforeEach
    public void before() {
        userDto = userService.createUser(getUserDtoWithoutId());
        userCreateRequest = userService.createUser(new UserDto("user has request", "userrequest@mail.com"));
        requestDto = requestService.save(2, getRequestForCreate());
    }

    @Test
    void shouldCreateItem() {
        ItemDto itemDto = itemService.createItem(1, getItemDtoForCreate());
        assertEquals(itemDto.getId(), 1);
        assertEquals(itemDto.getName(), "item name create");
        assertEquals(itemDto.getDescription(), "item description create");
        assertTrue(itemDto.getAvailable());
        assertEquals(itemDto.getUser(), userDto);
    }

    @Test
    void shouldReturnExceptionCreateItemWrongUser() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.createItem(99, getItemDtoForCreate());
                });

        assertEquals("Пользователя с id = 99 нет в базе", e.getMessage());
    }

    @Test
    void shouldCreateItemWhiteRequestId() {
        ItemDto itemDto = itemService.createItem(1, getItemDtoForCreateRequestId());
        assertEquals(itemDto.getId(), 1);
        assertEquals(itemDto.getName(), "item reply request");
        assertEquals(itemDto.getDescription(), "description item reply request");
        assertTrue(itemDto.getAvailable());
        assertEquals(itemDto.getRequestId(), 1);
        assertEquals(itemDto.getUser(), userDto);
    }

    @Test
    void shouldReturnExceptionCreateItemWrongRequestId() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.createItem(1, new ItemDto("name", "description", true, 99));
                });

        assertEquals("Запрос с id = 99 не найден", e.getMessage());
    }

    @Test
    void shouldUpdateItem() {
        ItemDto item = itemService.createItem(1, getItemDtoForCreate());
        item.setName("update");
        item.setAvailable(false);
        ItemDto updateItemDto = itemService.updateItem(1, 1, item);
        assertEquals(updateItemDto.getName(), "update");
        assertEquals(updateItemDto.getDescription(), item.getDescription());
        assertEquals(updateItemDto.getAvailable(), false);

    }

    @Test
    void shouldReturnExceptionUpdateItemNotBase() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.updateItem(1, 99, new ItemDto("name", "description", true));
                });

        assertEquals("Вещи с id = 99 нет в базе", e.getMessage());

    }

    @Test
    void shouldReturnExceptionUpdateItemWrongUserId() {
        ItemDto item = itemService.createItem(1, getItemDtoForCreate());
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.updateItem(99, 1, item);
                });
        assertEquals("У пользователя с id 99 нет вещи с id 1", e.getMessage());

    }

    @Test
    void shouldGetItemById() {
        ItemDto createdItem = itemService.createItem(1, getItemDtoForCreate());
        ItemDto itemDto = itemService.getItemById(1, 1);
        assertEquals(itemDto.getId(), 1);
        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    void shouldReturnExceptionGetItemByWrongId() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.getItemById(99, 1);
                });
        assertEquals("Вещи с id = 99 нет в базе", e.getMessage());
    }

    @Test
    void shouldGetSearchItem() {
        itemService.createItem(1, getItemDtoForCreate());
        ItemDto itemDto = new ItemDto("ring", "description", true);
        itemService.createItem(1, itemDto);
        List<ItemDto> ring = itemService.getSearchItem("Ring");
        assertEquals(ring.size(), 1);
        assertEquals(ring.get(0).getName(), "ring");
        List<ItemDto> items = itemService.getSearchItem("Descr");
        assertEquals(items.size(), 2);

    }

    @Test
    void shouldGetAllItemsUser() {
        itemService.createItem(1, getItemDtoForCreate());
        ItemDto itemDto = new ItemDto("ring", "description", true);
        itemService.createItem(2, itemDto);
        List<ItemDto> items = itemService.getAllItemsUser(1);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getUser().getId(), 1);
    }

    @Test
    void shouldDeleteItemById() {
        itemService.createItem(1, getItemDtoForCreate());
        itemService.deleteItemById(1);
        assertEquals(itemService.getAllItemsUser(1).size(), 0);
    }
}