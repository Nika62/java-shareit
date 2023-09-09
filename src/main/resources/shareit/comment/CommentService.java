package shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);

    List<CommentDto> getAllCommentsByItemId(long itemId);
}
