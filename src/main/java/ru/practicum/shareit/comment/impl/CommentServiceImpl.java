package ru.practicum.shareit.comment.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        LocalDateTime time = LocalDateTime.now();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь c id = " + userId + " не найден"));

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с id " + itemId + " не найдена "));

        List<Booking> bookings =  bookingRepository.getByUserIdAndItemIdStatusApproved(userId, itemId, time);

        if (bookings.isEmpty()) {
               throw new ValidationException("Пользователь с id " + userId + "не может оставить комментарий вещи с id " + itemId);
        }
       commentDto.setCreated(time);
       Comment comment  = commentMapper.convertCommentDtoToComment(commentDto);
       comment.setAuthor(user);
       comment.setItem(item);
       return commentMapper.convertCommentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getAllCommentsByItemId(long itemId) {

       List<Comment>  comments = commentRepository.getAllByItemId(itemId);

        return comments.stream().map(commentMapper::convertCommentToCommentDto).collect(Collectors.toList());
    }


}
