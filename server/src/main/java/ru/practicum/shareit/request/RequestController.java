package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto saveRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody RequestDto requestDto) {
        return requestService.save(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping
    public List<RequestDto> getALLRequestsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllRequestsFromUser(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getALLRequests(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return requestService.getAllRequests(userId, from, size);
    }

}
