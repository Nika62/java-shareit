package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;
import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveRequest(long userId, RequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getRequestById(long userId, long requestId) {
        Map<String, Object> parameters = Map.of(
                "requestId", requestId
        );
        return get("/{requestId}", userId, parameters);
    }


    public ResponseEntity<Object> getALLRequestsFromUser(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getALLRequests(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("/all", userId, parameters);
    }

}
