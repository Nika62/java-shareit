package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto save(long userId, RequestDto requestDto);

    RequestDto getRequestById(long userId, long requestId);

    List<RequestDto> getAllRequests(long userId, int from, int size);

    List<RequestDto> getAllRequestsUser(long userId);
}
