package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId, @RequestBody ItemDto item) {
        log.info("Update item {}, userId={}, itemId={}", item, userId, itemId);
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getIemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item by itemId={}, userId={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        log.info("Get all items by key={}", text);
        return itemClient.getSearchItem(userId, text);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get all items by user userId={}", userId);
        return itemClient.getAllItemsUser(userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemBy(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Delete itemId={}, userId={}", itemId, userId);
        return itemClient.deleteItemById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @Valid @RequestBody CommentDto commentDto) {
        log.info("Creating comment {}, userId, itemId", commentDto, userId, itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }

}
