package request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestMapper requestMapper;

    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Override
    public RequestDto save(long userId, RequestDto requestDto) {
        LocalDateTime created = LocalDateTime.now();
        User user = getUserOrTrow(userId);
        Request request = requestMapper.convertRequestDtoToRequest(requestDto);
        request.setUser(user);
        request.setCreated(created);

        return requestMapper.convertRequestToRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto getRequestById(long userId, long requestId) {
        User user = getUserOrTrow(userId);
        RequestDto requestDto = requestMapper.convertRequestToRequestDto(requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос с id = " + requestId + " не найден")
        ));
        requestDto.setItems(getReplyItemsOnResponse(requestId));
        return requestDto;
    }

    @Override
    public List<RequestDto> getAllRequestsUser(long userId) {
        User user = getUserOrTrow(userId);
        List<RequestDto> requests = requestRepository.findAllRequestsByUserId(userId).stream()
                .map(requestMapper::convertRequestToRequestDto)
                .collect(Collectors.toList());
        setItemsInRequestsDto(requests);
        return requests;
    }

    @Override
    public List<RequestDto> getAllRequests(long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<Request> page = requestRepository.findAllRequestsByUserIdNot(userId, pageRequest);
        List<RequestDto> requests = page.get().map(requestMapper::convertRequestToRequestDto).collect(Collectors.toList());
        setItemsInRequestsDto(requests);
        return requests;
    }

    private User getUserOrTrow(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + userId + " не найден в базе"));
    }

    private List<ItemRequestDto> getReplyItemsOnResponse(long requestId) {
        return itemRepository.findAllItemsByRequestId(requestId).stream()
                .map(itemMapper::convertItemToItemRequestDto).collect(Collectors.toList());
    }

    private void setItemsInRequestsDto(List<RequestDto> requests) {
        if (requests.size() > 0) {
            for (int i = 0; i < requests.size(); i++) {
                RequestDto requestDto = requests.get(i);
                requestDto.setItems(getReplyItemsOnResponse(requestDto.getId()));
            }
        }
    }

}
