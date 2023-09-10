package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

@Component
public class RequestMapper {

    public Request convertRequestDtoToRequest(RequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setCreated(requestDto.getCreated());
        return request;
    }

    public RequestDto convertRequestToRequestDto(Request request) {
        if (request == null) {
            return null;
        }
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());

        return requestDto;
    }
}
