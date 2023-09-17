package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.HelperCreationEntities.getUser;
import static ru.practicum.shareit.HelperCreationEntities.getUser2;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RequestServiceImplIntegrationTest {
    @Autowired
    private final RequestMapper requestMapper;

    @Autowired
    private final ItemMapper itemMapper;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final RequestRepository requestRepository;
    @Autowired
    private final RequestService requestService;

    private User owner;

    @BeforeEach
    public void before() {
        owner = userRepository.save(getUser());
        User user = userRepository.save(getUser2());
    }

    @Test
    void save() {
        RequestDto requestDto = requestService.save(2, new RequestDto("description"));
        assertEquals(requestDto.getId(), 1);
        assertEquals(requestDto.getDescription(), "description");
        assertNotNull(requestDto.getCreated());
    }

    @Test
    void shouldReturnNotFondExceptionCreate() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.save(99, new RequestDto("description"));
                });

        assertEquals("Пользователь с id 99 не найден в базе", e.getMessage());
    }

    @Test
    void getRequestById() {
        RequestDto requestDto = requestService.save(2, new RequestDto("description"));
        Request request = requestMapper.convertRequestDtoToRequest(requestDto);
        Item item = itemRepository.save(itemRepository.save(new Item("item name", "item description", true, owner, request)));
        RequestDto returnedRequestDto = requestService.getRequestById(2, 1);
        assertEquals(returnedRequestDto.getId(), 1);
        assertEquals(returnedRequestDto.getItems().size(), 1);
        assertEquals(returnedRequestDto.getItems().get(0).getName(), "item name");
    }

    @Test
    void shouldReturnNotFoundExceptionGetRequestById() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.getRequestById(2, 99);
                });

        assertEquals("Запрос с id = 99 не найден", e.getMessage());
    }

    @Test
    void shouldReturnNotFoundExceptionUserGetRequestById() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.getRequestById(99, 1);
                });

        assertEquals("Пользователь с id 99 не найден в базе", e.getMessage());
    }

    @Test
    void getAllRequestsUser() {
        RequestDto requestDto = requestService.save(2, new RequestDto("description"));
        Request request = requestMapper.convertRequestDtoToRequest(requestDto);
        Item item = itemRepository.save(itemRepository.save(new Item("item name", "item description", true, owner, request)));
        List<RequestDto> requests = requestService.getAllRequestsFromUser(2);
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getId(), requestDto.getId());
        assertEquals(requests.get(0).getItems().size(), 1);
        assertEquals(requests.get(0).getItems().get(0).getName(), "item name");

    }

    @Test
    void shouldReturnNotFoundExceptionGetAllRequestsUser() {
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.getAllRequestsFromUser(99);
                });

        assertEquals("Пользователь с id 99 не найден в базе", e.getMessage());
    }

    @Test
    void getAllRequests() {
        RequestDto requestDto = requestService.save(2, new RequestDto("description"));
        Request request = requestMapper.convertRequestDtoToRequest(requestDto);
        Item item = itemRepository.save(itemRepository.save(new Item("item name", "item description", true, owner, request)));
        List<RequestDto> requests = requestService.getAllRequests(1, 0, 3);
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getId(), requestDto.getId());
        assertEquals(requests.get(0).getItems().size(), 1);
        assertEquals(requests.get(0).getItems().get(0).getName(), "item name");
    }
}