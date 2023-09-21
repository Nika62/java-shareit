package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    private final CommentService commentService;


    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto item) {
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);

    }

    @GetMapping("/{itemId}")
    public ItemDto getIemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemDto itemDto = itemService.getItemById(itemId, userId);
        itemDto.setComments(commentService.getAllCommentsByItemId(itemId));
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItem(@RequestParam String text) {
        return itemService.getSearchItem(text);
    }

    @GetMapping
    public List<ItemDto> getAllItemsUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItemsUser(userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemBy(@PathVariable long itemId) {
        itemService.deleteItemById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        return commentService.createComment(userId, itemId, commentDto);
    }
}
