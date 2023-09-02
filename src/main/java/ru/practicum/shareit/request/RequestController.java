package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto saveItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody RequestDto requestDto) {
        return requestService.save(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping
    public List<RequestDto> getALLRequestsUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllRequestsUser(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getALLRequests(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {

        if (from < 0 || size <= 0) {
            throw new ValidationException("Параметры запроса from = " + from + " или size = " + size + " введены некорректно");
        }
        return requestService.getAllRequests(userId, from, size);
    }

}
