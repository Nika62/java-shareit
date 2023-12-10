package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllItemsUser(long userId) {

        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(long itemId, long userId) {
        String path = UriComponentsBuilder.newInstance().path("/{itemId}").buildAndExpand(itemId).toString();
        return get(path, userId);
    }

    public ResponseEntity<Object> getSearchItem(long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }


    public ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }


    public ResponseEntity<Object> updateItem(long userId, long itemId, @RequestBody ItemDto item) {
        String path = UriComponentsBuilder.newInstance().path("/{itemId}").buildAndExpand(itemId).toString();
        return patch(path, userId, item);
    }

    public ResponseEntity<Object> deleteItemById(long userId, long itemId) {
        String path = UriComponentsBuilder.newInstance().path("/{itemId}").buildAndExpand(itemId).toString();
        return delete(path, userId);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentDto commentDto) {
        String path = UriComponentsBuilder.newInstance().path("/{itemId}/comment").buildAndExpand(itemId).toString();

        return post(path, userId, commentDto);
    }

}
