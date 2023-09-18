package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.ValidationException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> saveRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @Valid @RequestBody RequestDto requestDto) {
        log.info("Creating request {} userId={}", requestDto, userId);
        return requestClient.saveRequest(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Get request by userId={}, requestId={requestId}", userId, requestId);
        return requestClient.getRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getALLRequestsFromUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requests by userId={}", userId);
        return requestClient.getALLRequestsFromUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getALLRequests(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {

        if (from < 0 || size <= 0) {
            throw new ValidationException("Параметры запроса from = " + from + " или size = " + size + " введены некорректно");
        }
        log.info("Get request with userId={}, from={}, size={}", userId, from, size);
        return requestClient.getALLRequests(userId, from, size);
    }

}
